package com.librosmario.pedidos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cr_configuracion_remito")
public class ConfiguracionRemito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cr_configuracion_remito_k")
    private int id;

    @Column(name = "cr_remitente")
    private String remitente;

    public ConfiguracionRemito() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }
}
