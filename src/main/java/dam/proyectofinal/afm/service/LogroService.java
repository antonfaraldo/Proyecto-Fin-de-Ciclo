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
		// Jugar una personalizado
		if (partida.getDificultad().getNivel() == Nivel.FACIL ) {
			if (partida.getDificultad().getFilas() != 8 || 
		            partida.getDificultad().getColumnas() != 8 || 
		            partida.getDificultad().getNumMinas() != 10) {
		            verificarYRegistrarLogro(usuario, "Arquitecto");
		        }
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
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("¡Logro Desbloqueado!");
			alert.setHeaderText(null);
			alert.setContentText("🏆 ¡Felicidades, " + usuario.getNickname() + "!\nHas desbloqeuado el logro: " + nombreLogro);
			alert.showAndWait();
		});
	}
}
