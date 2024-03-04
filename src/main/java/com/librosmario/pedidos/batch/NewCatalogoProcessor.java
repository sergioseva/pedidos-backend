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

        //        log.info("Converting (" + AnimeDTO + ") into (" + transformedAnimeDTO + ")");

        return new Catalogo(Integer.parseInt(catalogo.getCODIGO().trim()),
        												  catalogo.getAUTOR().replaceAll("[^a-zA-Z0-9\\s']", ""),
        												  catalogo.getDESCR().replaceAll("[^a-zA-Z0-9\\s']", ""),
        												  precio,
        												  catalogo.getEDITORIAL().replaceAll("[^a-zA-Z0-9\\s']", ""),
        												  "",
        												  catalogo.getISBN(),
        												  observaciones
        		);
    }


}
