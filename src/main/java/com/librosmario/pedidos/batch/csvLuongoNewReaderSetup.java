package com.librosmario.pedidos.batch;

import com.librosmario.pedidos.entity.Catalogo;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class csvLuongoNewReaderSetup {
	
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;
    
    @Value("${pedidos.luongo.path}")
    private String filePath;
    
	@Bean
	public FlatFileItemReader < NewCatalogoCSV > csvCatalogoReader() {
	    FlatFileItemReader < NewCatalogoCSV > reader = new FlatFileItemReader<>();
	    reader.setResource(new FileSystemResource(filePath + "luongo.csv"));
	    reader.setLineMapper(new DefaultLineMapper <NewCatalogoCSV> () {
	        {
            setLineTokenizer(new DelimitedLineTokenizer(";") {
                {
					setNames("CODIGO",
                            "DESCR",
                            "AUTOR",
                            "EDITORIAL",
                            "ISBN",
                            "PRECIO",
                            "CLASE");
                }
            });
            setFieldSetMapper(new BeanWrapperFieldSetMapper < NewCatalogoCSV > () {
                {
                    setTargetType(NewCatalogoCSV.class);
                }
            });
        }
    });
    return reader;
}
	
	@Bean
	ItemProcessor<NewCatalogoCSV, Catalogo> csvCatalogoProcessor() {
		return new NewCatalogoProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<Catalogo> csvCatalogoWriter() {
		 JdbcBatchItemWriter<Catalogo> csvCatalogoWriter = new JdbcBatchItemWriter<>();
		 csvCatalogoWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
		 csvCatalogoWriter.setSql("INSERT INTO cg_catalogo (cg_codigo_luongo, cg_autor, cg_descripcion,cg_precio,cg_editorial,cg_tema,cg_isbn,cg_observaciones,cg_creador,cg_inputdate)"
		 		+ " VALUES (:codigoLuongo, :autor, :descripcion, :precio, :editorial, :tema , :isbn, :observaciones, 'luongo', now() )");
		 csvCatalogoWriter.setDataSource(dataSource);
	     return csvCatalogoWriter;
	}

	@Bean
	public Step csvFileToDatabaseStep() {
		return stepBuilderFactory.get("csvFileToDatabaseStep")
				.allowStartIfComplete(true)
				.<NewCatalogoCSV, Catalogo>chunk(100)
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
