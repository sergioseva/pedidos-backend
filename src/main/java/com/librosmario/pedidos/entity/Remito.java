package com.librosmario.pedidos.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="re_remito")
public class Remito {

	 /** Devolucion de libros a una distribuidora: el destinatario es {@link #re_distribuidora_ed}. */
	 public static final String TIPO_DEVOLUCION = "DEVOLUCION";

	 /** Entrega de libros en consignacion a un punto de venta: el destinatario es {@link #re_comercio_cm}. */
	 public static final String TIPO_CONSIGNACION = "CONSIGNACION";

	 /** Retiro de libros que el comercio no vendio y vuelven a la libreria. Descuenta saldo. */
	 public static final String TIPO_RETIRO = "RETIRO";

	 /** Libros que el comercio vendio y pasa a deber. Descuenta saldo y genera cobro. */
	 public static final String TIPO_VENTA_CONSIGNACION = "VENTA_CONSIGNACION";

	 /** Los tres tipos cuyo destinatario es un comercio. */
	 public static boolean esDeComercio(String tipo) {
		 return TIPO_CONSIGNACION.equals(tipo) || TIPO_RETIRO.equals(tipo)
				 || TIPO_VENTA_CONSIGNACION.equals(tipo);
	 }

	 @Id
	 @GeneratedValue(strategy=GenerationType.IDENTITY)
	 @Column(name="re_remito_k")
	 Integer re_remito_k;
	 
	 @Column(name="re_fecha")
	 Date re_fecha;
	 
	 @ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE,
			 CascadeType.DETACH, CascadeType.REFRESH})
	 @JoinColumn(name="re_distribuidora_ed")
	 Distribuidora re_distribuidora_ed;	
	 
	 /**
	  * Destinatario de un remito de consignacion. Exactamente uno de {@code re_distribuidora_ed} y
	  * {@code re_comercio_cm} esta seteado, segun {@link #re_tipo}.
	  */
	 @ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE,
			 CascadeType.DETACH, CascadeType.REFRESH})
	 @JoinColumn(name="re_comercio_cm")
	 Comercio re_comercio_cm;

	 /**
	  * {@link #TIPO_DEVOLUCION} o {@link #TIPO_CONSIGNACION}. Los remitos anteriores a la
	  * consignacion son todos devoluciones, de ahi el default.
	  */
	 @Column(name="re_tipo", length=20)
	 String re_tipo = TIPO_DEVOLUCION;

	 /**
	  * Comision del comercio (0-100) vigente al liquidar, solo en remitos de venta. Se congela
	  * aca en vez de leerse de Comercio para que renegociar el porcentaje no reescriba la plata
	  * de una liquidacion ya emitida.
	  */
	 @Column(name="re_comision")
	 Double re_comision;

	 @Column(name="re_observaciones")
	 String re_observaciones;
	 

	@OneToMany(mappedBy = "ri_remito_re", cascade= {CascadeType.PERSIST, CascadeType.MERGE,
			 CascadeType.DETACH, CascadeType.REFRESH})
	private List<RemitoItem> items;

	/**
	 * Recibo del pago, solo en remitos de venta y solo si el comercio ya pago. Viaja con el
	 * remito para que la consulta pueda distinguir de un vistazo lo cobrado de lo pendiente:
	 * sin esto no habria manera de encontrar los remitos impagos para emitirles el recibo.
	 */
	@OneToOne(mappedBy = "rc_remito_re")
	private Recibo recibo;
		
	 public String getRe_observaciones() {
		return re_observaciones;
	}
	public void setRe_observaciones(String re_observaciones) {
		this.re_observaciones = re_observaciones;
	}

	 
	public List<RemitoItem> getItems() {
		return items;
	}
	public void setItems(List<RemitoItem> items) {
		this.items = items;
	}
	public Integer getRe_remito_k() {
		return re_remito_k;
	}
	public void setRe_remito_k(Integer re_remito_k) {
		this.re_remito_k = re_remito_k;
	}
	public Date getRe_fecha() {
		return re_fecha;
	}
	public void setRe_fecha(Date re_fecha) {
		this.re_fecha = re_fecha;
	}
	public Distribuidora getRe_distribuidora_ed() {
		return re_distribuidora_ed;
	}
	public void setRe_distribuidora_ed(Distribuidora re_distribuidora_ed) {
		this.re_distribuidora_ed = re_distribuidora_ed;
	}
	public Comercio getRe_comercio_cm() {
		return re_comercio_cm;
	}
	public void setRe_comercio_cm(Comercio re_comercio_cm) {
		this.re_comercio_cm = re_comercio_cm;
	}
	public String getRe_tipo() {
		return re_tipo;
	}
	public void setRe_tipo(String re_tipo) {
		this.re_tipo = re_tipo;
	}
	public Double getRe_comision() {
		return re_comision;
	}
	public void setRe_comision(Double re_comision) {
		this.re_comision = re_comision;
	}

	public Recibo getRecibo() {
		return recibo;
	}
	public void setRecibo(Recibo recibo) {
		this.recibo = recibo;
	}

	/** Un remito de venta sin recibo es plata que el comercio todavia debe. */
	public boolean isPagado() {
		return recibo != null;
	}

	/** Lo que el comercio efectivamente paga: total de tapa menos la comision congelada. */
	public double getNetoAPagar() {
		double comision = re_comision == null ? 0d : re_comision;
		return getTotal() * (100d - comision) / 100d;
	}

	/** Total del remito, para no repetir la suma en cada consumidor. */
	public double getTotal() {
		if (items == null) {
			return 0d;
		}
		return items.stream()
				.mapToDouble(i -> (i.getRi_cantidad() == null ? 0 : i.getRi_cantidad())
						* (i.getRi_precio() == null ? 0d : i.getRi_precio()))
				.sum();
	}


}
