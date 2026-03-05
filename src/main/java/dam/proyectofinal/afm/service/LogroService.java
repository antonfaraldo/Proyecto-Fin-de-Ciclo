package dam.proyectofinal.afm.service;

import dam.proyectofinal.afm.model.Nivel;
import dam.proyectofinal.afm.model.Partida;
import dam.proyectofinal.afm.model.Usuario;

public class LogroService {
	// El metodo analiza l partida una vez terminada y compruea si se ha desbloqueado algun logro 
	public void comprobarLogros (Usuario usuario, Partida partida) {
		if (!partida.isVictoria()) return; // SI pierde la partida no hay logros
		
		// Logro 1: Ganar en menos de 30 segundos en el modo FACIL
		if (partida.getDificultad().getNivel() == Nivel.FACIL && partida.getTiempoSegundos() < 30) {
			registrarLogro(usuario, "Principiante Veloz");
		}
		// Logro 2: Ganar en el modo DIFICIL, no importa el tiempo
		if(partida.getDificultad().getNivel() == Nivel.DIFICIL) {
			registrarLogro(usuario, "Maestro Minero");
		}
	}

	private void registrarLogro(Usuario usuario, String nombreLogro) {
		// TODO Auto-generated method stub
		// Por ahora solo se imprimen en consola 
		System.out.println("Logro Desbloqueado para " + usuario.getNickname() + ": " + nombreLogro);
	}
}
