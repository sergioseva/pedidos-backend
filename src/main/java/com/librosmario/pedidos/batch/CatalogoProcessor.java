package com.librosmario.pedidos.batch;

import org.springframework.batch.item.ItemProcessor;

import com.librosmario.pedidos.entity.Catalogo;

public class CatalogoProcessor implements ItemProcessor<CatalogoCSV, Catalogo>{

    @Override
    public Catalogo process(final CatalogoCSV catalogo)  {

    	double precio=0;
    	String observaciones= catalogo.getOBSERVA().replaceAll("[^a-zA-Z0-9\\s']", "");
    	try {
    		precio = Double.parseDouble(catalogo.getPRECIO().replace(",","."));
    	} catch (NumberFormatException e) {
    		observaciones+= "ERROR DE IMPORTACION:Precio en 0 porque el registro contenia " + catalogo.getPRECIO();
    	}

        return new Catalogo(truncate(catalogo.getCODIGO().trim(), 255),
        												  truncate(catalogo.getAUTOR().replaceAll("[^a-zA-Z0-9\\s']", ""), 45),
        												  truncate(catalogo.getDESC().replaceAll("[^a-zA-Z0-9\\s']", ""), 250),
        												  precio,
        												  truncate(catalogo.getEDI().replaceAll("[^a-zA-Z0-9\\s']", ""), 45),
        												  truncate(catalogo.getTEMA().replaceAll("[^a-zA-Z0-9\\s']", ""), 45),
        												  truncate(catalogo.getBARRA(), 45),
        												  truncate(observaciones, 200)
        		);
    }

    private String truncate(String value, int maxLength) {
    	if (value == null || value.length() <= maxLength) {
    		return value;
    	}
    	return value.substring(0, maxLength);
    }

}
