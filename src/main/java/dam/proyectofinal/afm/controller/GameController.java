package dam.proyectofinal.afm.controller;

import dam.proyectofinal.afm.dao.PartidaDAOImpl;  

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.util.Duration;

import dam.proyectofinal.afm.dao.PartidaDAO;
import dam.proyectofinal.afm.model.Casilla;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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
	@FXML private VBox vboxPausa;
	@FXML private Button btnPausa;
	private boolean pausado = false;
	@FXML private Label lblMejorTiempo;
	private int recordActual = -1;
	@FXML private VBox vboxFinal;
	@FXML private Label lblEstadoFinal;
	@FXML private Label lblDetallesFinal;
	@FXML private BorderPane mainContainer;
	private double tiempoContrarreloj = 60.0;
	private AnimationTimer animationTimer;
	private long lastUpdate = 0;
	
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
						
						GridPane.setRowIndex(btn, f);
						GridPane.setColumnIndex(btn, c);
						
						// Efecto Hover
						final int filaActual = f;
						final int colActual = c;
						
						btn.setOnMouseEntered(e -> {
							if (filaActual < tablero.getFilas() && colActual < tablero.getColumnas()) {
								Casilla casillaLogica = tablero.getCeldas()[filaActual][colActual];
								if (!casillaLogica.isRevelada() && !casillaLogica.isMarcada() && !pausado && !juegoTerminado ) {
									btn.setStyle("-fx-background-color: #d1d8e0; -fx-cursor: hand; " +
						                     "-fx-border-color: #ecf0f1 #7f8c8d #7f8c8d #ecf0f1; -fx-border-width: 2px;");
								}
							}
						});
						btn.setOnMouseExited(e -> {
							if (filaActual < tablero.getFilas() && colActual < tablero.getColumnas()) {
								// Al salir se restaura el fondo original
								Casilla casillaLogica = tablero.getCeldas()[filaActual][colActual];
								actualizarBotonCasilla(btn, casillaLogica);
							}
						});
						
						
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
						// Efecto resaltado del chording
						btn.setOnMousePressed(e -> {
							if (e.getButton() == MouseButton.PRIMARY && !pausado && !juegoTerminado) {
								Casilla casillaLogica = tablero.getCeldas()[filaActual][colActual];
								// Si la casilla esta revelada o es un num, se resaltan las de al lado
								if ( casillaLogica.isRevelada() && casillaLogica.getMinasAlrededor() > 0) {
									resaltarVecinas(filaActual, colActual, true);
								}
							}
						});
						btn.setOnMouseReleased(e -> {
							if (e.getButton() == MouseButton.PRIMARY) {
								// Al soltar , se quita el resaltado
								resaltarVecinas(filaActual, colActual, false);
							}
						});
						
						// Se añade al GridPane
						gridTablero.add(btn, c, f);
						actualizarBotonCasilla(btn, tablero.getCeldas()[f][c]);
					}
				}
		gridTablero.setDisable(false);
	}
	private void marcarBandera(int f, int c, Button btn) {
		// TODO Auto-generated method stub
		Casilla casilla = tablero.getCeldas()[f][c];
		
		// Casilla ya revelado, no se ouede poner la bandera
		if (casilla.isRevelada()) return;
		
		// Si hay bandera se quita, si no hay se pone
		if (casilla.isMarcada()) {
			casilla.setMarcada(false);
			banderasColocadas--;
		} else {
			casilla.setMarcada(true);
			banderasColocadas++;
		}
		actualizarBotonCasilla(btn, casilla);
		
		int totalMinas = tablero.getDificultad().getNumMinas();
		int minasRestantes = totalMinas - banderasColocadas;
		lblMinas.setText(String.format("💣 %d | 🚩 %d/%d", minasRestantes, banderasColocadas, totalMinas));
		
		if (minasRestantes < 0) {
			// Se cambia a color rojo para alertar 
			lblMinas.setStyle(lblMinas.getStyle() + "-fx-text-fill: #ff4444; -fx-font-weight: bold;");
			// Animacion de sacudida
			vibrarLabel(lblMinas);
		} else {
			// Se restaura al color normal si vuelve a tener valores positivos
			lblMinas.setStyle(lblMinas.getStyle() + "-fx-text-fill: black; -fx-font-weight: bold;");
		}
	}

	private void vibrarLabel(Label label) {
		// TODO Auto-generated method stub
		TranslateTransition tt = new TranslateTransition(Duration.millis(50), label);
		tt.setFromX(0);
		tt.setByX(5); // Se mueve 5 pixeles a la derecha
		tt.setCycleCount(4); // Hace el movimiento 4 veces
		tt.setAutoReverse(true); // Vuelve al origen
		
		tt.setOnFinished(e -> label.setTranslateX(0));
		
		tt.play();
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
		
		// Reseteamos el estado de Pausa
		pausado = false;
		if (vboxPausa != null) {
			vboxPausa.setVisible(false);
		}
		if (gridTablero != null) {
			gridTablero.setVisible(true);
			gridTablero.setOpacity(1.0);
			gridTablero.setDisable(false);
			gridTablero.setTranslateX(0); // Reset de la animación
		}
		if (btnPausa != null) {
			btnPausa.setText("⏸ Pausa");
			btnPausa.setStyle("");
		}
		// Ocultamos el panel de fin de partida
		if (vboxFinal != null) {
			vboxFinal.setVisible(false);
			vboxFinal.setScaleX(1.0); // Reset de la animación de escala
			vboxFinal.setScaleY(1.0); // Reset de la animación de escala
		}
		if (gridTablero != null) {
			gridTablero.setOpacity(1.0);
			gridTablero.setDisable(false);
		}
		
		if (gridTablero != null) {
			gridTablero.setDisable(true);
			gridTablero.getChildren().clear();
		}
		
		// Extraer datos según el nivel
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
	    case PERSONALIZADO:
	    	// Solo asignamos valores por defecto para evitar errores, se sobreescribirán en el método específico
	        filas = (tablero != null) ? tablero.getFilas() : 8;
	        columnas = (tablero != null) ? tablero.getColumnas() : 8;
	        minas = (tablero != null) ? tablero.getDificultad().getNumMinas() : 10;
	        break;
	    case CONTRARRELOJ:
	    	filas = 16; columnas = 16; minas = 50;
	    	break;
	    default:
	        filas = 8; columnas = 8; minas = 10;
	}
		
		// Creamos el objeto Dificultad 
		Dificultad dificultad = new Dificultad(0, nivelSeleccionado, filas, columnas, minas);
		this.tablero = new Tablero(dificultad);
		
		
		lblMinas.setText(String.format("💣 %d | 🚩 0/%d", minas, minas));
		lblMinas.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
		generarBotones(filas, columnas);
		
		// Ajustamos el tamaño de la ventana según el nivel
		javafx.application.Platform.runLater(() -> {
			Stage stage = (Stage) gridTablero.getScene().getWindow();
			if (nivelSeleccionado == Nivel.DIFICIL) {
				stage.setMaximized(true);
			} else {
				if (!stage.isMaximized()) {
		            AppShell.getInstance().ajustarVentana();
				}
			}
			
			// Atajos del teclado
			configurarAtajosTeclado();
		});
		// Cargar Record Actual
		try { 
			if (nivelSeleccionado == Nivel.PERSONALIZADO) {
				recordActual = -1;
				lblMejorTiempo.setText("Personalizado");
	            lblMejorTiempo.setStyle("-fx-text-fill: #bdc3c7;");
			} else {
				List<Partida> mejores = partidaDAO.obtenerRankingTop(nivelSeleccionado, 1);
				if (!mejores.isEmpty()) {
					recordActual = mejores.get(0).getTiempoSegundos();
					lblMejorTiempo.setText("Récord: " + recordActual + "s");
					lblMejorTiempo.setStyle("-fx-text-fill: white;");
				} else {
					recordActual = -1;
					lblMejorTiempo.setText("Récord: --");
					lblMejorTiempo.setStyle("-fx-text-fill: #FFD700;"); // Color para que resalte mejor 
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			recordActual = -1;
		}

	}

	private void manejarClic(int f, int c, Button botonPulsado) {
		// No se puede hacer clic mientras el cronometro esta pausado
		if (pausado || juegoTerminado) return;		
		Casilla casilla = tablero.getCeldas()[f][c];
		// Lógica Chording
		if (casilla.isRevelada()) {
			if (casilla.getMinasAlrededor() > 0 && !casilla.isEsMina()) {
				ejecutarChording(f, c, botonPulsado);
			}
			return;
		}
		
		// SI hay bandera o la casilla esta revelada, no pasa nada
		// Ahora si el juego termina tampoco se puede hacer clic
		if (casilla.isMarcada())
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
			finalizarPartida(false);
		} else {
			// Modo Contrarreloj
			if (tablero.getDificultad().getNivel() == Nivel.CONTRARRELOJ) {
				tiempoContrarreloj += 1.0; // Se suma 1 segundo por cada casilla revelada
				
				// Destello visual verde 
				lblTiempo.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
				new Timeline(new KeyFrame(Duration.millis(300), e -> lblTiempo.setStyle(""))).play();
			}
			if (tablero.verificarVictoria()) {
				finalizarPartida(true);
			}
		}
	}
	private void finalizarPartida(boolean victoria) {
		// TODO Auto-generated method stub
		juegoTerminado = true;
        cronometro.stop();
        // Se bloque el tablero inmediatamente
        gridTablero.setDisable(true);
        
        if (victoria) {
        	lblEstadoFinal.setText("¡VICTORIA!");
            lblEstadoFinal.setStyle("-fx-text-fill: #2ecc71;");
            // Animación confeti
            lanzarConfeti();
            animarPanelFinal();
            
            guardarResultado(true);

            LogroService logroService = new LogroService();
            Partida p = new Partida();
            p.setDificultad(tablero.getDificultad());
            p.setTiempoSegundos(segundosTranscurridos);
            p.setVictoria(true);
            p.setNumBanderasUsadas(banderasColocadas);
            logroService.comprobarLogros(AppShell.getInstance().getUsuario(), p);
            vboxFinal.setVisible(true);
            gridTablero.setOpacity(0.4);
        } else {
            revelarMinasAnimado();
            // Animación 
            TranslateTransition tt = new TranslateTransition(Duration.millis(50), gridTablero);
            tt.setFromX(-10);
            tt.setToX(10);
            tt.setCycleCount(6);
            tt.setAutoReverse(true);
            tt.play();
            
            lblEstadoFinal.setText("¡BOOM!");
            lblEstadoFinal.setStyle("-fx-text-fill: #e74c3c;");
            guardarResultado(false);
        }
        lblDetallesFinal.setText("Tiempo: " + segundosTranscurridos + "s | Banderas: " + banderasColocadas);
	}

	private void animarPanelFinal() {
		// TODO Auto-generated method stub
		vboxFinal.setOpacity(0);
		vboxFinal.setScaleX(0.5);
		vboxFinal.setScaleY(0.5);
		vboxFinal.setVisible(true);
		
		FadeTransition fade = new FadeTransition(Duration.millis(600), vboxFinal);
	    fade.setToValue(1.0);
		
		ScaleTransition st = new ScaleTransition(Duration.millis(600), vboxFinal);
		st.setToX(1.0);
	    st.setToY(1.0);
	    st.setInterpolator(Interpolator.EASE_OUT); // Efecto de frenado
	    
	    ParallelTransition pt = new ParallelTransition(fade, st);
	    pt.setDelay(Duration.millis(200));
	    pt.play();
	}

	private void ejecutarChording(int fila, int col, Button botonPulsado) {
		// TODO Auto-generated method stub
		Casilla casillaCentral = tablero.getCeldas()[fila][col];
		int minasIndicadas = casillaCentral.getMinasAlrededor();
		int banderasAlrededor = 0;
		
		// Contamos banderas en las casillas vecinas
		for (int i = fila - 1; i <= fila + 1; i++) {
			for (int j = col - 1; j <= col + 1; j++) {
				// Se verifican los limites del tablero y la casilla central
				if (i >= 0 && i < tablero.getFilas() && j >= 0 && j < tablero.getColumnas() && !(i == fila && j == col)) {
					if (tablero.getCeldas()[i][j].isMarcada()) {
						banderasAlrededor++;
					}
				}
			}
		}
		// El num de banderas coincide, se revelan las casillas vecinas sin bandera
		if (banderasAlrededor == minasIndicadas) {
			System.out.println("Ejecutando Chording en (" + fila + ", " + col + ")");
			boolean minaTocada = false;
			int casillasAbiertas = 0; // Contador para el tiempo extra en el modo contrarreloj
			
			for (int i = fila - 1; i <= fila + 1; i++) {
				for (int j = col - 1; j <= col + 1; j++) {
					if (i >= 0 && i < tablero.getFilas() && j >= 0 && j < tablero.getColumnas() && !(i == fila && j == col)) {
						Casilla vecina = tablero.getCeldas()[i][j];
						
						// Solo se revela si no está marcada y no está ya revelada
						if (!vecina.isMarcada() && !vecina.isRevelada()) {
							tablero.revelarCasilla(i, j);
							casillasAbiertas++; 
							if (vecina.isEsMina()) {
								minaTocada = true;
							}
						}
					}
				}
			}
			// Se actualiza la interfaz
			refrescarTablero();
			
			// Se suma tiempo en el modo contrarreloj
			if (tablero.getDificultad().getNivel() == Nivel.CONTRARRELOJ && casillasAbiertas > 0 && !minaTocada) {
				tiempoContrarreloj += casillasAbiertas; // Se suma 1s por cada casilla revelada 
				
				// Destello verde
				lblTiempo.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
				new Timeline(new KeyFrame(Duration.millis(300), e -> lblTiempo.setStyle(""))).play();
			}
			
			// Se comprueba 
			if (minaTocada) {
				finalizarPartida(false);
			} else if (tablero.verificarVictoria()) {
				finalizarPartida(true);
			}
		} 
		// Animación de parpadeo
		else {
			botonPulsado.setStyle(botonPulsado.getStyle() + "-fx-border-color: #e74c3c; -fx-border-width: 2px; -fx-background-color: #ffcccc;");
			
			PauseTransition pausa = new PauseTransition(Duration.millis(200));
			pausa.setOnFinished(e -> {
				refrescarTablero();
			});
			pausa.play();
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
					actualizarBotonCasilla(btn, tablero.getCeldas()[f][c]);
				}
			}
		}
	}
	private String obtenerColorPorNumero(int numero) {
		// TODO Auto-generated method stub
		switch (numero) {
		case 1: return "-fx-text-fill: blue;";
		case 2: return "-fx-text-fill: green;";
		case 3: return "-fx-text-fill: red;";
		case 4: return "-fx-text-fill: darkblue;";
		case 5: return "-fx-text-fill: maroon;";
		case 6: return "-fx-text-fill: #008080;";
		case 7: return "-fx-text-fill: black;";
		case 8: return "-fx-text-fill: gray;";
		default: return "-fx-text-fill: transparent;";
		}
	}

	private void iniciarCronometro() {
		if (cronometro != null) {
			cronometro.stop();
		}
		
		// Para el modo contrarreloj
		if (animationTimer != null) animationTimer.stop();
		
		segundosTranscurridos = 0;
		lastUpdate = 0;
		// lblTiempo.setText("Tiempo: 0s");
		
		// Se verifica si el nivel es contrarreloj
		if (tablero.getDificultad().getNivel() == Nivel.CONTRARRELOJ) {
			tiempoContrarreloj = 60.0; // Es el tiempo inicial
			lblTiempo.setText(String.format("Tiempo: %.1f s", tiempoContrarreloj));
			
			animationTimer = new AnimationTimer() {
				
				@Override
				public void handle(long now) {
					// TODO Auto-generated method stub
					if (lastUpdate > 0 && !pausado && !juegoTerminado) {
						double elapsedSeconds = (now - lastUpdate) / 1_000_000_000.0;
						tiempoContrarreloj -= elapsedSeconds;
						
						// Se actualiza la etiqueta
						lblTiempo.setText(String.format("Tiempo: %.1f s", tiempoContrarreloj));
						
						if (tiempoContrarreloj <= 0) {
							stop();
							finalizarPartida(false); // Derrota por tiempo
							lblEstadoFinal.setText("¡BOOM!");
							lblDetallesFinal.setText("Tiempo agotado");						}
					}
					lastUpdate = now;
				}
			};
			animationTimer.start();
		}
		
		cronometro = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
			segundosTranscurridos++;
			int minutos = segundosTranscurridos / 60;
			int segundos = segundosTranscurridos % 60;
			lblTiempo.setText(String.format("Tiempo: %02d:%02d", minutos, segundos));
			
			// Comparamos con el record
			if (recordActual != -1) {
				int diferencia = recordActual - segundosTranscurridos;
				if (diferencia >= 0) {
					// Por debajo del record, GANANDO
					lblMejorTiempo.setText("Récord: " + recordActual + "s (-" + diferencia + ")");
					lblMejorTiempo.setStyle("-fx-text-fill: #00FF00;");
				} else {
					// Perdiendo
					lblMejorTiempo.setText("Récord: " + recordActual + "s (+" + Math.abs(diferencia) + ")");
	                lblMejorTiempo.setStyle("-fx-text-fill: #FF4444;");
				}
			}
			// Si el nivel es Personlizado, se mantiene el texto neutro
			else if (tablero.getDificultad().getNivel() == Nivel.PERSONALIZADO) {
				lblMejorTiempo.setText("Personalizado");
	            lblMejorTiempo.setStyle("-fx-text-fill: #bdc3c7;");
			}
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
	private void handlePausa() {
		// Si el juego no ha empezado, no se pausa
		if (!juegoIniciado || juegoTerminado) return;
		
		pausado = !pausado;
		
		if (pausado) {
			cronometro.pause();
			vboxPausa.setVisible(true); // Muestra el aviso y la capa negra
			gridTablero.setVisible(false); // Oculta las minas
			gridTablero.setOpacity(0.2);
			gridTablero.setDisable(true); // Bloqueamos los clics 
			btnPausa.setText("▶ Reanudar");
		} else {
			cronometro.play();
			vboxPausa.setVisible(false);
			gridTablero.setOpacity(1.0);
			gridTablero.setDisable(false);
			gridTablero.setVisible(true);
			btnPausa.setText("⏸ Pausa");
			btnPausa.setStyle("");
		}
	}

	@FXML
	void resetJuego(ActionEvent event) {
		// Se impide el reset si la animación esta corriendo
		if (juegoTerminado && !vboxFinal.isVisible()) {
			return;
		}
		if (tablero != null && tablero.getDificultad() != null) {
			Dificultad difActual = tablero.getDificultad();
			
			// Nivel personalizado se reinicia con los valores especificos
			if (difActual.getNivel() == Nivel.PERSONALIZADO) {
				prepararPartidaPersonalizada(difActual.getFilas(), difActual.getColumnas(), difActual.getNumMinas());
			} else {
				prepararPartida(difActual.getNivel());
			}
			System.out.println("Partida reiniciada con éxito");
		}
	}
	@FXML
	private void volverAlMenu() {
		if (!juegoTerminado && juegoIniciado) {
			Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Seguro que quieres abandonar la partida?");
			if (confirm.showAndWait().get() != ButtonType.OK) return;
		}
		// Obtener la ventana actual y quitando el maximizado de la dificultad dificil
		Stage stage = (Stage) gridTablero.getScene().getWindow();
		stage.setMaximized(false);
		
	    AppShell.getInstance().loadView(View.MENU);
	    AppShell.getInstance().ajustarVentana();
	}

	public void prepararPartidaPersonalizada(int filas, int columnas, int minas) {
		// TODO Auto-generated method stub
		// Alerta Preventiva
		if (columnas > 20 || filas > 15) {
			mostrarAlerta("Recomendación de pantalla", "Has diseñado un tablero grande. Se recomienda jugar en pantalla completa para ver todas las casillas.");
		}
		
		prepararPartida(Nivel.PERSONALIZADO);
		
		// Sobrescribimos con los valores personalizados
		Dificultad personalizada = new Dificultad(0, Nivel.PERSONALIZADO, filas, columnas, minas);
		this.tablero = new Tablero(personalizada);
		
		lblMinas.setText(String.format("💣 %d | 🚩 0/%d", minas, minas));
		lblMinas.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
		
		generarBotones(filas, columnas);
		
		javafx.application.Platform.runLater(() -> {
			Stage stage = (Stage) gridTablero.getScene().getWindow();
			if (!stage.isMaximized()) {
				AppShell.getInstance().ajustarVentana();
			}
			configurarAtajosTeclado();
		});
	}
	private void revelarMinasAnimado() {
		List<Button> botonesConMina = new ArrayList<>();
		
		// Se buscan todos los botones que tienen una mina oculta
		for (Node node : gridTablero.getChildren()) {
			if (node instanceof Button) {
				Button btn = (Button) node;
				Integer c = GridPane.getColumnIndex(btn);
				Integer f = GridPane.getRowIndex(btn);
				if (f != null && c != null) {
					Casilla casilla = tablero.getCeldas()[f][c];
					if (casilla.isEsMina() && !casilla.isRevelada()) {
						botonesConMina.add(btn);
					}
				}
			}
		}
		// Timeline para revelar las casillas
		Timeline timelineAnimacion = new Timeline();
		for (int i = 0; i < botonesConMina.size(); i++) {
			Button btnMina = botonesConMina.get(i);
			KeyFrame frame = new KeyFrame(Duration.millis(50 * i), e -> {
				Integer col = GridPane.getColumnIndex(btnMina);
				Integer fil = GridPane.getRowIndex(btnMina);
				Casilla c = tablero.getCeldas()[fil][col];
				c.setRevelada(true);
				actualizarBotonCasilla(btnMina, c);
			});
			timelineAnimacion.getKeyFrames().add(frame);
		}
		timelineAnimacion.setOnFinished(event -> {
	        vboxFinal.setVisible(true);
	        gridTablero.setOpacity(0.4);
	        animarPanelFinal(); 
	    });
		
		timelineAnimacion.play();
	}

	private void actualizarBotonCasilla(Button btn, Casilla casilla) {
		// TODO Auto-generated method stub
		btn.setGraphic(null);
	    btn.setText("");
	    if (casilla.isRevelada()) {
	        if (casilla.isEsMina()) {
	            btn.setText("💣");
	            btn.setStyle("-fx-background-color: #ff0000; -fx-text-fill: black; -fx-font-weight: bold; -fx-opacity: 1.0;");
	        } else {
	            int minas = casilla.getMinasAlrededor();
	            btn.setText(minas > 0 ? String.valueOf(minas) : "");
	            btn.setStyle("-fx-background-color: #f1f2f6; " +
                        "-fx-border-color: #bdc3c7; " +
                        "-fx-border-width: 0.5px; " +
                        "-fx-font-weight: bold; " + 
                        obtenerColorPorNumero(minas));
	        }
	    } else if (casilla.isMarcada()) {
	        btn.setText("🚩");
	        btn.setStyle("-fx-background-color: #bdc3c7; " +
                    "-fx-border-color: #ecf0f1 #7f8c8d #7f8c8d #ecf0f1; " +
                    "-fx-border-width: 2px; " +
                    "-fx-text-fill: red; -fx-font-weight: bold;");
	    } else {
	        btn.setStyle("-fx-background-color: #bdc3c7; "
	        		+                  "-fx-border-color: #ecf0f1 #7f8c8d #7f8c8d #ecf0f1; " 
	        		+                   "-fx-border-width: 2px;");
	    }
	}
	private void resaltarVecinas (int fila, int col, boolean resaltar) {
		for (int i = fila - 1; i <= fila + 1; i++) {
			for (int j = col - 1; j <= col + 1; j++) {
				if (i >= 0 && i < tablero.getFilas() && j >= 0 && j < tablero.getColumnas() && !(i == fila && j == col)) {
					Casilla vecinaLogica = tablero.getCeldas()[i][j];
					// Solo se resalta si no está revelada ni marcada con bandera
					if (!vecinaLogica.isRevelada() && !vecinaLogica.isMarcada()) {
						for (Node node : gridTablero.getChildren()) {
							if (GridPane.getRowIndex(node) == i && GridPane.getColumnIndex(node) == j) {
								Button btnVecino = (Button) node;
								if (resaltar) {
									btnVecino.setStyle("-fx-background-color: #b0c4de; -fx-border-color: white;");
								} else {
									actualizarBotonCasilla(btnVecino, vecinaLogica);
								}
								break;
							}
						}
					}
				}
			}
		}
	}
	private void lanzarConfeti() {
		Pane root = (Pane) gridTablero.getScene().getRoot();
		Random random = new Random();
		Color[] colores = {Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN, Color.PURPLE, Color.ORANGE, Color.PINK};
		
		for (int i = 0; i < 50; i++) {
			Circle p = new Circle(random.nextInt(5, 10), colores[random.nextInt(colores.length)]);
			
			// Posición inicial
			p.setCenterX(random.nextInt((int) root.getWidth()));
			p.setCenterY(-20);
			root.getChildren().add(p);
			
			// Animación de caída
			TranslateTransition fall = new TranslateTransition(Duration.seconds(random.nextDouble() * 2 + 1), p);
			fall.setToY(root.getHeight() + 40 );
			fall.setByX(random.nextInt(100) - 50); // Movimiento lateral
			
			// Animación de rotación
			ScaleTransition scale = new ScaleTransition(Duration.seconds(0.5), p);
			scale.setToX(0.1);
	        scale.setCycleCount(Animation.INDEFINITE);
	        scale.setAutoReverse(true);
	        
	        // Quitar el confeti
	        fall.setOnFinished(e -> root.getChildren().remove(p));
	        
	        fall.play();
	        scale.play();
		}
	}
	public void configurarAtajosTeclado() {
		// El contenedor puede recibir el teclado
		mainContainer.setFocusTraversable(true);
		
		mainContainer.setOnKeyPressed(event -> {
			// SI el juego ha terminado, solo se puede reinicar
			if (juegoTerminado) {
				if (event.getCode() == KeyCode.R) {
					resetJuego(null);
				}
				return;
			}
			switch (event.getCode()) {
			case R:
				resetJuego(null);
				break;
			case P:
				handlePausa();
				break;
			case ESCAPE:
				volverAlMenu();
				break;
				default:
					break;
			}
		});
		Platform.runLater(() -> mainContainer.requestFocus());
	}
}