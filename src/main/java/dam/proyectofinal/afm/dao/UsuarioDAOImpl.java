package dam.proyectofinal.afm.dao;

import java.util.List;
import java.util.ArrayList;

import dam.proyectofinal.afm.model.Usuario;

public class UsuarioDAOImpl  implements UsuarioDAO{
	// Lista que simula una dabatabase (esto es temporal)
	private static List<Usuario> usuariosMemoria = new ArrayList<>();

	@Override
	public boolean registrar(Usuario usuario) {
		// TODO Auto-generated method stub
		if (existeEmail(usuario.getEmail()))
		return false;
		
		usuariosMemoria.add(usuario);
		System.out.println("Usuario guardado en memoria: " + usuario.getNickname());
		return true;
	}

	@Override
	public Usuario login(String nickname, String password) {
		// TODO Auto-generated method stub
		return usuariosMemoria.stream()
				.filter(u -> u.getNickname().equals(nickname) && u.getPassword().equals(password))
				.findFirst()
				.orElse(null);	
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

}
