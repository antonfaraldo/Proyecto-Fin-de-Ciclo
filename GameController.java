package dam.proyectofinal.afm.controller;

import javafx.event.ActionEvent;
import dam.proyectofinal.afm.model.Dificultad;
import dam.proyectofinal.afm.model.Tablero;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class GameController {
	@FXML private Label lblMinas;
	@FXML private Label lblTiempo;
	private Tablero tablero; // Conectamos la lógica
	@FXML
	private GridPane gridTablero; // Panel de los botones
	
	public void inicializarJuego(Tablero tablero) {
		this.tablero = tablero;
		generarBotones(tablero.getFilas(), tablero.getColumnas());
	}

	private void generarBotones(int filas, int columnas) {
		// TODO Auto-generated method stub
		// Limpiamos el grid de la partida de antes
				gridTablero.getChildren().clear();
				
				// Se generan los botones
				for (int f = 0; f < filas; f++) {
					for (int c = 0; c < columnas; c++) {
						Button btn = new Button();
						btn.setPrefSize(30,  30);
						
						int filaActual = f;
						int colActual = c;
						btn.setOnAction(e -> manejarClic(filaActual, colActual, btn));
						
						// Se añade al GridPane
						gridTablero.add(btn, c, f);
					}
				}
		
	}
	// Inicializar la vista del juego con la dificultad
	public void prepararPartida(int filas, int columnas, int minas) {
		// Creamos el objeto Dificultad 
		Dificultad dificultad = new Dificultad(0, null, filas, columnas, minas);
		
		
		this.tablero = new Tablero(dificultad);
		lblMinas.setText("Minas: " + minas);
		
		generarBotones(filas, columnas);

	}

	private void manejarClic(int f, int c, Button botonPulsado) {
		// TODO Auto-generated method stub
		System.out.println("Has pulsado la casilla: " + f + "," + c);
		botonPulsado.setDisable(true); // Deshabilitar al pulsar
	}
	@FXML
	void resetJuego(ActionEvent event) {
		if (tablero != null) {
			prepararPartida(tablero.getFilas(), tablero.getColumnas(), tablero.getNumMinas());
			System.out.println("Partida reiniciada con éxito");
		}
	}
}
