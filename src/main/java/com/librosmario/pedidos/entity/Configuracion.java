package com.librosmario.pedidos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "co_configuracion")
public class Configuracion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "co_configuracion_k")
    private int id;

    @Column(name = "co_nombre")
    private String nombre;

    @Lob
    @Column(name = "co_logo", columnDefinition = "LONGBLOB")
    private byte[] logo;

    @Column(name = "co_logo_content_type")
    private String logoContentType;

    @Column(name = "co_direccion")
    private String direccion;

    @Column(name = "co_telefono")
    private String telefono;

    public Configuracion() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public byte[] getLogo() { return logo; }
    public void setLogo(byte[] logo) { this.logo = logo; }
    public String getLogoContentType() { return logoContentType; }
    public void setLogoContentType(String logoContentType) { this.logoContentType = logoContentType; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
