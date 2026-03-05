package dam.proyectofinal.afm.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import dam.proyectofinal.afm.model.Usuario;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class AppShell {
	private static AppShell instance;
	private BorderPane mainLayout;
	private Stage primaryStage;
	private Map<View, Object> controllers = new HashMap<>();
	
	private Usuario usuario;
	
	private AppShell() {}
	
	public static AppShell getInstance() {
		if (instance == null) {
			instance = new AppShell();
		}
		return instance;
	}
	
	public void init(Stage stage) {
		this.mainLayout = new BorderPane();
		this.primaryStage = stage;
		// Se ajusta el tamaño
		Scene scene = new Scene(mainLayout, 800, 600);
		stage.setScene(scene);
		stage.setTitle("Mine Manager Pro");
		stage.show();
	}
	public Object loadView(View view) {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(view.getFxmlPath()));
		try {
			Parent viewNode = fxmlLoader.load();
			
			// Setear la nueva vista
			primaryStage.getScene().setRoot(viewNode);
			
			// Se guarda el controlador
			Object controller = fxmlLoader.getController();
			controllers.put(view, controller);
			
			return controller;
		} catch (IOException e) {
			// TODO: handle exception
			throw new RuntimeException("Error al cargar la vista " + view, e);
		}
	}
	public void ajustarVentana() {
		if (primaryStage != null) {
			primaryStage.sizeToScene(); // La ventana se ajusta ala tamaño del Layout
			primaryStage.centerOnScreen(); // Se centra la ventana 
		}
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	

}
