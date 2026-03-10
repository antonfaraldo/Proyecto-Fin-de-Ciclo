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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
		
        ImageView iconView = new ImageView();
        iconView.setFitWidth(50);
        iconView.setFitHeight(50);
        iconView.setPreserveRatio(true);
        
        try {
        	String rutaImagen;
        if(logro.isDesbloqueado()) {
        	rutaImagen = "/images/LogroDesbloqueado.png"; // Estrella significa desbloqueado
        	vbox.setOpacity(1.0); // Visible
        } else {
        	rutaImagen = "/images/LogroBloqueado.png"; // Candado para bloqueado
            vbox.setOpacity(0.6); // Transparente
        }
        iconView.setImage(new Image(getClass().getResourceAsStream(rutaImagen)));
        } catch (Exception e) {
			// TODO: handle exception
        	System.out.println("No se pudo cargar la imagen del logro: " + e.getMessage());
		}
        
        Label name = new Label(logro.getNombre());
        name.setStyle("-fx-font-weight: bold; -fx-text-alignment: center;");
        name.setWrapText(true);

        Label desc = new Label(logro.getDescripcion());
        desc.setStyle("-fx-font-size: 11px; -fx-text-fill: #777; -fx-text-alignment: center;");
        desc.setWrapText(true);

        vbox.getChildren().addAll(iconView, name, desc);
        
		return vbox;
	}
	@FXML
	private void volverMenu() {
		AppShell.getInstance().loadView(View.MENU);
	} 

}
