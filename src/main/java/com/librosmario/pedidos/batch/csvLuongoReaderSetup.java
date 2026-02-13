package com.librosmario.pedidos.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;

import com.librosmario.pedidos.entity.Catalogo;

//@Configuration
@EnableBatchProcessing
public class csvLuongoReaderSetup {
	
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;
    
	@Bean
	public FlatFileItemReader < CatalogoCSV > csvCatalogoReader() {
	    FlatFileItemReader < CatalogoCSV > reader = new FlatFileItemReader < > ();
	    reader.setResource(new FileSystemResource(System.getProperty("java.io.tmpdir") + "/luongo.csv"));
	    reader.setLineMapper(new DefaultLineMapper <CatalogoCSV> () {
	        {
            setLineTokenizer(new DelimitedLineTokenizer(";") {
                {
                    setNames("CODIGO",
                            "AUTOR",
                            "DESC",
                            "EDI",
                            "PRECIO",
                            "BARRA",
                            "CLASE",
                            "PEDIDO",
                            "TEMA",
                            "OBSERVA",
                            "PSTOCK",
                            "MARCA",
                            "RUBRO",
                            "VIGENTE");
                }
            });
            setFieldSetMapper(new BeanWrapperFieldSetMapper < CatalogoCSV > () {
                {
                    setTargetType(CatalogoCSV.class);
                }
            });
        }
    });
    return reader;
}
	
	@Bean
	ItemProcessor<CatalogoCSV, Catalogo> csvCatalogoProcessor() {
		return new CatalogoProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<Catalogo> csvCatalogoWriter() {
		 JdbcBatchItemWriter<Catalogo> csvCatalogoWriter = new JdbcBatchItemWriter<>();
		 csvCatalogoWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
		 csvCatalogoWriter.setSql("INSERT INTO cg_catalogo (cg_codigo_luongo, cg_autor, cg_descripcion,cg_precio,cg_editorial,cg_tema,cg_isbn,cg_observaciones,cg_creador,cg_inputdate)"
		 		+ " VALUES (:codigoLuongo, :autor, :descripcion, :precio, :editorial, :tema , :isbn, :observaciones, 'luongo_bulk_new', now() )");
		 csvCatalogoWriter.setDataSource(dataSource);
	     return csvCatalogoWriter;
	}

	@Bean
	public Step csvFileToDatabaseStep() {
		return stepBuilderFactory.get("csvFileToDatabaseStep")
				.allowStartIfComplete(true)
				.<CatalogoCSV, Catalogo>chunk(100)
				.reader(csvCatalogoReader())
				.processor(csvCatalogoProcessor())
				.writer(csvCatalogoWriter())
				.faultTolerant()
				.skipLimit(100)
				.skip(Exception.class)
				.listener(new ImportSkipListener())
				//.listener(listener())
				.build();
	}
	
	@Bean
	public Job readCSVFilesJob(JobCompletionNotificationListener listener) {
	    return jobBuilderFactory
	            .get("readCSVFilesJob")
	            .incrementer(new RunIdIncrementer())
	            .listener(listener)
	            .start(csvFileToDatabaseStep())
	            .build();
	}
	
	@Bean
	public ItemCountListener listener() {
	    return new ItemCountListener();
	}


}
