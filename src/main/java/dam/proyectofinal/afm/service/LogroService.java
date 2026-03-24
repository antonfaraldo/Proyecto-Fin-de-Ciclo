package dam.proyectofinal.afm.service;

import java.util.List;

import dam.proyectofinal.afm.dao.LogroDAO;
import dam.proyectofinal.afm.dao.LogroDAOImpl;
import dam.proyectofinal.afm.dao.PartidaDAO;
import dam.proyectofinal.afm.dao.PartidaDAOImpl;
import dam.proyectofinal.afm.model.Logro;
import dam.proyectofinal.afm.model.Nivel;
import dam.proyectofinal.afm.model.Partida;
import dam.proyectofinal.afm.model.Usuario;
import dam.proyectofinal.afm.util.AppShell;
import dam.proyectofinal.afm.util.Toast;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class LogroService {
	private static int partidasGanadasTotales = 0;
	private LogroDAO logroDAO = new LogroDAOImpl();
	private static int contadorVictoriasMedioSinBanderas = 0;
	// El metodo analiza l partida una vez terminada y compruea si se ha desbloqueado algun logro 
	public void comprobarLogros (Usuario usuario, Partida partida) {
		if (!partida.isVictoria()) return; // SI pierde la partida no hay logros
		partidasGanadasTotales++;
		
		// Logros de Velocidad
		// Logro 1: Ganar en menos de 30 segundos en el modo FACIL
		if (partida.getDificultad().getNivel() == Nivel.FACIL && partida.getTiempoSegundos() < 30) {
			verificarYRegistrarLogro(usuario, "Principiante Veloz");
		}
		if (partida.getTiempoSegundos() < 5) {
			verificarYRegistrarLogro(usuario, "Pura Suerte");
		}
		// Logros de Dificultad
		// Logro 2: Ganar en el modo DIFICIL, no importa el tiempo
		if(partida.getDificultad().getNivel() == Nivel.DIFICIL) {
			verificarYRegistrarLogro(usuario, "Maestro Minero");
		}
		if (partida.getDificultad().getNivel() == Nivel.MEDIO) {
			verificarYRegistrarLogro(usuario, "Campo de Minas Superado");
		}
		// Jugar una personalizado
		if (partida.getDificultad().getNivel() == Nivel.PERSONALIZADO ) {
			verificarYRegistrarLogro(usuario, "Arquitecto");
		}
		// Logro de Contrarreloj
		if (partida.getDificultad().getNivel() == Nivel.CONTRARRELOJ && partida.isVictoria()) {
			verificarYRegistrarLogro(usuario, "Superviviente del Tiempo");
		}
		// Logros de desafio
		// Ganar sin usar banderas
		if (partida.getNumBanderasUsadas() == 0) {
			verificarYRegistrarLogro(usuario, "Estratega Preciso");
		}
		if (partida.getDificultad().getNivel() == Nivel.MEDIO && partida.getNumBanderasUsadas() == 0) {
			contadorVictoriasMedioSinBanderas++;
			System.out.println("Progreso Purista: " + contadorVictoriasMedioSinBanderas + "/3");
			
			if (contadorVictoriasMedioSinBanderas >= 3) {
				verificarYRegistrarLogro(usuario, "Purista del Medio");
			}
		}
		// Difícil y sin banderas
		if (partida.getDificultad().getNivel() == Nivel.DIFICIL && partida.getNumBanderasUsadas() == 0) {
			verificarYRegistrarLogro(usuario, "Maestro del Cálculo");
		}
		
		// Logros Evolutivos
		comprobarNivelesEvolutivos(usuario);
	}

	private void comprobarNivelesEvolutivos(Usuario usuario) {
		// TODO Auto-generated method stub
		PartidaDAO partidaDAO = new PartidaDAOImpl();
		// Se obtienen todas las partidas para contar las victorias
		List<Partida> historial = partidaDAO.listarPorUsuario(usuario);
		
		// Se cuentan las victorias totales 
		long victoriasTotales = historial.stream().filter(Partida::isVictoria).count();
		
		// Victorias especificas en Dificil
		long victoriasDificil = historial.stream().filter(p -> p.isVictoria() && p.getDificultad().getNivel() == Nivel.DIFICIL)
				.count();
		
		if (victoriasDificil >= 50) {
			verificarYRegistrarLogro(usuario, "Leyenda del Abismo");
		}
		
		// Verificación de Rangos acumulativos
		if (victoriasTotales >= 5) {
			verificarYRegistrarLogro(usuario, "Desactivador Novato");
		}
		if (victoriasTotales >= 10 ) {
			verificarYRegistrarLogro(usuario, "Novato del Hierro");
		}
		if (victoriasTotales >= 50) {
			verificarYRegistrarLogro(usuario, "Experto de Plata");
		}
		if (victoriasTotales >= 100) {
			verificarYRegistrarLogro(usuario, "Maestro de Oro");
		}
		if (victoriasTotales >= 200) {
			verificarYRegistrarLogro(usuario, "Veterano de Guerra");
		}
		if (victoriasTotales >= 500) {
			verificarYRegistrarLogro(usuario, "Leyenda de las Minas");
		}
	}

	private void verificarYRegistrarLogro(Usuario usuario, String nombreLogro) {
		// TODO Auto-generated method stub
		// Verificar si el usuario ya tiene el logro 
		if (!logroDAO.tieneUsuarioLogro(usuario, nombreLogro)) {
			// Se buscar el logro en el DAO
			Logro logroA_Asignar = null;
			for (Logro l : logroDAO.listarTodos()) {
				if (l.getNombre().equals(nombreLogro)) {
					logroA_Asignar = l;
					break;
				}
			}
			// Si se encuentra el logro en la lista, se asigna
			if (logroA_Asignar != null) {
				if (logroDAO.asignarLogroAUsuario(usuario, logroA_Asignar) ) {
					lanzarNotificacionLogro(usuario, nombreLogro);
				}
			}
		}
	}

	private void lanzarNotificacionLogro(Usuario usuario, String nombreLogro) {
		// TODO Auto-generated method stub
		// Solo se imprimen en consola 
		System.out.println("Logro Desbloqueado para " + usuario.getNickname() + ": " + nombreLogro);
		
		// Notificación visual
		Platform.runLater(() -> {
				Stage stagePrincipal = AppShell.getInstance().getPrimaryStage();
				
				if (stagePrincipal != null) {
					// Se llama a la clase Toast
					Toast.show(stagePrincipal, "¡LOGRO DESBLOQUEADO!", "Felicidades " + usuario.getNickname() + ", has ganado: " + nombreLogro);
				} else {
				// Si falla el Toast se muestra un alert 
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("¡Logro Desbloqueado!");
				alert.setHeaderText(null);
				alert.setContentText("🏆 ¡Felicidades, " + usuario.getNickname() + "!\nHas desbloqueado el logro: " + nombreLogro);
				alert.showAndWait();
			}
		});
	}
}
