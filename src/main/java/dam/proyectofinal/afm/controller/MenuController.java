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
	@FXML private Button btnRanking;
	
	@FXML
	public void initialize() {
		if (AppShell.getInstance().getUsuario() != null) {
			String nombre = AppShell.getInstance().getUsuario().getNickname();
			
			lblBienvenida.setText("Bienvenido, " + nombre);
		}
		boolean esAdmin = AppShell.getInstance().getUsuario().getNickname().equalsIgnoreCase("admin");
		btnAdmin.setVisible(esAdmin);
		btnAdmin.setManaged(esAdmin);
		
		configurarToolTips();
	}
	
	private void configurarToolTips() {
		// TODO Auto-generated method stub 
		// Tooltip para el Modo Contrarreloj
		Tooltip tipContra = new Tooltip(
				"MODO CONTRARRELOJ:\n" +
						"• Tienes un tiempo límite que baja.\n" +
						"• Revelar casillas te da segundos extra.\n" +
						"• ¡Haz clic para leer la guía completa online!"
				);  
		// Aparece rápido y dura bastante para que de tiempo a leer
		tipContra.setShowDelay(Duration.millis(300));
		tipContra.setShowDuration(Duration.seconds(10));
		
		if (btnInfoContra != null) {
			btnInfoContra.setTooltip(tipContra);
		}
		
		// Tooltip para el Ranking
		Tooltip tipRanking = new Tooltip(
				"RANKING GLOBAL:\n" +
				"• Consulta los mejores tiempos de la comunidad.\n" +
				"• Filtra por dificultad o periodos de tiempo (Hoy/Mes).\n" +
				"• ¡Compite por entrar en el Top 10!"
				);
		tipRanking.setShowDelay(Duration.millis(300));
		tipRanking.setShowDuration(Duration.seconds(10));
		
		if (btnRanking != null) {
			btnRanking.setTooltip(tipRanking);
		}
	}
	
	@FXML
	private void handleAbrirGuiaWeb() {
		// URL provisional
		String url = "https://antonfaraldo.github.io/Proyecto-Fin-de-Ciclo/"; 
		
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
