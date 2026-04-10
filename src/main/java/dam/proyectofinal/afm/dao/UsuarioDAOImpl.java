package dam.proyectofinal.afm.dao;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import dam.proyectofinal.afm.model.Usuario;

public class UsuarioDAOImpl  implements UsuarioDAO{
	// Lista que simula una dabatabase (esto es temporal)
	private static List<Usuario> usuariosMemoria = new ArrayList<>();
	
	// TEMPORAL
	// Se fuerza la existencia del admin 
	public UsuarioDAOImpl() {
		if (usuariosMemoria.isEmpty()) {
			Usuario adminMaestro = new Usuario();
			adminMaestro.setEmail("admin@minemanager.com");
			adminMaestro.setNickname("admin");
			adminMaestro.setPassword("1234");
			
			usuariosMemoria.add(adminMaestro);
			System.out.println("SISTEMA: Admin Maestro inicializado.");
		}
	}

	@Override
	public boolean registrar(Usuario usuario) {
		// TODO Auto-generated method stub
		// TEMPORAL
		// Se impide que alguien se registre con el admin
		if (usuario.getNickname().trim().equalsIgnoreCase("admin")) {
			return false;
		}
		
		if (existeEmail(usuario.getEmail()))
		return false;
		
		usuariosMemoria.add(usuario);
		System.out.println("Usuario guardado en memoria: " + usuario.getNickname());
		return true;
	}

	@Override
	public Usuario login(String nickname, String password) {
		// TODO Auto-generated method stub
		Usuario usuario = usuariosMemoria.stream()
				.filter(u -> u.getNickname().equals(nickname) && u.getPassword().equals(password))
				.findFirst()
				.orElse(null);	
		
		// Si las credencilaes son validas se guarda el acceso
		if (usuario != null) {
			usuario.setFechaUltimoAcceso(LocalDateTime.now());
			System.out.println("DEBUG: " + nickname + " logueado. Acceso registrado.");
		}
		
		return usuario;
	}
	
	@Override
	public boolean existeEmail(String email) {
		// TODO Auto-generated method stub
		// Se busca si algun usuario ya tiene asignado ese email
		return usuariosMemoria.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
	}
	@Override
	public List<Usuario> obtenerTodos() {
	    return new ArrayList<>(usuariosMemoria);
	}

	@Override
	public boolean eliminar(String nickname) {
	    return usuariosMemoria.removeIf(u -> u.getNickname().equals(nickname));
	}
	
	@Override
	public boolean existeNickname(String nickname) {
	    // Se obtienen todos los usuarios actuales
	    List<Usuario> usuarios = obtenerTodos();
	    // Se busca si alguno coincide (ignorando mayúsculas/minúsculas por seguridad)
	    return usuarios.stream()
	                   .anyMatch(u -> u.getNickname().equalsIgnoreCase(nickname));
	}
	
	@Override
	public int obtenerTotalUsuarios() {
		return usuariosMemoria.size(); // Esto se hace asi de manera temporal hasta implementar de forma definitiva la base de datos
	}
}
