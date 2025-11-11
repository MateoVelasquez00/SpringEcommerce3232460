package com.sena.springecommerce.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sena.springecommerce.model.Usuario;
import com.sena.springecommerce.service.IUsuarioService;

@RestController
@RequestMapping("/apiusuarios")
public class APIUsuarioController {
	
	@Autowired
	private IUsuarioService userService;
	
	// Endpoint GET para obtener todos los productos
		@GetMapping("/list")
		public List<Usuario> getAllUsers() {
			return userService.findAll();
		}

		// Endpoint GET para obtener un Usuario por ID
		@GetMapping("/user/{id}")
		public ResponseEntity<Usuario> getUserById(@PathVariable Integer id) {
			Optional<Usuario> usuario = userService.get(id);
			return usuario.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
		}

		// Endpoint POST para crear un nuevo Usuario
		@PostMapping("/create")
		public ResponseEntity<Usuario> createUser(@RequestBody Usuario usuario) {
			usuario.setRol("USER");
			Usuario savedUser = userService.save(usuario);
			return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
		}

		// Endpoint PUT para actualizar un Usuario
		@PutMapping("/update/{id}")
		public ResponseEntity<Usuario> undateProduct(@PathVariable Integer id, @RequestBody Usuario userDetails) {
			Optional<Usuario> usuario = userService.get(id);
			if (!usuario.isPresent()) {
				return ResponseEntity.notFound().build();
			}
			Usuario existingUser = usuario.get();
			existingUser.setNombre(userDetails.getNombre());
			existingUser.setTelefono(userDetails.getTelefono());
			existingUser.setEmail(userDetails.getEmail());
			existingUser.setDireccion(userDetails.getDireccion());
			existingUser.setPassword(userDetails.getPassword());
			
			userService.update(existingUser);
			return ResponseEntity.ok(existingUser);
		}
		//Endpoint DELETE pata eliminar unproducto
		@DeleteMapping("/delete/{id}")
		public ResponseEntity<?> deleteProduct(@PathVariable Integer id){
			Optional<Usuario> usuario = userService.get(id);
			if (!usuario.isPresent()) {
				return ResponseEntity.notFound().build();	
				}
			userService.delete(id);
			return ResponseEntity.ok().build();
		}
	}
