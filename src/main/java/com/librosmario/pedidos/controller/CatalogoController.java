package com.librosmario.pedidos.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.librosmario.pedidos.entity.Catalogo;
import com.librosmario.pedidos.exception.BadRequestException;
import com.librosmario.pedidos.exception.ConflictException;
import com.librosmario.pedidos.service.CatalogoService;

@RestController
public class CatalogoController {
	private static final Logger logger = LogManager.getLogger(CatalogoController.class);

	/** Zip local file header: every .xlsx (OOXML) starts with these bytes. */
	private static final byte[] OOXML_SIGNATURE = { 0x50, 0x4B, 0x03, 0x04 };
	/** OLE2 compound document header: a legacy .xls starts with these bytes. */
	private static final byte[] OLE2_SIGNATURE = { (byte) 0xD0, (byte) 0xCF, 0x11, (byte) 0xE0,
			(byte) 0xA1, (byte) 0xB1, 0x1A, (byte) 0xE1 };

	/** A number written plainly with a dot decimal separator, e.g. 24094.5 — never 1,234.50 or 150,50. */
	private static final Pattern PLAIN_DECIMAL = Pattern.compile("-?\\d+\\.\\d+");

	/** Serializes imports: they share a temp file and the staging marker in cg_catalogo. */
	private final ReentrantLock importLock = new ReentrantLock();

	@Autowired
	CatalogoService service;

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job job;

    @GetMapping( value = "/catalogos/search/findByAny")
    public ResponseEntity<Page<Catalogo>> findByAny(@RequestParam(required = false) String parametro,
    												@RequestParam(required = false) String descripcion,
    												@RequestParam(required = false) String autor,
    												@RequestParam(required = false) String editorial,
    												@RequestParam(required = false) String isbn,
    												@RequestParam(required = false) String observaciones,
    												@PageableDefault(size = 20, sort = "descripcion", direction = Sort.Direction.ASC) Pageable pageable) {

    	logger.info("findByAny parametro='{}', descripcion='{}', autor='{}', editorial='{}', isbn='{}', observaciones='{}', page={}, size={}", parametro, descripcion, autor, editorial, isbn, observaciones, pageable.getPageNumber(), pageable.getPageSize());
    	Page<Catalogo> catalogos = service.findByAny(parametro, descripcion, autor, editorial, isbn, observaciones, pageable);
    	return ResponseEntity.ok(catalogos);

    }

    /**
     * Exact ISBN lookup for the till's barcode reader. Two segments, so it does not collide with
     * the Spring Data REST export's /catalogos/{id}. Returns 404 when unknown, which the front
     * uses to offer manual entry rather than blocking the sale.
     */
    @GetMapping( value = "/catalogos/isbn/{isbn}")
    public ResponseEntity<Catalogo> findByIsbn(@PathVariable String isbn) {
    	return ResponseEntity.ok(service.findByIsbn(isbn));
    }

    @GetMapping( value = "/catalogos/search/findByAll")
    public ResponseEntity<Page<Catalogo>> findByAll(@RequestParam(required = false) String libro,
    												@RequestParam(required = false) String isbn,
    												@RequestParam(required = false) String autor,
    												@RequestParam(required = false) String editorial,
    																																				@RequestParam(required = false) String tema,
    												@PageableDefault(size = 20, sort = "descripcion", direction = Sort.Direction.ASC) Pageable pageable) {

    	Page<Catalogo> catalogos = service.findByAll(libro, autor, editorial, tema, isbn, pageable);
    	return ResponseEntity.ok(catalogos);

    }

    @PostMapping( value = "/catalogos/import")
    public  ResponseEntity<Boolean> importCatalogo(@RequestParam("file") MultipartFile file, @RequestParam(defaultValue = "true") boolean deleteOldRecords) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {

    	logger.info("Import catalogo request: filename='{}', size={} bytes", file.getOriginalFilename(), file.getSize());

    	if (file.isEmpty()) {
    		throw new BadRequestException("El archivo esta vacio");
    	}

    	// Imports share a fixed temp file and a single staging marker (cg_creador='luongo_bulk_new',
    	// wiped wholesale in beforeJob), so two running at once would destroy each other's rows.
    	if (!importLock.tryLock()) {
    		logger.warn("Import rejected: another import is already in progress");
    		throw new ConflictException("Ya hay una importacion en curso, espere a que termine");
    	}
    	try {
    		return runImport(file, deleteOldRecords);
    	} finally {
    		importLock.unlock();
    	}
    }

