package com.librosmario.pedidos.batch;

import com.librosmario.pedidos.entity.Catalogo;
import org.springframework.batch.item.ItemProcessor;

public class NewCatalogoProcessor implements ItemProcessor<NewCatalogoCSV, Catalogo>{

    @Override
    public Catalogo process(final NewCatalogoCSV catalogo) throws Exception {

    	double precio=0;
    	String observaciones = "";
    	try {
    		precio = Double.parseDouble(catalogo.getPRECIO().replace(".","").replace(",",".").replace("$",""));
    	} catch (NumberFormatException e) {
    		observaciones+= "ERROR DE IMPORTACION:Precio en 0 porque el registro contenia " + catalogo.getPRECIO();
    	}

        return new Catalogo(truncate(catalogo.getCODIGO().trim(), 255),
        												  truncate(catalogo.getAUTOR().replaceAll("[^a-zA-Z0-9\\s']", ""), 45),
        												  truncate(catalogo.getDESCR().replaceAll("[^a-zA-Z0-9\\s']", ""), 250),
        												  precio,
        												  truncate(catalogo.getEDITORIAL().replaceAll("[^a-zA-Z0-9\\s']", ""), 45),
        												  "",
        												  truncate(catalogo.getISBN(), 45),
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
