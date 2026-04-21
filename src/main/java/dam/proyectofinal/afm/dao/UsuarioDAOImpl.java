package dam.proyectofinal.afm.dao;

import java.util.List; 
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.mindrot.jbcrypt.BCrypt;
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
			adminMaestro.setPassword(BCrypt.hashpw("1234", BCrypt.gensalt()));
			adminMaestro.setActivo(true); // El admin siempre activo
			usuariosMemoria.add(adminMaestro);
			System.out.println("SISTEMA: Admin Maestro inicializado.");
		}
	}

	@Override
	public boolean registrar(Usuario usuario) {
		// TODO Auto-generated method stub
		// TEMPORAL
		// Se impide que alguien se registre con el admin
		if (usuario.getNickname().trim().equalsIgnoreCase("admin") || existeEmail(usuario.getEmail())) {
			return false;
		}
		// Cifrar antes de guardar la contraseña
		usuario.setPassword(BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt()));
		
		// El usuario se registra como inactivo por defecto
		usuario.setActivo(false);
		
		usuariosMemoria.add(usuario);
		System.out.println("Usuario guardado en memoria: " + usuario.getNickname());
		return true;
	}

	@Override
	public Usuario login(String nickname, String password) {
		// TODO Auto-generated method stub
		Usuario usuario = usuariosMemoria.stream()
				.filter(u -> u.getNickname().equals(nickname))
				.findFirst()
				.orElse(null);	
		
		// SE verifica si ul usuario existe y si la contraseña coincide con el hash
		if (usuario != null && BCrypt.checkpw(password, usuario.getPassword())) {
			// Se bloquea el login si la cuenta no ha sido activada
			if (!usuario.isActivo()) {
				System.out.println("DEBUG: Intento de login en cuenta no activa: " + nickname);
				return null;
			}
			
			// Si las credencilaes son validas se guarda el acceso
			usuario.setFechaUltimoAcceso(LocalDateTime.now());
			System.out.println("DEBUG: " + nickname + " logueado. Acceso registrado.");
			return usuario;
		}
		
		return null;
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
	
	@Override
	public Usuario buscarPorEmail(String email) {
		return usuariosMemoria.stream()
				.filter(u -> u.getEmail().equalsIgnoreCase(email))
				.findFirst()
				.orElse(null);
	}
	
	@Override
	public boolean guardarTokenRecuperacion(String email, String token, LocalDateTime expiracion) {
		Usuario u = buscarPorEmail(email);
		if (u != null) {
			u.setTokenRecuperacion(token);
			u.setFechaExpiracionToken(expiracion);
			return true;
		}
		return false;
	}

	@Override
	public boolean actualizarPassword(String email, String nuevaPassword) {
		// TODO Auto-generated method stub
		Usuario u = buscarPorEmail(email);
		if (u != null) {
			// Se cifra la nueva contraseña
			String hash = BCrypt.hashpw(nuevaPassword, BCrypt.gensalt());
			u.setPassword(hash);
			u.setTokenRecuperacion(null); // Se limpia el token tras el uso
			return true;
		}
		return false;
	}
	
}
