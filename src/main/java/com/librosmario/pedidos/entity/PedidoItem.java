package com.librosmario.pedidos.entity;


import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name="pi_pedido_item")
public class PedidoItem {
	  @Id
	  @GeneratedValue(strategy=GenerationType.IDENTITY)
	  @Column(name="pi_pedido_item_k")
	  private Integer id;
	  
	  @JsonIgnore
	  @ManyToOne(fetch = FetchType.LAZY)
	  @JoinColumn(name="pi_pedido_pe") 
	  private Pedido pedido;
	  
//	  @JsonIgnore
//	  @ManyToOne( cascade= {CascadeType.PERSIST, CascadeType.MERGE,
//				 CascadeType.DETACH, CascadeType.REFRESH})
//	  @JoinColumn(name="pi_catalogo_cg") 
//	  private Catalogo catalogo;
//	  
	  @Column(name="pi_cantidad")
	  private Integer cantidad;
	  @Column(name="pi_nombre_libro")
	  private String libro;
	  @Column(name="pi_autor")
	  private String autor;
	  @Column(name="pi_editorial")
	  private String editorial;
	  @Column(name="pi_isbn")
	  private String isbn;
	  @Column(name="pi_precio")
	  private Double precio;
	  

	  @OneToOne(fetch = FetchType.LAZY)
	  @JoinColumn(name = "pi_editorial_ed")
	  private Distribuidora pedidoAeditorial;
	  
	  @Column(name="pi_pendiente")
	  private boolean pendiente;
	  @Column(name="pi_ensucursal")
	  private boolean enSucursal=false;
	  @Column(name="pi_retirado")
	  private boolean retirado=false;
	  @Column(name="pi_fecha_retiro")
	  private LocalDateTime fechaRetiro;
	 


	  @ManyToMany(fetch=FetchType.LAZY)
	  @JoinTable(
				name="pdpi_pedido_distribuidora_item",
				joinColumns=@JoinColumn(name="pdpi_pedido_item_pi"),
				inverseJoinColumns=@JoinColumn(name="pdpi_pedido_a_distribuidora_pd")
				)	
	  private List<PedidoDistribuidora> pedidosADistribuidoras;
	
	  
	public PedidoItem() {

	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public Pedido getPedido() {
		return pedido;
	}


	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}




	public Integer getCantidad() {
		return cantidad;
	}


	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}


	public String getLibro() {
		return libro;
	}


	public void setLibro(String libro) {
		this.libro = libro;
	}


	public String getAutor() {
		return autor;
	}


	public void setAutor(String autor) {
		this.autor = autor;
	}





	public void setEditorial(String editorial) {
		this.editorial = editorial;
	}


	public String getIsbn() {
		return isbn;
	}


	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}


	public Double getPrecio() {
		return precio;
	}


	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public Distribuidora getPedidoAeditorial() {
		return pedidoAeditorial;
	}


	public void setPedidoAeditorial(Distribuidora pedidoAeditorial) {
		this.pedidoAeditorial = pedidoAeditorial;
	}


	public String getEditorial() {
		return editorial;
	}


	public boolean getPendiente() {
		return pendiente;
	}


	public void setPendiente(boolean pendiente) {
		this.pendiente = pendiente;
	}


	public List<PedidoDistribuidora> getPedidosADistribuidoras() {
		return pedidosADistribuidoras;
	}


	public void setPedidosADistribuidoras(List<PedidoDistribuidora> pedidosADistribuidoras) {
		this.pedidosADistribuidoras = pedidosADistribuidoras;
	}
	
	 public boolean isEnSucursal() {
			return enSucursal;
		}


		public void setEnSucursal(boolean enSucursal) {
			this.enSucursal = enSucursal;
		}


		public boolean isRetirado() {
			return retirado;
		}


		public void setRetirado(boolean retirado) {
			this.retirado = retirado;
		}


		public LocalDateTime getFechaRetiro() {
			return fechaRetiro;
		}


		public void setFechaRetiro(LocalDateTime fechaRetiro) {
			this.fechaRetiro = fechaRetiro;
		}



	

}
