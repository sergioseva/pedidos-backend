package com.librosmario.pedidos.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bt_batchstatistics")
public class BatchStatistics {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;

	@Column(name = "bt_proceso")
	String proceso;

	@Column(name = "bt_starttime")
	Date starttime;

	@Column(name = "bt_endtime")
	Date endtime;

	@Column(name = "bt_registros")
	Integer registros;

	@Column(name = "bt_errores")
	Integer errores;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProceso() {
		return proceso;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public Date getStarttime() {
		return starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	public Date getEndtime() {
		return endtime;
	}

	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}

	public Integer getRegistros() {
		return registros;
	}

	public void setRegistros(Integer registros) {
		this.registros = registros;
	}

	public Integer getErrores() {
		return errores;
	}

	public void setErrores(Integer errores) {
		this.errores = errores;
	}
}
