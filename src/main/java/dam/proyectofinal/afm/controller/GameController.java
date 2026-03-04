package dam.proyectofinal.afm.controller;

import javafx.event.ActionEvent;
import dam.proyectofinal.afm.model.Dificultad;
import dam.proyectofinal.afm.model.Tablero;
import javafx.fxml.FXML;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import dam.proyectofinal.afm.model.Casilla;
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
						
						// Se usa el setOnMouseClicked para detectar si es click derecho o izquierdo
						btn.setOnMouseClicked(event -> {
							if (event.getButton() == MouseButton.SECONDARY) {
								// Click Derecho: Poner o Quitar la bandera
								marcarBandera(filaActual, colActual, btn);
							} else if (event.getButton() == MouseButton.PRIMARY) {
								// Click Izquierdo: Se revela la casilla
								manejarClic(filaActual, colActual, btn);
							}
						});
						
						// Se añade al GridPane
						gridTablero.add(btn, c, f);
					}
				}
		
	}
	private void marcarBandera(int f, int c, Button btn) {
		// TODO Auto-generated method stub
		Casilla casilla = tablero.getCeldas()[f][c];
		
		// Casilla ya revelado, no se ouede poner la bandera
		if (casilla.isRevelada()) return;
		
		// Si hay bandera se quita, si no hay se pone
		if (casilla.isMarcada()) {
			casilla.setMarcada(false);
			btn.setText(""); // Se quita el icono
			btn.setStyle(""); // Se quita el supuesto icono
		} else {
			casilla.setMarcada(true);
			btn.setText("🚩"); // Acordarse de meter luego el icono
			btn.setStyle("-fx-text-fill: red; -fx-font-weight: bold;"); // Luego hay que añadir el estilo 
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
		Casilla casilla = tablero.getCeldas()[f][c];
		
		// SI hay bandera o la casilla esta revelada, no pasa nada
		if (casilla.isMarcada() || casilla.isRevelada()) {
			return;
		}
		
		tablero.revelarCasilla(f, c);
		refrescarTablero();
		
	}
	private void refrescarTablero() {
		// TODO Auto-generated method stub
		// Recorrer todos los nodos
		for (javafx.scene.Node node : gridTablero.getChildren()) {
			// Verificación que el Nodo sea un botn
			if (node instanceof Button) {
				Button btn = (Button) node;
				
				// Se obtiene la posición del boton
				Integer c = GridPane.getColumnIndex(btn);
				Integer f = GridPane.getRowIndex(btn);
				
				if (f != null && c != null) {
					// Se consulta el estado de la casilla 
					Casilla casilla = tablero.getCeldas()[f][c];
					
					// Si esta revelada se actualiza el boton
					if (casilla.isRevelada()) {
						if (casilla.isEsMina()) {
							btn.setText("💣");
	                        btn.setStyle("-fx-background-color: red;");
						} else {
							int minas = casilla.getMinasAlrededor();
							btn.setText(minas > 0 ? String.valueOf(minas) : "");
							
							// Se bloquea el botn
							btn.setDisable(true);
							btn.setStyle("-fx-background-color: #ddd; -fx-opacity: 1;");
						}
					}
				}
			}
		}
	}

	@FXML
	void resetJuego(ActionEvent event) {
		if (tablero != null) {
			prepararPartida(tablero.getFilas(), tablero.getColumnas(), tablero.getNumMinas());
			System.out.println("Partida reiniciada con éxito");
		}
	}
}
