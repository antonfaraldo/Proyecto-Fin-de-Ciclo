package dam.proyectofinal.afm.dao;

import java.util.List;

import dam.proyectofinal.afm.model.Usuario;

public interface UsuarioDAO {
	boolean registrar(Usuario usuario);
	Usuario login(String nickname, String password);
	boolean existeEmail(String email);
	boolean existeNickname(String nickname);

	List<Usuario> obtenerTodos();
	boolean eliminar(String nickname);
}
