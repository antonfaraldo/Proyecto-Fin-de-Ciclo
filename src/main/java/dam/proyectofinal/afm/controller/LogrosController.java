package dam.proyectofinal.afm.controller;

import java.util.List;

import dam.proyectofinal.afm.dao.LogroDAO;
import dam.proyectofinal.afm.dao.LogroDAOImpl;
import dam.proyectofinal.afm.model.Logro;
import dam.proyectofinal.afm.model.Usuario;
import dam.proyectofinal.afm.util.AppShell;
import dam.proyectofinal.afm.util.View;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
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
		VBox vbox = new VBox(12);
		vbox.setAlignment(Pos.CENTER);
		// vbox.setPrefSize(150, 150);
        
        // 	El texto no toca los bordes
        vbox.setPadding(new Insets(15));
		
        ImageView iconView = new ImageView();
        iconView.setFitWidth(55);
        iconView.setFitHeight(55);
        iconView.setPreserveRatio(true);
        
        
        try {
        	String rutaImagen;
        if(logro.isDesbloqueado()) {
        	rutaImagen = "/images/LogroDesbloqueado.png"; // Estrella significa desbloqueado
        	// Tarjeta blanca para conseguidos
			vbox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5); -fx-border-color: #f1c40f; -fx-border-radius: 10; -fx-border-width: 1;");
        	iconView.setOpacity(1.0); // Visible
        } else {
        	rutaImagen = "/images/LogroBloqueado.png"; // Candado para bloqueado
        	// Tarjeta grisácea para bloqueados
			vbox.setStyle("-fx-background-color: #ececec; -fx-background-radius: 10; -fx-border-color: #ccc; -fx-border-radius: 10; -fx-border-width: 1;");
            iconView.setOpacity(0.3); // Transparente
        }
        iconView.setImage(new Image(getClass().getResourceAsStream(rutaImagen)));
        } catch (Exception e) {
			// TODO: handle exception
        	System.out.println("No se pudo cargar la imagen del logro: " + e.getMessage());
        	// Que se muestren los emojis si falla la imagen
        	Label lblFallback = new Label(logro.isDesbloqueado() ? "⭐" : "🔒");
            lblFallback.setStyle("-fx-font-size: 30px;");
            iconView.setVisible(false);
		}
        
        Label name = new Label(logro.getNombre().toUpperCase());
        name.setStyle("-fx-font-weight: bold; -fx-text-alignment: center;-fx-text-fill: black;");
        name.setWrapText(true);
        name.setPrefHeight(40); 
        name.setMaxHeight(40);
        name.setMaxWidth(130);
        name.setAlignment(Pos.CENTER);

        Label desc = new Label(logro.getDescripcion());
        desc.setStyle("-fx-font-size: 11px; -fx-text-fill: #777; -fx-text-alignment: center;");
        desc.setWrapText(true);
        desc.setPrefHeight(45);
        desc.setMaxHeight(45);
        desc.setMaxWidth(130);
        desc.setAlignment(Pos.TOP_CENTER);

        vbox.getChildren().addAll(name, iconView, desc);
        
		return vbox;
	}
	@FXML
	private void volverMenu() {
		AppShell.getInstance().loadView(View.MENU);
	} 

}