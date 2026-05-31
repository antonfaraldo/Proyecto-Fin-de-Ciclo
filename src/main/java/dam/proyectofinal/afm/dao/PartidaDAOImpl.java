package dam.proyectofinal.afm.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import dam.proyectofinal.afm.dto.ViciadoDTO;
import dam.proyectofinal.afm.model.Dificultad;
import dam.proyectofinal.afm.model.Nivel;
import dam.proyectofinal.afm.model.Partida;
import dam.proyectofinal.afm.model.Usuario;
import dam.proyectofinal.afm.util.CSVManager;
import dam.proyectofinal.afm.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class PartidaDAOImpl implements PartidaDAO{
    public PartidaDAOImpl() {}
	// Lista para simular la database
	private static List<Partida> historialPartidas = new ArrayList<>();

	public Map<String, Object> obtenerEstadisticasCompletas(Usuario usuario) {
		List<Partida> partidas = listarPorUsuario(usuario);
		// Se usan todas las partidas para el favorito
		List<Partida> partidasValidas = partidas;
		
		Map<String, Object> stats = new HashMap<>();
		
		// Victorias Totales y ratio
		long total = partidasValidas.size();
		long victorias = partidasValidas.stream().filter(Partida::isVictoria).count();
		double ratioGlobal = total > 0 ? (double) victorias / total * 100 : 0;
		
		// Tiempo total en segundos 
		int tiempoTotal = partidasValidas.stream().mapToInt(Partida::getTiempoSegundos).sum();
		
		Map<Nivel, Long> conteos = partidasValidas.stream()
				.collect(Collectors.groupingBy(p -> p.getDificultad().getNivel(), Collectors.counting()));
		
		// Conteo de victorias por nivel
		Map<Nivel, Long> victoriasPorNivel = partidasValidas.stream()
				.filter(Partida::isVictoria)
				.collect(Collectors.groupingBy(p -> p.getDificultad().getNivel(), Collectors.counting()));
		
		// Nivel favorito
		Nivel favorito = conteos.entrySet().stream()
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
	    
	    stats.put("conteoNiveles", conteos);
	    stats.put("victoriasNivel", victoriasPorNivel);
	    
	    return stats;
	}
	
	@Override
	public void guardar(Partida partida) {
		// TODO Auto-generated method stub
        Transaction tx =  null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Dificultad difBD = session.createQuery(
                    "FROM Dificultad WHERE nivel = :niv AND filas = :f AND columnas = :c AND numMinas = :m", Dificultad.class)
                    .setParameter("niv", partida.getDificultad().getNivel())
                    .setParameter("f", partida.getDificultad().getFilas())
                    .setParameter("c", partida.getDificultad().getColumnas())
                    .setParameter("m", partida.getDificultad().getNumMinas())
                    .uniqueResult();

            if (difBD != null) {
                partida.setDificultad(difBD);
            } else {
                session.persist(partida.getDificultad());
            }
            Usuario usuarioDB = session.merge(partida.getUsuario());
            partida.setUsuario(usuarioDB);

            session.persist(partida);
            tx.commit();
            System.out.println("SISTEMA: Historial de partida guardado en MySQL con éxito.");
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("ERROR: No se pudo guardar la partida en MySQL: " + e.getMessage());
        }
	}

	@Override
	public List<Partida> listarTodas() {
		// TODO Auto-generated method stub
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Partida p JOIN FETCH p.usuario JOIN FETCH p.dificultad";
            return session.createQuery(hql, Partida.class).list();
        } catch (Exception e) {
            System.err.println("ERROR en listarTodas de PartidaDAOImpl: " + e.getMessage());
            return new ArrayList<>();
        }
	}

	@Override
	public List<Partida> listarPorUsuario(Usuario usuario) {
		// TODO Auto-generated method stub
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Partida p JOIN FETCH p.usuario JOIN FETCH p.dificultad WHERE lower(p.usuario.nickname) = lower(:nick)";
            return session.createQuery(hql, Partida.class)
                    .setParameter("nick", usuario.getNickname()).list();
        } catch (Exception e) {
            System.err.println("ERROR en listarPorUsuario de PartidaDAOImpl: " + e.getMessage());
            return new ArrayList<>();
        }
	}

	@Override
	public List<Partida> obtenerRankingTop(Nivel nivel, int limite) {
		// TODO Auto-generated method stub
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String orden = (nivel == Nivel.CONTRARRELOJ) ? "DESC" : "ASC";
            String hql = "FROM Partida p JOIN FETCH p.usuario JOIN FETCH p.dificultad " +
                    "WHERE p.victoria = true AND p.dificultad.nivel = :niv ORDER BY p.tiempoSegundos " + orden;

            var query = session.createQuery(hql, Partida.class).setParameter("niv", nivel);
            if (limite > 0) {
                query.setMaxResults(limite);
            }
            return query.list();
        } catch (Exception e) {
            System.err.println("ERROR: Fallo al obtener el Ranking desde la base de datos: " + e.getMessage());
            return new ArrayList<>();
        }
	}
	
	public List<ViciadoDTO> obtenerRankingViciados(Nivel nivelFiltro) {
        return listarTodas().stream()
                .filter(p -> nivelFiltro == null || p.getDificultad().getNivel() == nivelFiltro)
                .collect(Collectors.groupingBy(
                        p -> p.getUsuario().getNickname(),
                        Collectors.summingInt(Partida::getTiempoSegundos)
                ))
                .entrySet().stream()
                .map(entry -> new ViciadoDTO(entry.getKey(), entry.getValue()))
                .sorted((v1, v2) -> Integer.compare(v2.getTiempoTotal(), v1.getTiempoTotal()))
                .collect(Collectors.toList());
	}
	
	@Override
	public void eliminarPartidasPorUsuario(String nickname) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createMutationQuery("DELETE FROM Partida p WHERE lower(p.usuario.nickname) = lower(:nick)")
                    .setParameter("nick", nickname).executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
        }
	}

}
