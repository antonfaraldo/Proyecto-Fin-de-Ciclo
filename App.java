package dam.proyectofinal.afm;

import dam.proyectofinal.afm.model.Dificultad;
import dam.proyectofinal.afm.model.Tablero;

public class App {
	public static void main(String[] args) {
		// Dificultad de prueba (8x8 con 10 minas)
		Dificultad prueba = new Dificultad();
		prueba.setFilas(8);
		prueba.setColumnas(8);
		prueba.setNumMInas(10);
		
		// Creación del Tablero
		Tablero pruebaTablero = new Tablero(prueba);
		
		// Mostramos el resultado
		System.out.println("---Prueba de Tablero Buscaminas---");
		pruebaTablero.imprimirTableroConsola();
	}

}
