package dam.proyectofinal.afm.controller;

import dam.proyectofinal.afm.dao.PartidaDAOImpl;
import dam.proyectofinal.afm.model.Nivel;
import dam.proyectofinal.afm.model.Partida;
import dam.proyectofinal.afm.util.AppShell;
import dam.proyectofinal.afm.util.CSVManager;
import dam.proyectofinal.afm.util.View;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

import dam.proyectofinal.afm.dao.PartidaDAO;

public class RankingController {
	private PartidaDAO partidaDao = new PartidaDAOImpl();
	
	@FXML private TableView<Partida> tableFacil;
	@FXML private TableView<Partida> tableMedio;
	@FXML private TableView<Partida> tableDificil;
	
	// Columnas Facil
	 @FXML private 	TableColumn<Partida, String> colUserFacil, colFechaFacil;
	 @FXML private TableColumn<Partida, Integer> colTiempoFacil;
	 
	 // Columnas Medio
	 @FXML private TableColumn<Partida, String> colUserMedio, colFechaMedio;
	 @FXML private TableColumn<Partida, Integer> colTiempoMedio;
	 
	 // Columnas Dificil
	 @FXML private TableColumn<Partida, String> colUserDificil, colFechaDificil;
	 @FXML private TableColumn<Partida, Integer> colTiempoDificil;
	
	@FXML
	public void initialize() {
		// Vinculamos las columnas
		vincularColumnas(colUserFacil, colTiempoFacil, colFechaFacil);
		vincularColumnas(colUserMedio, colTiempoMedio, colFechaMedio);
		vincularColumnas(colUserDificil, colTiempoDificil, colFechaDificil);
		
		// Cargamos los datos en la tabla
		configurarTabla(tableFacil, Nivel.FACIL);
		configurarTabla(tableMedio, Nivel.MEDIO);
		configurarTabla(tableDificil, Nivel.DIFICIL);
	}

	private void vincularColumnas(TableColumn<Partida, String> colUser,TableColumn<Partida, Integer> colTime, TableColumn<Partida, String> colDate) {
		// TODO Auto-generated method stub
		colUser.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsuario().getNickname()));
		colTime.setCellValueFactory(new PropertyValueFactory<>("tiempoSegundos"));
		colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFechaHora().toString()));
	}

	private void configurarTabla(TableView<Partida> table, Nivel nivel) {
		// TODO Auto-generated method stub
		//Esto luego hay que modificarlo
		// Se cargan los datos desde el DAO
		table.getItems().setAll(partidaDao.obtenerRankingTop(nivel, 5));
		
	}
	@FXML
	private void handleVolver() {
		AppShell.getInstance().loadView(View.MENU);
		AppShell.getInstance().ajustarVentana();
	}
	@FXML
	private void handleImportarCSV() {
		FileChooser fc = new FileChooser();
		fc.setTitle("Importar Raking Externo");
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
		
		File selectedFile = fc.showOpenDialog(AppShell.getInstance().getPrimaryStage());
		if (selectedFile != null) {
			List<Partida> importadas = CSVManager.importarPartidas(selectedFile);
			if (!importadas.isEmpty()) {
				for (Partida p : importadas) {
					partidaDao.guardar(p);
				}
				initialize();
			}
		}
	}
}
