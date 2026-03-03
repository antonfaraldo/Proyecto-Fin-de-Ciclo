package dam.proyectofinal.afm.model;

import java.util.Random;

public class Tablero {
	private Casilla[][] celdas;
	private int filas;
	private int columnas;
	private int numeroMinas;
	private boolean juegoTerminado;
	
	//Con el constructor configuro el tamaño según la dificultad
	public Tablero(Dificultad dificultad) {
		this.filas = dificultad.getFilas();
		this.columnas = dificultad.getColumnas();
		this.numeroMinas = dificultad.getNumMinas();
		this.celdas = new Casilla[filas][columnas];
		this.juegoTerminado = false;
		
		// UNa vez el tablero esta creado, se prepara
		inicializarCeldas();
		colocarMinas();
		calcularNumeros();
	}

	private void calcularNumeros() {
		// TODO Auto-generated method stub
		for (int f = 0; f < filas; f++) {
			for (int c = 0; c < columnas; c++) {
				if (!celdas[f][c].isEsMina()) {
					int minas = contarMinasVecinas(f, c);
					celdas[f][c].setMinasAlrededor(minas);
				}
			}
		}
	}

	private int contarMinasVecinas(int fila, int col) {
		// TODO Auto-generated method stub
		int contador = 0;
		// Se recorre el cuadro alrededor de la casilla
		for (int i = fila - 1; i <= fila + 1; i++) {
			for (int j = col - 1; j <= col + 1; j++) {
				// Se verifica que no se sale del tablero
				if (i >= 0 && i < filas && j >= 0 && j < columnas) {
					if (celdas[i][j].isEsMina()) {
					contador++;
					}
				}
			}
		}
		
		return contador;
	}

	private void colocarMinas() {
		// TODO Auto-generated method stub
		Random rand = new Random();
		int minasColocadas = 0;
		
		while (minasColocadas < numeroMinas) {
			int f = rand.nextInt(filas); // Elige la fila al azar
			int c = rand.nextInt(columnas); // Elige la columna al azar
			
			// Solo se coloca la mina si la casilla está vacía
			if (!celdas[f][c].isEsMina()) {
				celdas[f][c].setEsMina(true);
				minasColocadas++;
			}
		}
		
	}

	private void inicializarCeldas() {
		// TODO Auto-generated method stub
		for (int f = 0; f < filas; f++) {
			for (int c = 0; c < columnas; c++) {
				celdas[f][c] = new Casilla();
			}
		}
		
	}
	
	// Permite a la interfaz dibujar los botones
	public Casilla[][] getCeldas() {
		return celdas;
	}

	// Permite al controlador saber cuando parar el juego
	public boolean isJuegoTerminado() {
		return juegoTerminado;
	}
	
	// Es útil para que el controlador sepa medir la cuadrícula
	public int getFilas() {
		return filas;
	}
	public int getColumnas() {
		return columnas;
	}

	public void imprimirTableroConsola() {
		// TODO Auto-generated method stub
		for (int f = 0; f < filas; f++) {
			for (int c = 0; c < columnas; c++) {
				if (celdas[f][c].isEsMina()) {
					System.out.print("[*] "); // Mina representada por el asterisco
				} else {
					System.out.print("[" + celdas[f][c].getMinasAlrededor() + "] ");
				}
			}
			System.out.println();
		}
	}
}
