package com.librosmario.pedidos.batch;

public class CatalogoCSV {
	public CatalogoCSV() {
		
	}
	
    String CODIGO;
    String AUTOR;
    String DESC;
    String EDI;
    String PRECIO;
    String BARRA;
    String CLASE;
    String PEDIDO;
    String TEMA;
    String OBSERVA;
    String PSTOCK;
    String MARCA;
    String RUBRO;
    String VIGENTE;
    
    
	public String getCLASE() {
		return CLASE;
	}
	public void setCLASE(String cLASE) {
		CLASE = cLASE;
	}
	public String getPEDIDO() {
		return PEDIDO;
	}
	public void setPEDIDO(String pEDIDO) {
		PEDIDO = pEDIDO;
	}
	public String getPSTOCK() {
		return PSTOCK;
	}
	public void setPSTOCK(String pSTOCK) {
		PSTOCK = pSTOCK;
	}
	public String getMARCA() {
		return MARCA;
	}
	public void setMARCA(String mARCA) {
		MARCA = mARCA;
	}
	public String getRUBRO() {
		return RUBRO;
	}
	public void setRUBRO(String rUBRO) {
		RUBRO = rUBRO;
	}
	public String getVIGENTE() {
		return VIGENTE;
	}
	public void setVIGENTE(String vIGENTE) {
		VIGENTE = vIGENTE;
	}
	public String getCODIGO() {
		return CODIGO;
	}
	public void setCODIGO(String cODIGO) {
		CODIGO = cODIGO;
	}
	public String getAUTOR() {
		return AUTOR;
	}
	public void setAUTOR(String aUTOR) {
		AUTOR = aUTOR;
	}
	public String getDESC() {
		return DESC;
	}
	public void setDESC(String dESC) {
		DESC = dESC;
	}
	public String getEDI() {
		return EDI;
	}
	public void setEDI(String eDI) {
		EDI = eDI;
	}
	public String getPRECIO() {
		return PRECIO;
	}
	public void setPRECIO(String pRECIO) {
		PRECIO = pRECIO;
	}
	public String getBARRA() {
		return BARRA;
	}
	public void setBARRA(String bARRA) {
		BARRA = bARRA;
	}
	public String getTEMA() {
		return TEMA;
	}
	public void setTEMA(String tEMA) {
		TEMA = tEMA;
	}
	public String getOBSERVA() {
		return OBSERVA;
	}
	public void setOBSERVA(String oBSERVA) {
		OBSERVA = oBSERVA;
	}
	public CatalogoCSV(String cODIGO, String aUTOR, String dESC, String eDI, String pRECIO, String bARRA, String tEMA,
			String oBSERVA) {
		super();
		CODIGO = cODIGO;
		AUTOR = aUTOR;
		DESC = dESC;
		EDI = eDI;
		PRECIO = pRECIO;
		BARRA = bARRA;
		TEMA = tEMA;
		OBSERVA = oBSERVA;
	}

}
