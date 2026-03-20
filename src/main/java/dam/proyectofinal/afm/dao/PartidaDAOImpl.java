package dam.proyectofinal.afm.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	public Map<String, Object> obtenerEstadisticasCompletas(Usuario usuario) {
		List<Partida> partidas = listarPorUsuario(usuario);
		// Se filtra para ignorar el nivel personalizado
		List<Partida> partidasValidas = partidas.stream().filter(p -> p.getDificultad().getNivel() != Nivel.PERSONALIZADO).collect(Collectors.toList());
		
		Map<String, Object> stats = new HashMap<>();
		
		// Victorias Totales y ratio
		long total = partidasValidas.size();
		long victorias = partidasValidas.stream().filter(Partida::isVictoria).count();
		double ratioGlobal = total > 0 ? (double) victorias / total * 100 : 0;
		
		// Tiempo total en segundos 
		int tiempoTotal = partidasValidas.stream().mapToInt(Partida::getTiempoSegundos).sum();
		
		// Nivel favorito
		Nivel favorito = partidasValidas.stream()
				.collect(Collectors.groupingBy(p -> p.getDificultad().getNivel(), Collectors.counting()))
				.entrySet().stream()
				.max(Map.Entry.comparingByValue())
				.map(Map.Entry::getKey)
				.orElse(null);
		
		// Porcentaje de victorias por nivel
		Map<Nivel, Double> porcentajesPorNivel = new HashMap<>();
		for (Nivel n : new Nivel[] {Nivel.FACIL, Nivel.MEDIO, Nivel.DIFICIL, Nivel.CONTRARRELOJ}) {
			long totalN = partidasValidas.stream().filter(p -> p.getDificultad().getNivel() == n).count();
			long vicN = partidasValidas.stream().filter(p -> p.getDificultad().getNivel() == n && p.isVictoria()).count();
			porcentajesPorNivel.put(n, totalN > 0 ? (double) vicN / totalN * 100 : 0.0);
		}
		
		stats.put("total", total);
	    stats.put("victorias", victorias);
	    stats.put("ratioGlobal", ratioGlobal);
	    stats.put("tiempoTotal", tiempoTotal);
	    stats.put("favorito", favorito);
	    stats.put("porcentajesNivel", porcentajesPorNivel);
	    
	    return stats;
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
				.sorted((p1, p2) -> {
					// Si es contrarreloj, el emjro es el que tiene mayor tiempo
					if (nivel == Nivel.CONTRARRELOJ) {
						return Integer.compare(p2.getTiempoSegundos(), p1.getTiempoSegundos());
					}
					return Integer.compare(p1.getTiempoSegundos(), p2.getTiempoSegundos());
				})
				.limit(limite)
				.collect(Collectors.toList());
	}

}
