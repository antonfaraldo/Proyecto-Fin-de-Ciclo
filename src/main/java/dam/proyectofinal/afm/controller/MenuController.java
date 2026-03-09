package dam.proyectofinal.afm.controller;

import dam.proyectofinal.afm.model.Nivel;
import dam.proyectofinal.afm.util.AppShell;
import dam.proyectofinal.afm.util.View;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MenuController {
	@FXML private Label lblBienvenida;
	@FXML
	public void initialize() {
		if (AppShell.getInstance().getUsuario() != null) {
			String nombre = AppShell.getInstance().getUsuario().getNickname();
			
			lblBienvenida.setText("Bienvenido, " + nombre);
		}
	}
	
	@FXML private void handleJugarFacil() { iniciarJuego(Nivel.FACIL);}
	@FXML private void handleJugarMedio() {iniciarJuego(Nivel.MEDIO);}
	@FXML private void handleJugarDificil() {iniciarJuego(Nivel.DIFICIL);}

	private void iniciarJuego(Nivel nivel) {
		// TODO Auto-generated method stub
		GameController gameController = (GameController) AppShell.getInstance().loadView(View.GAME);
		gameController.prepararPartida(nivel);
	}

	@FXML
	private void handleVerLogros() {
			System.out.println("Cargando vitrina de logros...");
	}
	@FXML
	private void handleVerRanking() {
		System.out.println("Cargando ranking desde CSV...");
		AppShell.getInstance().loadView(View.RANKING);
	}
	@FXML
	private void handleCerrarSesion() {
		AppShell.getInstance().setUsuario(null);
		AppShell.getInstance().loadView(View.LOGIN);
		AppShell.getInstance().ajustarVentana();
	}
	@FXML
	private void handlePersonalizar() {
		// Cambiamos a la vista de configuracion personalizada
		AppShell.getInstance().loadView(View.PERSONALIZAR);
	}
}
