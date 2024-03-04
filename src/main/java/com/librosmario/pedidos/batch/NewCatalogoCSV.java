package com.librosmario.pedidos.batch;

public class NewCatalogoCSV {
	public NewCatalogoCSV() {

	}
    String CODIGO;
    String DESCR;
    String AUTOR;
    String EDITORIAL;
    String ISBN;
    String PRECIO;
    String CLASE;

	public NewCatalogoCSV(String CODIGO, String DESCR, String AUTOR, String EDITORIAL, String ISBN, String PRECIO, String CLASE) {
		this.CODIGO = CODIGO;
		this.DESCR = DESCR;
		this.AUTOR = AUTOR;
		this.EDITORIAL = EDITORIAL;
		this.ISBN = ISBN;
		this.PRECIO = PRECIO;
		this.CLASE = CLASE;
	}

	public String getCODIGO() {
		return CODIGO;
	}

	public void setCODIGO(String CODIGO) {
		this.CODIGO = CODIGO;
	}

	public String getDESCR() {
		return DESCR;
	}

	public void setDESCR(String DESCR) {
		this.DESCR = DESCR;
	}

	public String getAUTOR() {
		return AUTOR;
	}

	public void setAUTOR(String AUTOR) {
		this.AUTOR = AUTOR;
	}

	public String getEDITORIAL() {
		return EDITORIAL;
	}

	public void setEDITORIAL(String EDITORIAL) {
		this.EDITORIAL = EDITORIAL;
	}

	public String getISBN() {
		return ISBN;
	}

	public void setISBN(String ISBN) {
		this.ISBN = ISBN;
	}

	public String getPRECIO() {
		return PRECIO;
	}

	public void setPRECIO(String PRECIO) {
		this.PRECIO = PRECIO;
	}

	public String getCLASE() {
		return CLASE;
	}

	public void setCLASE(String CLASE) {
		this.CLASE = CLASE;
	}
}
