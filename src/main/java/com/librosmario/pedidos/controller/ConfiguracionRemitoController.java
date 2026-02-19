package com.librosmario.pedidos.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.librosmario.pedidos.entity.ConfiguracionRemito;
import com.librosmario.pedidos.repository.ConfiguracionRemitoRepository;

@RestController
public class ConfiguracionRemitoController {

    @Autowired
    ConfiguracionRemitoRepository repository;

    @GetMapping("/configuracion-remito")
    public ResponseEntity<ConfiguracionRemito> getConfiguracion() {
        Optional<ConfiguracionRemito> config = repository.findAll().stream().findFirst();
        return config.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/configuracion-remito", consumes = {"application/json"})
    public ResponseEntity<ConfiguracionRemito> updateConfiguracion(@RequestBody ConfiguracionRemito configuracion) {
        Optional<ConfiguracionRemito> existing = repository.findAll().stream().findFirst();
        if (existing.isPresent()) {
            ConfiguracionRemito current = existing.get();
            current.setRemitente(configuracion.getRemitente());
            return ResponseEntity.ok(repository.save(current));
        } else {
            ConfiguracionRemito saved = repository.save(configuracion);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        }
    }
}
