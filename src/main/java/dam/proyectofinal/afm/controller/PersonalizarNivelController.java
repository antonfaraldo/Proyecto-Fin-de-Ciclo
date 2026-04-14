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
import javafx.scene.layout.VBox;

public class PersonalizarNivelController {
	@FXML private Spinner<Integer> spinFilas;
	@FXML private Spinner<Integer> spinColumnas;
	@FXML private Spinner<Integer> spinMinas;
	
	@FXML private Label lblInfoMinas;
	@FXML private Label lblSugerenciaMinas;
	@FXML private Label lblAvisoMinas;
	
	@FXML private VBox paneAlerta;
	
	@FXML public void initialize() {
		// Configuramos los limites del nivel
		spinFilas.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 30, 10));
		spinColumnas.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 50, 10));
        spinMinas.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 750, 15));
        
        // Validación para evitar letras
        configurarValidacionNumerica(spinFilas);
        configurarValidacionNumerica(spinColumnas);
        configurarValidacionNumerica(spinMinas);
        
        // Se añade un Listener a filas y columnas para actualizar el aviso
        spinFilas.valueProperty().addListener((obs, oldVal, newVal) -> { 
        	if (newVal != null) {
        			actualizarAvisoMinas();
        		}
        	});
        spinColumnas.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                actualizarAvisoMinas();
            }
        });
        
        // Listener para el spinner de minas
        spinMinas.valueProperty().addListener((obs, oldVal, newVal) -> {
        	int maxPermitido = (int) ((spinFilas.getValue() * spinColumnas.getValue()) * 0.5);
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
		int totalCasillas = filas * columnas;
		
		// Se calcula el minimo de minas que son 2 o el 2% del tablero
		int min = Math.max(2, (int) (totalCasillas * 0.02));
		
		// Se calcula el 50%
		int max = (int) ((filas * columnas) * 0.5);
		
		// Se calcula el 20% que es la sugerencia estandar 
		int sugerencia = (int) ((filas * columnas) * 0.2);
		
		// Se actualiza el limite del spinner en tiempo real
		SpinnerValueFactory.IntegerSpinnerValueFactory factory = 
				(SpinnerValueFactory.IntegerSpinnerValueFactory) spinMinas.getValueFactory();
		
		if (factory != null) {
			factory.setMin(min);
	        factory.setMax(max);
	        
	        // Evitamos que el valor actual quede por fuera de los limites
	        if (spinMinas.getValue() != null) {
	            if (spinMinas.getValue() > max) factory.setValue(max);
	            if (spinMinas.getValue() < min) factory.setValue(min);
	        }
	    }
		
		// Se actualiza el texto de Label
		lblAvisoMinas.setText("Mínimo de minas: " + min + " (2% del tablero)");
		lblInfoMinas.setText("Límite de seguridad: Máximo " + max + " minas (50% del tablero)");
		lblSugerenciaMinas.setText("Recomendado (estándar 20%): " + sugerencia + " minas");
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
		paneAlerta.setVisible(true);
	}
	
	@FXML 
	private void ocultarAlerta() {
		paneAlerta.setVisible(false);
	}
	
	@FXML 
	private void confirmarJugar() {
		int f = spinFilas.getValue();
		int c = spinColumnas.getValue();
		int m = spinMinas.getValue();
		
		// Se carga la vista
		GameController game = (GameController) AppShell.getInstance().loadView(View.GAME);
		game.prepararPartidaPersonalizada(f, c, m);
	}
	
	@FXML
    private void handleCancelar() {
        // Volver al menu principal
        AppShell.getInstance().loadView(View.MENU);
    }
}
