package dam.proyectofinal.afm.controller;

import java.util.Optional;

import dam.proyectofinal.afm.util.AppShell;
import dam.proyectofinal.afm.util.View;
import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class PersonalizarNivelController {
	@FXML private Spinner<Integer> spinFilas;
	@FXML private Spinner<Integer> spinColumnas;
	@FXML private Spinner<Integer> spinMinas;
	
	@FXML private Label lblInfoMinas;
	
	@FXML public void initialize() {
		// Configuramos los limites del nivel
		spinFilas.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 30, 10));
		spinColumnas.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 50, 10));
        spinMinas.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500, 15));
        
        // Validación para evitar letras
        configurarValidacionNumerica(spinFilas);
        configurarValidacionNumerica(spinColumnas);
        configurarValidacionNumerica(spinMinas);
        
        // Se añade un Listener a filas y columnas para actualizar el aviso
        spinFilas.valueProperty().addListener((obs, oldVal, newVal) -> actualizarAvisoMinas());
        spinColumnas.valueProperty().addListener((obs, oldVal, newVal) -> actualizarAvisoMinas());
        
        // Listener para el spinner de minas
        spinMinas.valueProperty().addListener((obs, oldVal, newVal) -> {
        	int maxPermitido = (int) ((spinFilas.getValue() * spinColumnas.getValue()) * 0.8);
        	if (newVal > maxPermitido) {
        		lblInfoMinas.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        	} else {
        		lblInfoMinas.setStyle("-fx-text-fill: #7f8c8d; -fx-font-weight: normal;");
        	}
        });
        
        actualizarAvisoMinas();
	}
	
	private void actualizarAvisoMinas() {
		// TODO Auto-generated method stub
		// Se obtienen los valores actuales de los Spinners
		int filas = spinFilas.getValue();
		int columnas = spinColumnas.getValue();
		
		// Se calcula el 80%
		int max = (int) ((filas * columnas) * 0.8);
		
		// Se actualiza el texto de Label
		lblInfoMinas.setText("Límite de seguridad: Máximo " + max + " minas (80% del tablero)");
	}

	private void configurarValidacionNumerica(Spinner<Integer> spinner) {
		// TODO Auto-generated method stub
		spinner.setEditable(true); // Se permite editar los valores
		
		// Editor de texto
		TextField editor = spinner.getEditor();
		
		// Filtro que solo permite numeros
		editor.setTextFormatter(new TextFormatter<>(change -> {
			if (change.getControlNewText().matches("\\d*")) {
				return change; // Si es numero se valida
			}
			return null; // Si es una letra o simbolo mo se valida 
		} ));
	}

	@FXML 
	private void handleJugar() {
		// Alerta de Información
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Nivel Personalizada");
		alert.setHeaderText("Aviso de Ranking");
		alert.setContentText("Has seleccionado un nivel personalizado. Ten en cuenta que estas partidas NO se guardarán en el ranking global.\n\n¿Deseas continuar?");
		
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
		
			// Se obtienen los valores seleccionados por el usuario
			int f = spinFilas.getValue();
			int c = spinColumnas.getValue();
			int m = spinMinas.getValue();
			
			// Calculamos el límite de seguridad (las minas no pueden ser más del 80% del tablero)
			int maxMinasPermitidas = (int) ((f * c) * 0.8);
			
			// Validacion de seguridad
			if (m > maxMinasPermitidas) {
				m = maxMinasPermitidas;
			}
			// Se carga la vista
			GameController game = (GameController) AppShell.getInstance().loadView(View.GAME);
	        game.prepararPartidaPersonalizada(f, c, m);
		}
	}
	
	@FXML
    private void handleCancelar() {
        // Volver al menu principal
        AppShell.getInstance().loadView(View.MENU);
    }
}
