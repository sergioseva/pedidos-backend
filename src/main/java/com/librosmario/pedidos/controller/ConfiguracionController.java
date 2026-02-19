package com.librosmario.pedidos.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.librosmario.pedidos.entity.Configuracion;
import com.librosmario.pedidos.repository.ConfiguracionRepository;

@RestController
public class ConfiguracionController {

    @Autowired
    ConfiguracionRepository repository;

    @GetMapping("/configuracion")
    public ResponseEntity<Map<String, Object>> getConfiguracion() {
        Optional<Configuracion> config = repository.findAll().stream().findFirst();
        if (config.isPresent()) {
            Configuracion c = config.get();
            Map<String, Object> response = new HashMap<>();
            response.put("id", c.getId());
            response.put("nombre", c.getNombre());
            response.put("direccion", c.getDireccion());
            response.put("telefono", c.getTelefono());
            response.put("hasLogo", c.getLogo() != null && c.getLogo().length > 0);
            return ResponseEntity.ok(response);
        }
        Map<String, Object> empty = new HashMap<>();
        empty.put("id", null);
        empty.put("nombre", null);
        empty.put("direccion", null);
        empty.put("telefono", null);
        empty.put("hasLogo", false);
        return ResponseEntity.ok(empty);
    }

    @PutMapping(value = "/configuracion", consumes = {"application/json"})
    public ResponseEntity<Map<String, Object>> updateConfiguracion(@RequestBody Configuracion configuracion) {
        Optional<Configuracion> existing = repository.findAll().stream().findFirst();
        Configuracion current;
        HttpStatus status;
        if (existing.isPresent()) {
            current = existing.get();
            current.setNombre(configuracion.getNombre());
            current.setDireccion(configuracion.getDireccion());
            current.setTelefono(configuracion.getTelefono());
            status = HttpStatus.OK;
        } else {
            current = new Configuracion();
            current.setNombre(configuracion.getNombre());
            current.setDireccion(configuracion.getDireccion());
            current.setTelefono(configuracion.getTelefono());
            status = HttpStatus.CREATED;
        }
        Configuracion saved = repository.save(current);
        Map<String, Object> response = new HashMap<>();
        response.put("id", saved.getId());
        response.put("nombre", saved.getNombre());
        response.put("direccion", saved.getDireccion());
        response.put("telefono", saved.getTelefono());
        response.put("hasLogo", saved.getLogo() != null && saved.getLogo().length > 0);
        return new ResponseEntity<>(response, status);
    }

    @PostMapping("/configuracion/logo")
    public ResponseEntity<Map<String, Object>> uploadLogo(@RequestParam("file") MultipartFile file) {
        try {
            Optional<Configuracion> existing = repository.findAll().stream().findFirst();
            Configuracion current;
            if (existing.isPresent()) {
                current = existing.get();
            } else {
                current = new Configuracion();
            }
            current.setLogo(file.getBytes());
            current.setLogoContentType(file.getContentType());
            Configuracion saved = repository.save(current);
            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("nombre", saved.getNombre());
            response.put("hasLogo", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/configuracion/logo")
    public ResponseEntity<byte[]> getLogo() {
        Optional<Configuracion> config = repository.findAll().stream().findFirst();
        if (config.isPresent() && config.get().getLogo() != null && config.get().getLogo().length > 0) {
            Configuracion c = config.get();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                    c.getLogoContentType() != null ? c.getLogoContentType() : "image/png"));
            return new ResponseEntity<>(c.getLogo(), headers, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/configuracion/logo")
    public ResponseEntity<Void> deleteLogo() {
        Optional<Configuracion> config = repository.findAll().stream().findFirst();
        if (config.isPresent()) {
            Configuracion c = config.get();
            c.setLogo(null);
            c.setLogoContentType(null);
            repository.save(c);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
