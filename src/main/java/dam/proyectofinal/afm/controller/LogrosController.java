package dam.proyectofinal.afm.controller;

import java.util.List;

import dam.proyectofinal.afm.dao.LogroDAO;
import dam.proyectofinal.afm.dao.LogroDAOImpl;
import dam.proyectofinal.afm.model.Logro;
import dam.proyectofinal.afm.model.Usuario;
import dam.proyectofinal.afm.util.AppShell;
import dam.proyectofinal.afm.util.View;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class LogrosController {
	@FXML private TilePane tilePaneLogros;
	private LogroDAO logroDAO = new LogroDAOImpl();
	@FXML
	public void initialize() {
		cargarLogros();
	}
	private void cargarLogros() {
		// TODO Auto-generated method stub
		tilePaneLogros.getChildren().clear();
		
		// Se obtienen todos los logros posibles 
		List<Logro> todosLosLogros = logroDAO.listarTodos();
		Usuario usuarioActual = AppShell.getInstance().getUsuario();
		
		if (todosLosLogros != null) {
			for (Logro logro : todosLosLogros) {
				// Se comprueba si el usuario ya tiene ese logro
				boolean conseguido = logroDAO.tieneUsuarioLogro(usuarioActual, logro.getNombre());
				logro.setDesbloqueado(conseguido);
				
				VBox card = crearTarjetaLogro(logro);
				tilePaneLogros.getChildren().add(card);
			}
		}
		
	}
	private VBox crearTarjetaLogro(Logro logro) {
		// TODO Auto-generated method stub
		VBox vbox = new VBox(10);
		vbox.setAlignment(Pos.CENTER);
		vbox.setPrefSize(150, 150);
		
		// Estilo básico de la tarjeta
		String estiloBase = "-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);";
        vbox.setStyle(estiloBase);
		
        Label icon = new Label(logro.isDesbloqueado() ? "⭐" : "🔒");
        icon.setStyle("-fx-font-size: 40px;");

        Label name = new Label(logro.getNombre());
        name.setStyle("-fx-font-weight: bold; -fx-text-alignment: center;");
        name.setWrapText(true);

        Label desc = new Label(logro.getDescripcion());
        desc.setStyle("-fx-font-size: 11px; -fx-text-fill: #777; -fx-text-alignment: center;");
        desc.setWrapText(true);

        vbox.getChildren().addAll(icon, name, desc);
        
        // SI esta bloqueado sepone transparente
        if (!logro.isDesbloqueado()) {
        	vbox.setOpacity(0.5);
        }
        
		return vbox;
	}
	@FXML
	private void volverMenu() {
		AppShell.getInstance().loadView(View.MENU);
	} 

}
