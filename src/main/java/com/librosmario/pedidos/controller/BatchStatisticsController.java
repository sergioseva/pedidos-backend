package com.librosmario.pedidos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.librosmario.pedidos.entity.BatchStatistics;
import com.librosmario.pedidos.exception.ResourceNotFoundException;
import com.librosmario.pedidos.repository.BatchStatisticsRepository;

@RestController
public class BatchStatisticsController {

    @Autowired
    BatchStatisticsRepository repository;

    @GetMapping("/batchstatistics")
    public ResponseEntity<Page<BatchStatistics>> getAll(
            @PageableDefault(size = 10, sort = "starttime", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(repository.findAll(pageable));
    }

    @GetMapping("/batchstatistics/{id}")
    public ResponseEntity<BatchStatistics> getById(@PathVariable Integer id) {
        BatchStatistics stats = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BatchStatistics", "id", id));
        return ResponseEntity.ok(stats);
    }
}
