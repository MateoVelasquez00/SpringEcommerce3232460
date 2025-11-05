package com.sena.springecommerce.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class DetalleOrden {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Anotacion JPA
	private Integer id;
	private String nombre;
	private String telefono;
	private String email;
	private String direccion;
	private String rol;
	private String password;

	// Relaciones en DB
	@OneToMany(mappedBy = "usuario")
	private List<Producto> productos;

	@OneToMany(mappedBy = "usuario")
	private List<Orden> ordenes;

}