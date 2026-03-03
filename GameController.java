package dam.proyectofinal.afm.controller;

import dam.proyectofinal.afm.model.Tablero;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class GameController {
	private Tablero tablero; // Conectamos la lógica
	@FXML
	private GridPane gridTablero; // Panel de los botones
	
	public void inicializarJuego(Tablero tablero) {
		this.tablero = tablero;
		generarBotones();
	}

	private void generarBotones() {
		// TODO Auto-generated method stub
		
	}
}
