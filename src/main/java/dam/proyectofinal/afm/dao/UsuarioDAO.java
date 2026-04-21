package dam.proyectofinal.afm.dao;

import java.time.LocalDateTime;
import java.util.List;

import dam.proyectofinal.afm.model.Usuario;

public interface UsuarioDAO {
	boolean registrar(Usuario usuario);
	Usuario login(String nickname, String password);
	boolean existeEmail(String email);
	boolean existeNickname(String nickname);

	List<Usuario> obtenerTodos();
	boolean eliminar(String nickname);
	int obtenerTotalUsuarios();
	
	Usuario buscarPorEmail(String email);
	boolean guardarTokenRecuperacion(String email, String token, LocalDateTime expiracion);
	boolean actualizarPassword(String email, String nuevaPassword);
}
