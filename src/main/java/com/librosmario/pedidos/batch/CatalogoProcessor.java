package com.librosmario.pedidos.batch;

import org.springframework.batch.item.ItemProcessor;

import com.librosmario.pedidos.entity.Catalogo;

public class CatalogoProcessor implements ItemProcessor<CatalogoCSV, Catalogo>{
	
    @Override
    public Catalogo process(final CatalogoCSV catalogo) throws Exception {
    	
    	double precio=0;
    	String observaciones= catalogo.getOBSERVA().replaceAll("[^a-zA-Z0-9\\s']", "");
    	try {
    		precio = Double.valueOf(catalogo.getPRECIO().replace(",","."));
    	} catch (NumberFormatException e) {
    		observaciones+= "ERROR DE IMPRTACION:Precio en 0 porque el registro contenia " + catalogo.getPRECIO();
    	}
    	
        final Catalogo transformedCatalogo = new Catalogo(Integer.parseInt(catalogo.getCODIGO().trim()),
        												  catalogo.getAUTOR().replaceAll("[^a-zA-Z0-9\\s']", ""),
        												  catalogo.getDESC().replaceAll("[^a-zA-Z0-9\\s']", ""),
        												  precio,
        												  catalogo.getEDI().replaceAll("[^a-zA-Z0-9\\s']", ""),
        												  catalogo.getTEMA().replaceAll("[^a-zA-Z0-9\\s']", ""),
        												  catalogo.getBARRA(),
        												  observaciones
        		);

//        log.info("Converting (" + AnimeDTO + ") into (" + transformedAnimeDTO + ")");

        return transformedCatalogo;
    }


}
