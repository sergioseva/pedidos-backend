package com.librosmario.pedidos.payload;

/** Documentos emitidos por una liquidacion, para que el front sepa que ofrecer a imprimir. */
public class LiquidacionResultadoDTO {

	private final Integer remitoRetiroId;
	private final Integer remitoVentaId;
	private final Integer reciboId;
	private final double totalTapa;
	private final double comision;
	private final double netoAPagar;

	public LiquidacionResultadoDTO(Integer remitoRetiroId, Integer remitoVentaId, Integer reciboId,
			double totalTapa, double comision, double netoAPagar) {
		this.remitoRetiroId = remitoRetiroId;
		this.remitoVentaId = remitoVentaId;
		this.reciboId = reciboId;
		this.totalTapa = totalTapa;
		this.comision = comision;
		this.netoAPagar = netoAPagar;
	}

	public Integer getRemitoRetiroId() {
		return remitoRetiroId;
	}

	public Integer getRemitoVentaId() {
		return remitoVentaId;
	}

	public Integer getReciboId() {
		return reciboId;
	}

	public double getTotalTapa() {
		return totalTapa;
	}

	public double getComision() {
		return comision;
	}

	public double getNetoAPagar() {
		return netoAPagar;
	}
}
