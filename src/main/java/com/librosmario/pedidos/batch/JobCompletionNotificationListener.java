package com.librosmario.pedidos.batch;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

	private static final String CREADOR_STAGING = "luongo_bulk_new";
	private static final String CREADOR_FINAL = "luongo_bulk";

	private final JdbcTemplate jdbcTemplate;

	private static final Logger logger = LogManager.getLogger(JobCompletionNotificationListener.class);

	@Autowired
	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		// Clean up any leftover staging records from a previous failed import
		logger.info("Cleaning up staging records before import");
		jdbcTemplate.update("DELETE FROM cg_catalogo WHERE cg_creador=?", CREADOR_STAGING);
	}

	@Override
	public void afterJob(JobExecution jobExecution) {

		logger.info("Job finished with status: " + jobExecution.getStatus());

		//save statistics
		String query="INSERT INTO `librosmario`.`bt_batchstatistics` " +
				"(`bt_proceso`,`bt_starttime`,`bt_endtime`,`bt_registros`,`bt_errores`) " +
				"VALUES (?,?,?,?,?)";
		jdbcTemplate.execute(query,new PreparedStatementCallback<Boolean>(){
		    @Override
		    public Boolean doInPreparedStatement(PreparedStatement ps)
		            throws SQLException, DataAccessException {

				long leidos = 0;
				long escritos=0;
				long errores = 0;
				for (StepExecution step : jobExecution.getStepExecutions()) {
					leidos = leidos + step.getReadCount();
					escritos= escritos + step.getWriteCount();
					errores = errores + step.getSkipCount();
				}
				System.out.println("cant reads:"+ leidos);
				System.out.println("cant escritos:"+ escritos);
				System.out.println("cant errores:"+ errores);
		        ps.setString(1,"ImportLuongo");
		        ps.setTimestamp(2, Timestamp.valueOf(jobExecution.getStartTime()));
		        ps.setTimestamp(3, Timestamp.valueOf(jobExecution.getEndTime()));
		        ps.setLong(4, leidos);
		        ps.setLong(5, errores);
		        return ps.execute();
		    }
		    });

		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			logger.info("Import successful, replacing old records");
			// Delete old records and promote staging records
			jdbcTemplate.update("DELETE FROM cg_catalogo WHERE cg_creador=?", CREADOR_FINAL);
			jdbcTemplate.update("UPDATE cg_catalogo SET cg_creador=? WHERE cg_creador=?", CREADOR_FINAL, CREADOR_STAGING);
			System.out.println("============ JOB FINISHED ============ ");
		} else {
			logger.info("Import failed, removing staging records");
			// Clean up partial import
			jdbcTemplate.update("DELETE FROM cg_catalogo WHERE cg_creador=?", CREADOR_STAGING);
		}
	}
}
