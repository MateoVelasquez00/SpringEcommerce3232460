package com.sena.springecommerce.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ordenes")
public class Orden {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Anotacion JPA

	private Integer id;
	private String numero; 
	private Date fechacreacion;
	private Double total;
	
	@ManyToOne
	private Usuario usuario;
	
	@OneToMany(mappedBy = "orden")
	private List<DetalleOrden> ordenes;

	public Orden() {
	
	}

	public Orden(Integer id, String numero, Date fechacreacion, Double total) {
		super();
		this.id = id;
		this.numero = numero;
		this.fechacreacion = fechacreacion;
		this.total = total;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Date getFechacreacion() {
		return fechacreacion;
	}

	public void setFechacreacion(Date fechacreacion) {
		this.fechacreacion = fechacreacion;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<DetalleOrden> getOrdenes() {
		return ordenes;
	}

	public void setOrdenes(List<DetalleOrden> ordenes) {
		this.ordenes = ordenes;
	}
	

	@Override
	public String toString() {
		return "Orden [id=" + id + ", numero=" + numero + ", fechacreacion=" + fechacreacion + ", total=" + total + "]";
	}
	
	
	
}
