package com.sena.springecommerce.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sena.springecommerce.model.Orden;
import com.sena.springecommerce.model.Usuario;
import com.sena.springecommerce.repository.IOrdenRepository;

@Service
public class OrdenServiceImplement implements IOrdenService {

	@Autowired
	private IOrdenRepository ordenRepository;

	@Override
	public Orden save(Orden orden) {
		// TODO Auto-generated method stub
		return ordenRepository.save(orden);
	}

	@Override
	public List<Orden> findAll() {
		// TODO Auto-generated method stub
		return ordenRepository.findAll();
	}

	@Override
	public List<Orden> findByUsuarios(Usuario usuario) {
		// TODO Auto-generated method stub
		return ordenRepository.findByUsuario(usuario);
	}

	@Override
	public Optional<Orden> findById(Integer id) {
		// TODO Auto-generated method stub
		return ordenRepository.findById(id);
	}

	@Override
	public String generarNumeroOrden() {
		// TODO Auto-generated method stub
		int numero = 0;
		String numeroConcatenado = "";
		List<Orden> ordenes = findAll();
		List<Integer> numeros = new ArrayList<>();
		// Funciones java 8
		// Variable Anonima
		ordenes.stream() .filter(o -> o.getNumero() != null && !o.getNumero().isEmpty()) //Evita nulos
        .forEach(o -> numeros.add(Integer.parseInt(o.getNumero())));
		
		if (ordenes.isEmpty()){
			numero = 1;
		} else {
			numero = numeros.stream().max(Integer::compare).get();
			numero++;
		}

		if (numero < 10) {
			numeroConcatenado = "000000000000" + String.valueOf(numero);

		} else if (numero < 100) {
			numeroConcatenado = "00000000000" + String.valueOf(numero);

		} else if (numero < 1000) {
			numeroConcatenado = "0000000000" + String.valueOf(numero);
		}
		return numeroConcatenado;
	}

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub
		ordenRepository.deleteById(id);
	}

}
