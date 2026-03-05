package dam.proyectofinal.afm.dao;

import java.util.List;

import dam.proyectofinal.afm.model.Logro;
import dam.proyectofinal.afm.model.Usuario;

public interface LogroDAO {
	// La vitrina de trofeos
	List<Logro> listarTodos();
	
	// Medallas del Usuario actual
	List<Logro> obtenerLogrosPorUsuario(Usuario usuario);
	
	// Guardar cada trofeo nuevo
	boolean asignarLogroAUsuario(Usuario usuario, Logro logro);
	
	// Comprobar si el usuario ya tiene el trofeo
	boolean tieneUsuarioLogro(Usuario usuario, String nombreLogro);
}
