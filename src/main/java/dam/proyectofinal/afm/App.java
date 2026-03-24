package dam.proyectofinal.afm;

import dam.proyectofinal.afm.model.Dificultad;
import dam.proyectofinal.afm.model.Tablero;
import dam.proyectofinal.afm.util.AppShell;
import dam.proyectofinal.afm.util.View;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application{
//	public static void main(String[] args) {
//		// Dificultad de prueba (8x8 con 10 minas)
//		Dificultad prueba = new Dificultad();
//		prueba.setFilas(8);
//		prueba.setColumnas(8);
//		prueba.setNumMinas(10);
//		
//		// Creación del Tablero
//		Tablero pruebaTablero = new Tablero(prueba);
//		
//		// Mostramos el resultado
//		System.out.println("---Prueba de Tablero Buscaminas---");
//		pruebaTablero.imprimirTableroConsola();
//	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		AppShell.getInstance().setHostServices(this.getHostServices());
		
		AppShell.getInstance().init(primaryStage);
		// Cargamos la primera vista
		AppShell.getInstance().loadView(View.LOGIN);
		AppShell.getInstance().ajustarVentana();
		
	}
	public static void main(String[] args) {
		launch(args);
	}

}