    private ResponseEntity<Boolean> runImport(MultipartFile file, boolean deleteOldRecords) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {

    	Path rootLocation = Paths.get(System.getProperty("java.io.tmpdir"));
    	Path csvFile = rootLocation.resolve("luongo.csv");
    	long rows;

    	try {
    		Files.createDirectories(rootLocation);
    		String originalFilename = file.getOriginalFilename();
    		if (originalFilename != null && (originalFilename.endsWith(".xlsx") || originalFilename.endsWith(".xls"))) {
    			Path xlsxTmp = rootLocation.resolve("luongo_upload.xlsx");
    			try {
    				Files.copy(file.getInputStream(), xlsxTmp, StandardCopyOption.REPLACE_EXISTING);
    				requireOoxml(xlsxTmp, originalFilename);
    				rows = convertXlsxToCsv(xlsxTmp.toFile(), csvFile);
    			} finally {
    				Files.deleteIfExists(xlsxTmp);
    			}
    		} else {
    			Files.copy(file.getInputStream(), csvFile, StandardCopyOption.REPLACE_EXISTING);
    			rows = countCsvRows(csvFile);
    		}
    	} catch (BadRequestException e) {
    		throw e;
    	} catch (Exception e) {
    		logger.error("FAIL to store/convert the file luongo", e);
    		throw new RuntimeException("FAIL to store the file luongo", e);
    	}

    	// A file that yields no rows would let the job complete and wipe the existing
    	// catalog in JobCompletionNotificationListener. Refuse before launching.
    	if (rows == 0) {
    		logger.error("Import aborted: no data rows found in '{}'", file.getOriginalFilename());
    		throw new BadRequestException("El archivo no contiene registros para importar");
    	}
    	logger.info("Import file ready with {} data rows", rows);

    	JobParameters jobParameters = new JobParametersBuilder()
    			.addLong("time", System.currentTimeMillis())
    			.addString("fileName", file.getOriginalFilename())
    			.addString("deleteOldRecords", String.valueOf(deleteOldRecords))
    			.toJobParameters();
    	JobExecution jobExecution = jobLauncher.run(job, jobParameters);
    	boolean completed = jobExecution.getStatus() == BatchStatus.COMPLETED;
    	logger.info("Batch import job finished with status {}", jobExecution.getStatus());
    	if (!completed) {
    		return ResponseEntity.internalServerError().body(false);
    	}
    	return ResponseEntity.ok(true);
    }

    /**
     * POI renders General-format numbers the way Excel would in a narrow column: past ~11 digits it
     * switches to scientific notation, so a 13-digit ISBN arrives as "9.78987E+12" with its last
     * digits already gone. The spreadsheet holds the exact value, so render whole General numbers
     * from the raw value instead.
     *
     * Deliberately limited to whole numbers. Identifiers like the ISBN are always whole, while the
     * Luongo prices are General too and some carry floating point noise (6477.27000000000044): those
     * keep POI's rounding, which is what the downstream price parsing already expects.
     */
    private DataFormatter plainNumberFormatter() {
    	return new DataFormatter() {
    		@Override
    		public String formatRawCellContents(double value, int formatIndex, String formatString,
    				boolean use1904Windowing) {
    			if (isGeneralFormat(formatString) && isWholeNumber(value)) {
    				return BigDecimal.valueOf(value).setScale(0, RoundingMode.UNNECESSARY).toPlainString();
    			}
    			return super.formatRawCellContents(value, formatIndex, formatString, use1904Windowing);
    		}

    		private boolean isGeneralFormat(String formatString) {
    			return formatString == null || formatString.isEmpty()
    					|| "General".equalsIgnoreCase(formatString) || "@".equals(formatString);
    		}

    		private boolean isWholeNumber(double value) {
    			return !Double.isNaN(value) && !Double.isInfinite(value) && value == Math.rint(value);
    		}
    	};
    }

    /**
     * The spreadsheet parser only understands OOXML (.xlsx), which is a zip. A real .xls is an
     * OLE2 document and would fail deep inside POI with an unrelated message, so reject it here.
     * The check reads the magic bytes rather than the extension: a mislabeled file is common and
     * what matters is the actual format, not what it is called.
     */
    private void requireOoxml(Path uploaded, String originalFilename) throws Exception {
    	byte[] signature = new byte[8];
    	int read;
    	try (InputStream in = Files.newInputStream(uploaded)) {
    		read = in.readNBytes(signature, 0, signature.length);
    	}

    	if (read >= OOXML_SIGNATURE.length && Arrays.equals(
    			Arrays.copyOf(signature, OOXML_SIGNATURE.length), OOXML_SIGNATURE)) {
    		return;
    	}

    	if (read >= OLE2_SIGNATURE.length && Arrays.equals(
    			Arrays.copyOf(signature, OLE2_SIGNATURE.length), OLE2_SIGNATURE)) {
    		logger.error("Import rejected: '{}' is a legacy .xls (OLE2) file", originalFilename);
    		throw new BadRequestException(
    				"El formato .xls no esta soportado, guarde el archivo como .xlsx e intente nuevamente");
    	}

    	logger.error("Import rejected: '{}' is not a valid xlsx file", originalFilename);
    	throw new BadRequestException("El archivo no es un .xlsx valido");
    }

