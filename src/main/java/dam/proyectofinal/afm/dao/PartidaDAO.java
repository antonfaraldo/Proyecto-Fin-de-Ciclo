package dam.proyectofinal.afm.dao;

import java.util.List;

import dam.proyectofinal.afm.model.Nivel;
import dam.proyectofinal.afm.model.Partida;
import dam.proyectofinal.afm.model.Usuario;

public interface PartidaDAO {
	// Guardar una partida al finalizar
	void guardar(Partida partida);
	
	// Obtener todas las partidas
	List<Partida> listarTodas();
	
	// Obtener solo las partidas de un usuario
	List<Partida> listarPorUsuario(Usuario usuario);
	
	// Obtener las mejores puntuaciones 
	List<Partida> obtenerRankingTop(Nivel nivel, int limite);
	
	void eliminarPartidasPorUsuario(String nickname);

}
