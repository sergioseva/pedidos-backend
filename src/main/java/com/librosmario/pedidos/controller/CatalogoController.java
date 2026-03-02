package com.librosmario.pedidos.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.springframework.batch.core.Job;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.librosmario.pedidos.entity.Catalogo;
import com.librosmario.pedidos.service.CatalogoService;

@RestController
public class CatalogoController {
	private static final Logger logger = LogManager.getLogger(CatalogoController.class);

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
    public  ResponseEntity<Boolean> importCatalogo(@RequestParam("file") MultipartFile file) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {

    	logger.info("Import catalogo request: filename='{}', size={} bytes", file.getOriginalFilename(), file.getSize());
    	Path rootLocation = Paths.get(System.getProperty("java.io.tmpdir"));
    	Path csvFile = rootLocation.resolve("luongo.csv");

    	try {
    		Files.createDirectories(rootLocation);
    		String originalFilename = file.getOriginalFilename();
    		if (originalFilename != null && (originalFilename.endsWith(".xlsx") || originalFilename.endsWith(".xls"))) {
    			Path xlsxTmp = rootLocation.resolve("luongo_upload.xlsx");
    			Files.copy(file.getInputStream(), xlsxTmp, StandardCopyOption.REPLACE_EXISTING);
    			convertXlsxToCsv(xlsxTmp.toFile(), csvFile);
    			Files.deleteIfExists(xlsxTmp);
    		} else {
    			Files.copy(file.getInputStream(), csvFile, StandardCopyOption.REPLACE_EXISTING);
    		}
    	} catch (Exception e) {
    		logger.error("FAIL to store/convert the file luongo", e);
    		throw new RuntimeException("FAIL to store the file luongo", e);
    	}

    	JobParameters jobParameters = new JobParametersBuilder()
    			.addLong("time", System.currentTimeMillis())
    			.toJobParameters();
    	jobLauncher.run(job, jobParameters);
    	logger.info("Batch import job launched");
    	return ResponseEntity.ok(true);
    }

    /**
     * Converts an XLSX file to semicolon-delimited CSV using SAX streaming (constant memory).
     * XLSX columns: Isbn, Titulo, Autor, Coleccion, Sello, Clase, Precio
     * CSV columns:  CODIGO;DESCR;AUTOR;EDITORIAL;ISBN;PRECIO;CLASE
     */
    private void convertXlsxToCsv(File xlsxFile, Path csvOutput) throws Exception {
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
    				private int rowCount = 0;

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
    						String precio = sanitize(cells[6]);
    						writer.write(String.join(";", isbn, titulo, autor, sello, isbn, precio, clase));
    						writer.newLine();
    						rowCount++;
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
    			xmlReader.setContentHandler(new XSSFSheetXMLHandler(styles, strings, handler, false));
    			xmlReader.parse(new InputSource(sheetStream));
    		}
    	}
    	logger.info("CSV conversion complete, output file size: {} bytes", Files.size(csvOutput));
    }

}
