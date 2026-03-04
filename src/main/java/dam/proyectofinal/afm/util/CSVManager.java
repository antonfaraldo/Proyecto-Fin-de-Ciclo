package dam.proyectofinal.afm.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import dam.proyectofinal.afm.model.Partida;

public class CSVManager {
	private static final String ficheroPartidas = "historial.csv";
	
	public static void exportarPartida(Partida partida) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(ficheroPartidas, true))) {
				
				String datos = String.format("%s;%d;%b;%n",
						partida.getUsuario().getNickname(),
						partida.getTiempoSegundos(),
						partida.isVictoria()
						);
				writer.write(datos);
				System.out.println("Datos guardados en CSV: " + datos);
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
