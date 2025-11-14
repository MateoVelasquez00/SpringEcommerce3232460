package com.sena.springecommerce.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sena.springecommerce.model.DetalleOrden;
import com.sena.springecommerce.model.Orden;
import com.sena.springecommerce.model.Producto;
import com.sena.springecommerce.model.Usuario;
import com.sena.springecommerce.service.IDetalleOrdenService;
import com.sena.springecommerce.service.IOrdenService;
import com.sena.springecommerce.service.IProductoService;
import com.sena.springecommerce.service.IUsuarioService;

@RestController
@RequestMapping("/apiordenes")
public class APIOrdenController {

	@Autowired
	private IDetalleOrdenService detalleService;

	@Autowired
	private IOrdenService ordenService;

	@Autowired
	private IUsuarioService userService;

	@Autowired
	private IProductoService productService;

	private List<DetalleOrden> listaTemporal = new ArrayList<>();

	private Usuario userTemporal = null;

	// Endpoint GET para obtener todas las ordenes
	@GetMapping("/list")
	public List<Orden> getAllOrdenes() {
		return ordenService.findAll();
	}

	// Endpoint GET para obtener orden por ID
	@GetMapping("/orden/{id}")
	public ResponseEntity<Orden> getOrdenId(@PathVariable Integer id) {
		Optional<Orden> orden = ordenService.findById(id);
		return orden.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());

	}

	// Endpoind para selecionar el usuario que realizara la orden con ID
	@PostMapping("/userOrden/id/{idUser}")
	public ResponseEntity<?> usuarioOrden(@PathVariable Integer idUser) {

		Optional<Usuario> userOpt = userService.findById(idUser);
		if (userOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario con ese ID no encontrado");
		}

		userTemporal = userOpt.get();

		return ResponseEntity.ok("Usuario con ID " + userTemporal.getId() + " | Nombre:" + userTemporal.getNombre());
	}

	// Enpoint para selecionar usuario que realizara que la orden por EMAIL
	@PostMapping("/userOrden/email/{email}")
	public ResponseEntity<?> usuarioOrdenEmail(@PathVariable String email) {

		Optional<Usuario> userOpt = userService.findByEmail(email);
		if (userOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario con ese Email no encontrado");
		}

		userTemporal = userOpt.get();

		return ResponseEntity
				.ok("Usuario con Email: " + userTemporal.getEmail() + " | Nombre:" + userTemporal.getNombre());
	}

	// POST para agregar producto a la lista temporal
	@PostMapping("/agregar")
	public ResponseEntity<?> agregarProducto(@RequestParam(required = false) Integer id,
			@RequestParam(required = false) String nombre, @RequestParam Integer cantidad) {

		// Validacion del usuario temporal
		if (userTemporal == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Debes seleccionar un usuario antes de agregar la orden");
		}

		Optional<Producto> productoOpt = Optional.empty();

		// Buscador por ID producto
		if (id != null) {
			productoOpt = productService.get(id);
		}

		// Buscador por el Nombre del producto
		if (!productoOpt.isPresent() && nombre != null) {
			productoOpt = productService.findAll().stream().filter(p -> p.getNombre().equalsIgnoreCase(nombre)).findFirst();
		}
		
		if (!productoOpt.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
		}

		Producto producto = productoOpt.get();
		
		//Validacion del Stock
		if (producto.getCantidad() < cantidad.intValue()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Stock insuficiente para: " + producto.getNombre());

		}
		// Crear el DetalleOrden temporal
		DetalleOrden detalle = new DetalleOrden();
		detalle.setProducto(producto);
		detalle.setNombre(producto.getNombre());
		detalle.setCantidad(cantidad);
		detalle.setPrecio(producto.getPrecio());
		detalle.setTotal(producto.getPrecio() * cantidad);

		listaTemporal.add(detalle);
		return ResponseEntity.ok(listaTemporal);

	}

	// POST confirma la compra
	@PostMapping("/confirmar")
	public ResponseEntity<?> confirmarOrden() {

		// Valida que haya un usuario selecionado
		if (userTemporal == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Debe selecccionar un usuario antes de confirmar");
		}

		// Valida que haya una lista temporal
		if (listaTemporal.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No hay productos agregados");
		}
		// Crear la Orden
		Orden orden = new Orden();
		orden.setNumero(ordenService.generarNumeroOrden());
		orden.setFechacreacion(new Date());

		double total = listaTemporal.stream().mapToDouble(DetalleOrden::getTotal).sum();

		orden.setTotal(total);
		orden.setUsuario(userTemporal);

		Orden nuevaOrden = ordenService.save(orden);

		// Procesar cada uno de los detalles
		for (DetalleOrden d : listaTemporal) {

			Optional<Producto> pOpt = productService.get(d.getProducto().getId());
			if (pOpt.isPresent()) {
				Producto p = pOpt.get();

				// Actualiza la cantidad despues de confirmar el pedido
				int stockActual = p.getCantidad();
				int cantidadPedida = d.getCantidad();

				if (stockActual < cantidadPedida) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("Stock insuficente para: " + p.getNombre());
				}

				p.setCantidad(stockActual - cantidadPedida);
				productService.update(p);
			}

			// Guarda el DetalleOrden en la db despues de confirmar
			d.setOrden(nuevaOrden);
			detalleService.save(d);
		}

		// Limpia los datos para volver hacer otra Orden
		listaTemporal.clear();
		userTemporal = null;
		return ResponseEntity.ok("Orden creada correctamente con ID: " + nuevaOrden.getId());

	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteOrden(@PathVariable Integer id) {
		Optional<Orden> orden = ordenService.findById(id);
		if (!orden.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		ordenService.delete(id);
		return ResponseEntity.ok().build();
	}
}