    /**
     * POI hands back a fractional price with a dot separator ("24094.5"), but NewCatalogoProcessor
     * reads prices as European and strips dots as thousands separators, turning that into 240945.
     * Emit the comma form the processor expects, rounding away the floating point noise the
     * spreadsheet carries (6477.27000000000044 -> "6477,27").
     *
     * Only an unambiguous dot decimal is rewritten. Whole numbers ("28300") and prices already
     * written with a comma ("150,50") are passed through untouched — the processor reads both
     * correctly, and guessing at anything else risks turning a thousands separator into a decimal.
     */
    private static String formatPrecio(String value) {
    	if (value == null || value.isEmpty()) {
    		return "";
    	}
    	if (!PLAIN_DECIMAL.matcher(value).matches()) {
    		return value;
    	}
    	return new BigDecimal(value)
    			.setScale(2, RoundingMode.HALF_UP)
    			.stripTrailingZeros()
    			.toPlainString()
    			.replace('.', ',');
    }

    private long countCsvRows(Path csvFile) throws Exception {
    	try (Stream<String> lines = Files.lines(csvFile)) {
    		return lines.filter(line -> !line.isBlank()).count();
    	}
    }

    /**
     * Converts an XLSX file to semicolon-delimited CSV using SAX streaming (constant memory).
     * XLSX columns: Isbn, Titulo, Autor, Coleccion, Sello, Clase, Precio
     * CSV columns:  CODIGO;DESCR;AUTOR;EDITORIAL;ISBN;PRECIO;CLASE
     *
     * @return the number of data rows written (header excluded)
     */
    private long convertXlsxToCsv(File xlsxFile, Path csvOutput) throws Exception {
    	AtomicLong rowsWritten = new AtomicLong();
    	try (OPCPackage pkg = OPCPackage.open(xlsxFile);
    		 BufferedWriter writer = Files.newBufferedWriter(csvOutput)) {

    		ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
    		XSSFReader reader = new XSSFReader(pkg);
    		StylesTable styles = reader.getStylesTable();

    		logger.info("XLSX opened, starting SAX parse");
    		try (InputStream sheetStream = reader.getSheetsData().next()) {
    			SheetContentsHandler handler = new SheetContentsHandler() {
    				private final String[] cells = new String[7];
    				private int currentRow = -1;

    				@Override
    				public void startRow(int rowNum) {
    					currentRow = rowNum;
                        Arrays.fill(cells, "");
    				}

    				@Override
    				public void endRow(int rowNum) {
    					if (currentRow == 0) return; // skip header
    					try {
    						String isbn = sanitize(cells[0]);
    						String titulo = sanitize(cells[1]);
    						String autor = sanitize(cells[2]);
    						// cells[3] (Coleccion) is skipped
    						String sello = sanitize(cells[4]);
    						String clase = sanitize(cells[5]);
    						String precio = formatPrecio(sanitize(cells[6]));
    						writer.write(String.join(";", isbn, titulo, autor, sello, isbn, precio, clase));
    						writer.newLine();
    						long rowCount = rowsWritten.incrementAndGet();
    						if (rowCount <= 3) {
    							logger.info("CSV row {}: {}", rowCount, String.join(";", isbn, titulo, autor, sello, isbn, precio, clase));
    						}
    					} catch (Exception e) {
    						throw new RuntimeException("Error writing CSV row " + rowNum, e);
    					}
    				}

    				@Override
    				public void cell(String cellReference, String formattedValue, XSSFComment comment) {
    					int col = cellColumnIndex(cellReference);
    					if (col >= 0 && col < cells.length) {
    						cells[col] = formattedValue != null ? formattedValue : "";
    					}
    				}

    				@Override
    				public void headerFooter(String text, boolean isHeader, String tagName) {
    				}

    				private String sanitize(String value) {
    					if (value == null || value.isEmpty()) return "";
    					// Remove trailing .0 from numeric strings (e.g. "9789878.0" -> "9789878")
    					if (value.endsWith(".0")) {
    						try {
    							double d = Double.parseDouble(value);
    							if (d == Math.floor(d) && !Double.isInfinite(d)) {
    								return String.valueOf((long) d);
    							}
    						} catch (NumberFormatException ignored) {
    						}
    					}
    					return value.replaceAll("[\\r\\n;]+", " ").replaceAll("\\p{Z}+", " ").trim();
    				}

    				private int cellColumnIndex(String cellReference) {
    					int col = 0;
    					for (int i = 0; i < cellReference.length(); i++) {
    						char c = cellReference.charAt(i);
    						if (Character.isLetter(c)) {
    							col = col * 26 + (Character.toUpperCase(c) - 'A' + 1);
    						} else {
    							break;
    						}
    					}
    					return col - 1; // 0-based
    				}
    			};

    			SAXParserFactory factory = SAXParserFactory.newInstance();
    			factory.setNamespaceAware(true);
    			SAXParser saxParser = factory.newSAXParser();
    			XMLReader xmlReader = saxParser.getXMLReader();
    			xmlReader.setContentHandler(new XSSFSheetXMLHandler(styles, strings, handler, plainNumberFormatter(), false));
    			xmlReader.parse(new InputSource(sheetStream));
    		}
    	}
    	logger.info("CSV conversion complete, {} rows, output file size: {} bytes", rowsWritten.get(), Files.size(csvOutput));
    	return rowsWritten.get();
    }

}
