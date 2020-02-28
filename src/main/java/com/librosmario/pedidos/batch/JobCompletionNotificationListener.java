package com.librosmario.pedidos.batch;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport{

	private final JdbcTemplate jdbcTemplate;
	
	private static final Logger logger = LogManager.getLogger(JobCompletionNotificationListener.class);

	@Autowired
	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	
	
	public void afterJob(JobExecution jobExecution) {
		
		logger.info("Job finished");

		//save statistics
		String query="INSERT INTO `librosmario`.`bt_batchstatistics` " + 
				"(`bt_proceso`,`bt_starttime`,`bt_endtime`,`bt_registros`,`bt_errores`) " + 
				"VALUES (?,?,?,?,?)";
		jdbcTemplate.execute(query,new PreparedStatementCallback<Boolean>(){  
		    @Override  
		    public Boolean doInPreparedStatement(PreparedStatement ps)  
		            throws SQLException, DataAccessException {  
		              
				int leidos = 0;
				int escritos=0;
				int errores = 0; 
				for (StepExecution step : jobExecution.getStepExecutions()) {
					leidos = leidos + step.getReadCount();
					escritos= escritos + step.getWriteCount();
					errores = errores + step.getSkipCount();
				}
				System.out.println("cant reads:"+ leidos);
				System.out.println("cant escritos:"+ escritos);
				System.out.println("cant errores:"+ errores);
		        ps.setString(1,"ImportLuongo");  
		        ps.setTimestamp(2, new Timestamp(jobExecution.getStartTime().getTime()) );  
		        ps.setTimestamp(3, new Timestamp(jobExecution.getEndTime().getTime()));
		        ps.setInt(4, leidos);
		        ps.setInt(5, errores);
		        return ps.execute();  
		    }  
		    });
		
				
				
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			System.out.println("============ JOB FINISHED ============ ");
			//remove old records
			query=" delete from librosmario.cg_catalogo WHERE cg_creador='luongo' and cg_inputdate < ? "; 
			jdbcTemplate.update(query,new Timestamp(jobExecution.getStartTime().getTime()));
			
		}
	}
}
