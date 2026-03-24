package dam.proyectofinal.afm.controller;

import dam.proyectofinal.afm.model.Dificultad;
import dam.proyectofinal.afm.model.Nivel;
import dam.proyectofinal.afm.model.Usuario;
import dam.proyectofinal.afm.util.AppShell;
import dam.proyectofinal.afm.util.View;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

public class MenuController {
	@FXML private Label lblBienvenida;
	@FXML private Button btnAdmin;
	@FXML private Button btnInfoContra;
	
	@FXML
	public void initialize() {
		if (AppShell.getInstance().getUsuario() != null) {
			String nombre = AppShell.getInstance().getUsuario().getNickname();
			
			lblBienvenida.setText("Bienvenido, " + nombre);
		}
		boolean esAdmin = AppShell.getInstance().getUsuario().getNickname().equalsIgnoreCase("admin");
		btnAdmin.setVisible(esAdmin);
		btnAdmin.setManaged(esAdmin);
		
		configurarToolTipInfo();
	}
	
	private void configurarToolTipInfo() {
		// TODO Auto-generated method stub 
		Tooltip tip = new Tooltip(
				"MODO CONTRARRELOJ:\n" +
						"• Tienes un tiempo límite que baja.\n" +
						"• Revelar casillas te da segundos extra.\n" +
						"• ¡Haz clic para leer la guía completa online!"
				);  
		// Aparece rápido y dura bastante para que de tiempo a leer
		tip.setShowDelay(Duration.millis(300));
		tip.setShowDuration(Duration.seconds(10));
		
		if (btnInfoContra != null) {
			btnInfoContra.setTooltip(tip);
		}
	}
	
	@FXML
	private void handleAbrirGuiaWeb() {
		// URL provisional
		String url = "https://github.com/antonfaraldo/proyecto-fin-de-ciclo"; 
		
		// Abrir el navegador predeterminado del sistema
		if (AppShell.getInstance().getHostServices() != null) {
			AppShell.getInstance().getHostServices().showDocument(url);
		} else {
			System.out.println("Enlace a la guía: " + url);
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
			AppShell.getInstance().loadView(View.LOGROS);
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
	@FXML
	private void handleIrAdmin() {
	    AppShell.getInstance().loadView(View.ADMIN);
	}
	@FXML
	private void handleVerEstadisticas() {
		EstadisticasController controller = (EstadisticasController) AppShell.getInstance().loadView(View.ESTADISTICAS);
		Usuario usuarioActual = AppShell.getInstance().getUsuario();
		if (controller != null && usuarioActual != null) {
			controller.cargarDatos(usuarioActual);
		}
	}
	@FXML
	private void handleJugarContrarreloj() {
		// Se carga la vista
		GameController controller = (GameController) AppShell.getInstance().loadView(View.GAME);
		controller.prepararPartida(Nivel.CONTRARRELOJ);
	}
}
