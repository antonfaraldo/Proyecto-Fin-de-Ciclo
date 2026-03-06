package dam.proyectofinal.afm.controller;

import dam.proyectofinal.afm.dao.PartidaDAOImpl;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import dam.proyectofinal.afm.model.Dificultad;
import dam.proyectofinal.afm.model.Nivel;
import dam.proyectofinal.afm.model.Partida;
import dam.proyectofinal.afm.model.Tablero;
import dam.proyectofinal.afm.service.LogroService;
import dam.proyectofinal.afm.util.AppShell;
import dam.proyectofinal.afm.util.CSVManager;
import dam.proyectofinal.afm.util.View;
import javafx.fxml.FXML;
import javafx.scene.input.MouseButton;
import dam.proyectofinal.afm.dao.PartidaDAO;
import dam.proyectofinal.afm.model.Casilla;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class GameController {
	@FXML private Label lblMinas;
	@FXML private Label lblTiempo;
	private Tablero tablero; // Conectamos la lógica
	@FXML
	private GridPane gridTablero; // Panel de los botones
	private Timeline cronometro;
	private int segundosTranscurridos = 0;
	private boolean juegoIniciado = false; // El juego inica una vez el primer click
	private int banderasColocadas = 0;
	private PartidaDAO partidaDAO = new PartidaDAOImpl();
	private boolean juegoTerminado = false;
	
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
			banderasColocadas--;
		} else {
			casilla.setMarcada(true);
			btn.setText("🚩"); // Acordarse de meter luego el icono
			btn.setStyle("-fx-text-fill: red; -fx-font-weight: bold;"); // Luego hay que añadir el estilo
			banderasColocadas++;
		}
	}

	// Inicializar la vista del juego con la dificultad
	public void prepararPartida(Nivel nivelSeleccionado) {
		// Reseteamos el cronometro
		if (cronometro != null) cronometro.stop();
		segundosTranscurridos = 0;
		juegoIniciado = false;
		juegoTerminado = false;
		banderasColocadas = 0;
		lblTiempo.setText("Tiempo: 0s");
		
		// Extraer datos según el nievel
		int filas, columnas, minas;
		switch (nivelSeleccionado) {
	    case FACIL: 
	        filas = 8; columnas = 8; minas = 10;
	        break;      
	    case MEDIO: 
	        filas = 16; columnas = 16; minas = 40;
	        break;
	    case DIFICIL: 
	        filas = 16; columnas = 30; minas = 99;
	        break;
	    default:
	        filas = 8; columnas = 8; minas = 10;
	}
		
		// Creamos el objeto Dificultad 
		Dificultad dificultad = new Dificultad(0, nivelSeleccionado, filas, columnas, minas);
		this.tablero = new Tablero(dificultad);
		
		
		lblMinas.setText("Minas: " + minas);
		generarBotones(filas, columnas);
		
		// Ajustamos el tamaño de la ventana según el nivel
		javafx.application.Platform.runLater(() -> {
			AppShell.getInstance().ajustarVentana();
		});

	}

	private void manejarClic(int f, int c, Button botonPulsado) {
		Casilla casilla = tablero.getCeldas()[f][c];
		
		// SI hay bandera o la casilla esta revelada, no pasa nada
		// Ahora si el juego termina tampoco se puede hacer clic
		if (casilla.isMarcada() || casilla.isRevelada() || juegoTerminado)
			return;
		
		// Iniciar el crónometro al primer click
		if (!juegoIniciado) {
			tablero.colocarMinas(f, c);
			iniciarCronometro();
			juegoIniciado = true;
		}
		
		tablero.revelarCasilla(f, c);
		refrescarTablero();
		
		if (casilla.isEsMina()) {
			juegoTerminado = true; 
			cronometro.stop();
			revelarTodo();
			System.out.println("BOOM" + "Has perdido");
			mostrarAlerta("¡BOOM!", "Has pisado una mina. Partida terminada.");
			
			guardarResultado(false);
		} else if (tablero.verificarVictoria()) {
			juegoTerminado = true;
			cronometro.stop();
			System.out.println("Victoria");
			mostrarAlerta("¡!VICTORIA", "Enhorabuena. Has ganado la partida.");
			
			guardarResultado(true);
			
			// Se comprueban los logros
			LogroService logroService = new LogroService();
			// Se obtiene la partida que se acaba de ganar
			Partida p = new Partida();
			p.setDificultad(tablero.getDificultad());
			p.setTiempoSegundos(segundosTranscurridos);
			p.setVictoria(true);
			p.setNumBanderasUsadas(banderasColocadas);
			
			logroService.comprobarLogros(AppShell.getInstance().getUsuario(), p);
		}
		
	}
	private void mostrarAlerta(String titulo, String mensaje) {
		// TODO Auto-generated method stub
		javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
	}

	private void revelarTodo() {
		// TODO Auto-generated method stub
		for (int f = 0; f < tablero.getFilas(); f++) {
			for (int c = 0; c < tablero.getColumnas(); c++) {
				if (tablero.getCeldas()[f][c].isEsMina()) {
					tablero.getCeldas()[f][c].setRevelada(true);
				}
			}
		}
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
	private void iniciarCronometro() {
		if (cronometro != null) {
			cronometro.stop();
		}
		segundosTranscurridos = 0;
		lblTiempo.setText("Tiempo: 0s");
		
		cronometro = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
			segundosTranscurridos++;
			lblTiempo.setText("Tiempo: " + segundosTranscurridos + "s");
		}));
		cronometro.setCycleCount(Animation.INDEFINITE);
		cronometro.play();
	}
	private void guardarResultado(boolean victoria) {
		Partida p = new Partida();
		p.setUsuario(AppShell.getInstance().getUsuario());
		p.setVictoria(victoria);
		p.setTiempoSegundos(segundosTranscurridos);
		p.setFechaHora(java.time.LocalDateTime.now());
		p.setDificultad(tablero.getDificultad());
		p.setNumBanderasUsadas(banderasColocadas);
		
		CSVManager.exportarPartida(p);
		
		partidaDAO.guardar(p);
	}

	@FXML
	void resetJuego(ActionEvent event) {
		if (tablero != null && tablero.getDificultad() != null) {
			prepararPartida(tablero.getDificultad().getNivel());
			System.out.println("Partida reiniciada con éxito");
		}
	}
	@FXML
	private void volverAlMenu() {
	    AppShell.getInstance().loadView(View.MENU);
	    AppShell.getInstance().ajustarVentana();
	}
}
