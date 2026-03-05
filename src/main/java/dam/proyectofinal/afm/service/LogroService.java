package dam.proyectofinal.afm.service;

import dam.proyectofinal.afm.dao.LogroDAO;
import dam.proyectofinal.afm.dao.LogroDAOImpl;
import dam.proyectofinal.afm.model.Logro;
import dam.proyectofinal.afm.model.Nivel;
import dam.proyectofinal.afm.model.Partida;
import dam.proyectofinal.afm.model.Usuario;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class LogroService {
	private static int partidasGanadasTotales = 0;
	private LogroDAO logroDAO = new LogroDAOImpl();
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
		// Logros Acumulativos
		if (partidasGanadasTotales == 5) {
			verificarYRegistrarLogro(usuario, "Desactivador Novato");
		}
		
		// Logros de desafio
		// Ganar sin usar banderas
		if (partida.getNumBanderasUsadas() == 0) {
			verificarYRegistrarLogro(usuario, "Estratega Preciso");
		}
	}

	private void verificarYRegistrarLogro(Usuario usuario, String nombreLogro) {
		// TODO Auto-generated method stub
		// Verificar si el usuario ya tiene el logro 
		if (!logroDAO.tieneUsuarioLogro(usuario, nombreLogro)) {
			// En caso de no tener el logro, se crea
			Logro nuevoLogro = new Logro();
			nuevoLogro.setNombre(nombreLogro);
			
			if (logroDAO.asignarLogroAUsuario(usuario, nuevoLogro)) {
				// Si se guarda correctamente se lanza la alerta
				lanzarNotificacionLogro(usuario, nombreLogro);
			}
		}
	}

	private void lanzarNotificacionLogro(Usuario usuario, String nombreLogro) {
		// TODO Auto-generated method stub
		// Solo se imprimen en consola 
		System.out.println("Logro Desbloqueado para " + usuario.getNickname() + ": " + nombreLogro);
		
		// Notificación visual
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("¡Logro Desbloqueado!");
			alert.setHeaderText(null);
			alert.setContentText("🏆 ¡Felicidades, " + usuario.getNickname() + "!\nHas desbloqeuado el logro: " + nombreLogro);
			alert.showAndWait();
		});
	}
}
