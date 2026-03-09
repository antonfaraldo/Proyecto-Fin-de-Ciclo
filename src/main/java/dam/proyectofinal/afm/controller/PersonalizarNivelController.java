package dam.proyectofinal.afm.controller;

import dam.proyectofinal.afm.util.AppShell;
import dam.proyectofinal.afm.util.View;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class PersonalizarNivelController {
	@FXML private Spinner<Integer> spinFilas;
	@FXML private Spinner<Integer> spinColumnas;
	@FXML private Spinner<Integer> spinMinas;
	
	@FXML public void initialize() {
		// Configuramos los limites del nivel
		spinFilas.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 30, 10));
		spinColumnas.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 50, 10));
        spinMinas.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500, 15));
	}
	
	@FXML 
	private void handleJugar() {
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
	
	@FXML
    private void handleCancelar() {
        // Volver al menu principal
        AppShell.getInstance().loadView(View.MENU);
    }
}
