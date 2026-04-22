package dam.proyectofinal.afm.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import dam.proyectofinal.afm.model.Dificultad;
import dam.proyectofinal.afm.model.Nivel;
import dam.proyectofinal.afm.model.Partida;
import dam.proyectofinal.afm.model.Usuario;

public class CSVManager {
	private static final String ficheroPartidas = "historial.csv";
	private static final DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	
	public static void exportarPartida(Partida partida) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(ficheroPartidas, true))) {
			// Se define un formato para la fecha 
			String fechaStr = partida.getFechaHora().format(formato);
				
				String datos = String.format("%s;%s;%d;%b;%s%n",
						partida.getUsuario().getNickname(),
						partida.getDificultad().getNivel(),
						partida.getTiempoSegundos(),
						partida.isVictoria(),
                        fechaStr
						);
				writer.write(datos);
				System.out.println("Datos guardados en CSV: " + datos);
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static List<Partida> importarPartidas(File archivoSeleccionado) {
		// TODO Auto-generated method stub
		List<Partida> partidas = new ArrayList<>();
		
		File archivoLeer = (archivoSeleccionado != null) ? archivoSeleccionado : new File(ficheroPartidas);
		
		if (!archivoLeer.exists()) return partidas;
		
		try (BufferedReader br = new BufferedReader(new FileReader(archivoLeer))) {
			String linea;
			while ((linea = br.readLine()) != null) {
				if (linea.trim().isEmpty()) continue; // Se saltan lineas vacias
				
				String[] datos = linea.split(";");
				if (datos.length >= 5) {
					try {
					Partida p = new Partida();
					
					// Se reconstruye el usuario
					Usuario user = new Usuario();
					user.setNickname(datos[0]);
					p.setUsuario(user);
					
					// Seguridad en el ENUM
					try {
					// Se reconstruye la dificultad 
						Nivel nivel = Nivel.valueOf(datos[1].toUpperCase().trim());
						p.setDificultad(new Dificultad(0, nivel, 0, 0, 0));
					} catch (IllegalArgumentException e) {
						// TODO: handle exception
						System.err.println("Nivel no válido en línea: " + linea);
	                    continue;
					}
					
					p.setTiempoSegundos(Integer.parseInt(datos[2].trim()));
					p.setVictoria(Boolean.parseBoolean(datos[3].trim()));
                    p.setFechaHora(LocalDateTime.parse(datos[4].trim(), formato));
                    
                    partidas.add(p);
                    
					} catch (Exception e) {
						// TODO: handle exception
						System.err.println("Error procesando línea: " + linea + " -> " + e.getMessage());
					}
				} 
			}
		} catch (IOException e) {
			// TODO: handle exception
			System.err.println("Error al importar CSV: " + e.getMessage());
		}
		
		return partidas;
	}
	
	public static void guardarPartidas(List<Partida> partidas) {
		// No se usa el true para sobreescribir el fichero completo
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(ficheroPartidas))) {
			for (Partida partida : partidas) {
				String fechaStr = partida.getFechaHora().format(formato);
				String datos = String.format("%s;%s;%d;%b;%s%n", 
						partida.getUsuario().getNickname(),
						partida.getDificultad().getNivel(),
						partida.getTiempoSegundos(),
						partida.isVictoria(),
						fechaStr
						);
				writer.write(datos);
			}
			System.out.println("CSV actualizado: se han guardado " + partidas.size() + " partidas.");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
