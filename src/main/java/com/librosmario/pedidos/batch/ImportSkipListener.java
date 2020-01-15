package com.librosmario.pedidos.batch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.SkipListener;

import com.librosmario.pedidos.entity.Catalogo;

public class ImportSkipListener<T> implements SkipListener<T, Catalogo> {
	
	private static final Logger logger = LogManager.getLogger(ImportSkipListener.class);
	
	
	@Override
	public void onSkipInRead(Throwable t) {
		logger.error("Error leyendo registro:"+ t.getMessage());
	}

	@Override
	public void onSkipInWrite(Catalogo item, Throwable t) {

		logger.error("Error escribiendo registro:"+ item.toString() + t.getMessage());
	}

	@Override
	public void onSkipInProcess(T item, Throwable t) {
		logger.error("Error procesando registro:"+ item.toString() + t.getMessage());
	}
	

}
