package dam.proyectofinal.afm.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import dam.proyectofinal.afm.model.Nivel;
import dam.proyectofinal.afm.model.Partida;
import dam.proyectofinal.afm.model.Usuario;
import dam.proyectofinal.afm.util.CSVManager;

public class PartidaDAOImpl implements PartidaDAO{
	// Lista para simular la database
	private static List<Partida> historialPartidas = new ArrayList<>();
	
	// contructor
	public PartidaDAOImpl() {
		if (historialPartidas.isEmpty()) {
			List<Partida> cargadas = CSVManager.importarPartidas(null);
			if (cargadas != null) {
				historialPartidas.addAll(cargadas);
				System.out.println("Historial cargado desde CSV: " + cargadas.size() + " partidas"); 
			}
		}
	}
	
	@Override
	public void guardar(Partida partida) {
		// TODO Auto-generated method stub
		historialPartidas.add(partida);
		System.out.println("Partida registrada en el DAO");
	}

	@Override
	public List<Partida> listarTodas() {
		// TODO Auto-generated method stub
		return new ArrayList<>(historialPartidas);
	}

	@Override
	public List<Partida> listarPorUsuario(Usuario usuario) {
		// TODO Auto-generated method stub
		return historialPartidas.stream()
				.filter(p -> p.getUsuario().getNickname().equals(usuario.getNickname()))
				.collect(Collectors.toList());
	}

	@Override
	public List<Partida> obtenerRankingTop(Nivel nivel, int limite) {
		// TODO Auto-generated method stub
		return historialPartidas.stream()
				.filter(Partida::isVictoria) // Solo las ganadas
				.filter(p -> p.getDificultad().getNivel() == nivel)
				.sorted((p1, p2) -> Integer.compare(p1.getTiempoSegundos(), p2.getTiempoSegundos()))
				.limit(limite)
				.collect(Collectors.toList());
	}

}
