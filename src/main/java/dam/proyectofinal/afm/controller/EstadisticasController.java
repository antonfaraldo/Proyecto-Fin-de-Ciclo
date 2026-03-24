package dam.proyectofinal.afm.controller;

import java.util.Map; 

import dam.proyectofinal.afm.dao.PartidaDAOImpl;
import dam.proyectofinal.afm.model.Nivel;
import dam.proyectofinal.afm.model.Usuario;
import dam.proyectofinal.afm.util.AppShell;
import dam.proyectofinal.afm.util.View;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class EstadisticasController {
	@FXML private Label lblTitulo, lblRatioTotal, lblTiempoTotal, lblNivelFavorito;
	@FXML private Label lblPctFacil, lblPctMedio, lblPctDificil, lblPctContra;
	@FXML private ProgressBar barFacil, barMedio, barDificil, barContra;
	@FXML private Button btnAdminPanel;
	@FXML private PieChart chartNiveles;
	
	
	private PartidaDAOImpl partidaDAO = new PartidaDAOImpl();
	private Usuario usuarioMostrado;
	
	public void cargarDatos(Usuario usuario) {
		this.usuarioMostrado = usuario;
		lblTitulo.setText("Estadísticas de " + usuario.getNickname());
		
		Usuario logueado = AppShell.getInstance().getUsuario();
		// Lógica de visibilidad para el Admin
		boolean esAdmin = logueado != null && logueado.isEsAdmin();
		btnAdminPanel.setVisible(esAdmin);
        btnAdminPanel.setManaged(esAdmin);
        
        // El gráfico solo es visible y ocupa espacio si el que mira es admin
        chartNiveles.setVisible(esAdmin);
        chartNiveles.setManaged(esAdmin);
		
		Map<String, Object> stats = partidaDAO.obtenerEstadisticasCompletas(usuario);
		
		lblRatioTotal.setText(String.format("Ratio de Victorias: %.1f%% (%d partidas)",
				(double)stats.get("ratioGlobal"), (long)stats.get("total")));
		
		int segs = (int)stats.get("tiempoTotal");
		lblTiempoTotal.setText(String.format("Tiempo total: %02d:%02d:%02d",
				segs / 3600, (segs % 3600) / 60, segs % 60));
		
		Nivel fav = (Nivel)stats.get("favorito");
		lblNivelFavorito.setText("Nivel Favorito: " + (fav != null ? fav.name() : "N/A"));
		
		Map<Nivel, Double> pcts = (Map<Nivel, Double>) stats.get("porcentajesNivel");
		Map<Nivel, Long> conteos = (Map<Nivel, Long>) stats.get("conteoNiveles");
		
		// Si es admin se cargan los datos en el PieChart
		if (esAdmin && conteos != null) {
            actualizarGraficoPie(conteos);
        }
        
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
        
        // Modo Contrarreloj
        double pContra = pcts.getOrDefault(Nivel.CONTRARRELOJ, 0.0);
        lblPctContra.setText(String.format("Modo Contrarreloj: %.1f%%", pContra));
        barContra.setProgress(pContra / 100.0);
		
		// SI el usuario es admin se ajusta la ventana
        if (esAdmin) {
        	Platform.runLater(() ->
        	AppShell.getInstance().ajustarVentana()
        	);
        }
	}
	private void actualizarGraficoPie(Map<Nivel, Long> conteos) {
		// TODO Auto-generated method stub
		ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
		
		// Se recorren los niveles para crear las porciones del gráfico
		for (Map.Entry<Nivel, Long> entry : conteos.entrySet()) {
	        if (entry.getValue() > 0) { 
	            // Mostramos el nombre del nivel y el total de veces jugado
	            String textoPorcion = entry.getKey().name() + " (" + entry.getValue() + ")";
	            pieData.add(new PieChart.Data(textoPorcion, entry.getValue().doubleValue()));
	        }
	    }
		chartNiveles.setData(pieData);
		
		// Se asignan colores fijos
		for (PieChart.Data data : chartNiveles.getData()) {
			Node node = data.getNode();
			
			String nombreNivelRaw = data.getName().split(" ")[0];
			String colorHex = "#bdc3c7"; // Por defecto gris
			
			try {
				Nivel nivel = Nivel.valueOf(nombreNivelRaw);
				
				// Se asignan el color según el Nivel
				switch (nivel) {
	                case FACIL:
	                	colorHex = "#4CAF50"; // Verde
	                    break;
	                case MEDIO:
	                	colorHex = "#FF9800"; // Amarillo/Naranja
	                    break;
	                case DIFICIL:
	                	colorHex = "#F44336"; // Rojo
	                    break;
	                case CONTRARRELOJ:
	                	colorHex = "#9b59b6"; // Morado
	                    break;
	                case PERSONALIZADO:
	                	colorHex = "#3498db"; // Azul
	                	break;
				} 
				// Se aplica el color a la porción
				if (node != null) {
	                node.setStyle("-fx-pie-color: " + colorHex + ";");
	            }
				// Color de la leyenda
				final String finalColor = colorHex;
				final String nombreData = data.getName();
				
	            Platform.runLater(() -> {
	            	// Se buscan todos los items
	                for (Node item : chartNiveles.lookupAll(".chart-legend-item")) {
	                    if (item instanceof Label) {
	                        Label labelLeyenda = (Label) item;
	                        // Si el texto coincide, se pinta su símbolo
	                        if (labelLeyenda.getText().equals(nombreData)) {
	                        	Node simbolo = labelLeyenda.getGraphic();
	                        	if (simbolo != null) {
	                        		simbolo.setStyle("-fx-background-color: " + finalColor + ";");
	                        	}
	                        }
	                    }
	                }
	            });
			} catch (IllegalArgumentException e) {
				// TODO: handle exception
				
			}
		}
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
