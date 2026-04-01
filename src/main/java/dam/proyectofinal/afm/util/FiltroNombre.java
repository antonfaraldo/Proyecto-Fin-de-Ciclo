package dam.proyectofinal.afm.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class FiltroNombre {
	private static List<String> palabrasProhibidas = new ArrayList<>();
	private static final String ruta_json = "/json/filtro.json";
	
	// Se cargan las palabras prohibidas
	public static void cargarPalabrasDesdeJson() {
		System.out.println("Buscando en: " + FiltroNombre.class.getResource("/json/filtro.json"));
		try {
			InputStream is = FiltroNombre.class.getResourceAsStream(ruta_json);
			if (is == null) {
				System.err.println("Error: No se encuentra el archivo " + ruta_json + " en resources.");
                return;
			}
				
			String contenido = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
					.lines().collect(Collectors.joining("\n"));
			
			// Se extrae lo que hay entre los corchetes
			int inicio = contenido.indexOf("[") + 1;
			int fin = contenido.lastIndexOf("]");
			
			if (inicio > 0 && fin > inicio) {
			String soloPalabras = contenido.substring(inicio, fin);
			
			// Se limpian las comillas, espacios y saltos de línea
			String[] array = soloPalabras.replace("\"", "").replace("\n", "").replace("\r", "").trim().split(",");
			
			palabrasProhibidas.clear();
			for (String s : array) {
				if (!s.trim().isEmpty()) {
					palabrasProhibidas.add(s.trim().toLowerCase());
				}
			}
				System.out.println("Filtro JSON cargado: " + palabrasProhibidas.size() + " palabras.");
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Error cargando filtro.json: " + e.getMessage());
		}
	}
	
	public static boolean esValido(String nickname) {
		if (nickname == null || nickname.trim().isEmpty()) return false;
		
		// Si la lista esta vacio se intenta cargar
		if (palabrasProhibidas.isEmpty()) cargarPalabrasDesdeJson();
		
		String nickMinusculas = nickname.toLowerCase();
		
		// Se usa equals con posibilidad de cambiarlo por contains
		return palabrasProhibidas.stream().noneMatch(nickMinusculas::equals);
	}
}
