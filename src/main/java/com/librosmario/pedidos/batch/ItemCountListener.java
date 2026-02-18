package com.librosmario.pedidos.batch;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
 
public class ItemCountListener implements ChunkListener {
     
    @Override
    public void beforeChunk(ChunkContext context) {
    }
 
    @Override
    public void afterChunk(ChunkContext context) {
         
        long leidosCount = context.getStepContext().getStepExecution().getReadCount();
        long escritosCount = context.getStepContext().getStepExecution().getWriteCount();
        long salteadosCount = context.getStepContext().getStepExecution().getSkipCount();
        System.out.println("leidosCount: " + leidosCount);
        System.out.println("escritosCount: " + escritosCount);
        System.out.println("salteadosCount: " + salteadosCount);
        
    }
     
    @Override
    public void afterChunkError(ChunkContext context) {
    }
}