package dam.proyectofinal.afm.controller;

import java.util.Map; 

import dam.proyectofinal.afm.dao.PartidaDAOImpl;
import dam.proyectofinal.afm.model.Nivel;
import dam.proyectofinal.afm.model.Usuario;
import dam.proyectofinal.afm.util.AppShell;
import dam.proyectofinal.afm.util.View;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class EstadisticasController {
	@FXML private Label lblTitulo, lblRatioTotal, lblTiempoTotal, lblNivelFavorito;
	@FXML private Label lblPctFacil, lblPctMedio, lblPctDificil;
	@FXML private ProgressBar barFacil, barMedio, barDificil;
	@FXML private Button btnAdminPanel;
	
	private PartidaDAOImpl partidaDAO = new PartidaDAOImpl();
	private Usuario usuarioMostrado;
	
	public void cargarDatos(Usuario usuario) {
		this.usuarioMostrado = usuario;
		lblTitulo.setText("Estadísticas de " + usuario.getNickname());
		
		Usuario logueado = AppShell.getInstance().getUsuario();
	    if (logueado != null && logueado.isEsAdmin()) {
	        btnAdminPanel.setVisible(true);
	        btnAdminPanel.setManaged(true);
	    }
		
		Map<String, Object> stats = partidaDAO.obtenerEstadisticasCompletas(usuario);
		
		lblRatioTotal.setText(String.format("Ratio de Victorias: %.1f%% (%d partidas)",
				(double)stats.get("ratioGlobal"), (long)stats.get("total")));
		
		int segs = (int)stats.get("tiempoTotal");
		lblTiempoTotal.setText(String.format("Tiempo total: %02d:%02d:%02d",
				segs / 3600, (segs % 3600) / 60, segs % 60));
		
		Nivel fav = (Nivel)stats.get("favorito");
		lblNivelFavorito.setText("Nivel Favorito: " + (fav != null ? fav.name() : "N/A"));
		
		Map<Nivel, Double> pcts = (Map<Nivel, Double>) stats.get("porcentajesNivel");
        
        // Nivel Fácil
        double pFacil = pcts.get(Nivel.FACIL);
        lblPctFacil.setText(String.format("Nivel Fácil: %.1f%%", pFacil));
        barFacil.setProgress(pFacil / 100.0);
        
        // Nivel Medio
        double pMedio = pcts.get(Nivel.MEDIO);
        lblPctMedio.setText(String.format("Nivel Medio: %.1f%%", pMedio));
        barMedio.setProgress(pMedio / 100.0);
        
        // Nivel Difícil
        double pDificil = pcts.get(Nivel.DIFICIL);
        lblPctDificil.setText(String.format("Nivel Difícil: %.1f%%", pDificil));
        barDificil.setProgress(pDificil / 100.0);
		
		
	}
	@FXML 
	private void handleVolverMenu() {
	    AppShell.getInstance().loadView(View.MENU);
	    AppShell.getInstance().ajustarVentana();
	}

	@FXML 
	private void handleIrAdmin() {
	    AppShell.getInstance().loadView(View.ADMIN);
	    AppShell.getInstance().ajustarVentana();
	}
}
