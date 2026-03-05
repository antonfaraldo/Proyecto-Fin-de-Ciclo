package dam.proyectofinal.afm.dao;

import dam.proyectofinal.afm.model.Usuario;

public interface UsuarioDAO {
	boolean registrar(Usuario usuario);
	Usuario login(String nickname, String password);
	boolean existeEmail(String email);

}
