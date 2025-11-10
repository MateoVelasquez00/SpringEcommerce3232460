package com.sena.springecommerce.service;

import java.util.List;
import java.util.Optional;

import com.sena.springecommerce.model.Orden;
import com.sena.springecommerce.model.Usuario;

public interface IOrdenService {

	public Orden save(Orden orden);

	public List<Orden> findAll();

	public List<Orden> findByUsuarios(Usuario usuario);

	public Optional<Orden> findById(Integer id);

	public String generarNumeroOrden();
}
