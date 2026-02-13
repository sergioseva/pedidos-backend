package com.librosmario.pedidos.controller;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
    public ResponseEntity<List<Catalogo>> findByAny(@Param("parametro") String parametro) {

    	logger.info("find by any");
    	List<Catalogo> catalogos=service.findByAny(parametro);
    	return ResponseEntity.ok(catalogos);

    }

    @GetMapping( value = "/catalogos/search/findByAll")
    public ResponseEntity<List<Catalogo>> findByAll(@Param("libro") String libro,
    												@Param("isbn") 	String isbn,
    												@Param("autor") String autor,
    												@Param("editorial") String editorial,
    												@Param("tema") String tema
    		) {

    	List<Catalogo> catalogos=service.findByAll(libro,autor,editorial,tema,isbn);
    	return ResponseEntity.ok(catalogos);

    }

    @PostMapping( value = "/catalogos/import")
    public  ResponseEntity<Boolean> importCatalogo(@RequestParam("file") MultipartFile file) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {

    	Path rootLocation = Paths.get(System.getProperty("java.io.tmpdir"));
    	Path csvFile = rootLocation.resolve("luongo.csv");

    	try {
    		Files.createDirectories(rootLocation);
    		String originalFilename = file.getOriginalFilename();
    		if (originalFilename != null && (originalFilename.endsWith(".xlsx") || originalFilename.endsWith(".xls"))) {
    			convertXlsxToCsv(file.getInputStream(), csvFile);
    		} else {
    			Files.copy(file.getInputStream(), csvFile, StandardCopyOption.REPLACE_EXISTING);
    		}
    	} catch (Exception e) {
    		throw new RuntimeException("FAIL to store the file luongo");
    	}

    	JobParameters jobParameters = new JobParametersBuilder()
    			.addLong("time", System.currentTimeMillis())
    			.toJobParameters();
    	jobLauncher.run(job, jobParameters);
    	return ResponseEntity.ok(true);
    }

    /**
     * Converts an XLSX/XLS file to semicolon-delimited CSV matching the batch reader format.
     * XLSX columns: Isbn, Titulo, Autor, Coleccion, Sello, Clase, Precio
     * CSV columns:  CODIGO;DESCR;AUTOR;EDITORIAL;ISBN;PRECIO;CLASE
     */
    private void convertXlsxToCsv(InputStream xlsxInput, Path csvOutput) throws Exception {
    	try (Workbook workbook = WorkbookFactory.create(xlsxInput);
    		 BufferedWriter writer = Files.newBufferedWriter(csvOutput)) {

    		Sheet sheet = workbook.getSheetAt(0);
    		boolean firstRow = true;

    		for (Row row : sheet) {
    			if (firstRow) {
    				firstRow = false;
    				continue; // skip header row
    			}

    			String isbn = getCellStringValue(row.getCell(0));     // Isbn -> CODIGO & ISBN
    			String titulo = getCellStringValue(row.getCell(1));   // Titulo -> DESCR
    			String autor = getCellStringValue(row.getCell(2));    // Autor -> AUTOR
    			// column 3 (Coleccion) is skipped
    			String sello = getCellStringValue(row.getCell(4));    // Sello -> EDITORIAL
    			String clase = getCellStringValue(row.getCell(5));    // Clase -> CLASE
    			String precio = getCellStringValue(row.getCell(6));   // Precio -> PRECIO

    			// Format: CODIGO;DESCR;AUTOR;EDITORIAL;ISBN;PRECIO;CLASE
    			writer.write(String.join(";", isbn, titulo, autor, sello, isbn, precio, clase));
    			writer.newLine();
    		}
    	}
    }

    private String getCellStringValue(Cell cell) {
    	if (cell == null) {
    		return "";
    	}
    	if (cell.getCellType() == CellType.NUMERIC) {
    		double val = cell.getNumericCellValue();
    		if (val == Math.floor(val) && !Double.isInfinite(val)) {
    			return String.valueOf((long) val);
    		}
    		return String.valueOf(val);
    	}
    	return cell.getStringCellValue().replaceAll("[\\r\\n;]+", " ").replaceAll("\\p{Z}+", " ").trim();
    }

}
