package dam.proyectofinal.afm.controller;

import dam.proyectofinal.afm.dao.PartidaDAOImpl;  
import dam.proyectofinal.afm.model.Nivel;
import dam.proyectofinal.afm.model.Partida;
import dam.proyectofinal.afm.util.AppShell;
import dam.proyectofinal.afm.util.CSVManager;
import dam.proyectofinal.afm.util.View;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import dam.proyectofinal.afm.dao.PartidaDAO;

public class RankingController {
	private PartidaDAO partidaDao = new PartidaDAOImpl();
	
	@FXML private TableView<Partida> tableFacil;
	@FXML private TableView<Partida> tableMedio;
	@FXML private TableView<Partida> tableDificil;
	@FXML private TableView<Partida> tableContrarreloj;
	
	@FXML private TextField txtBusqueda;
	@FXML private ComboBox<String> comboPeriodo;
	
	// Columnas Facil
	 @FXML private 	TableColumn<Partida, String> colUserFacil, colFechaFacil;
	 @FXML private TableColumn<Partida, Integer> colTiempoFacil;
	 
	 // Columnas Medio
	 @FXML private TableColumn<Partida, String> colUserMedio, colFechaMedio;
	 @FXML private TableColumn<Partida, Integer> colTiempoMedio;
	 
	 // Columnas Dificil
	 @FXML private TableColumn<Partida, String> colUserDificil, colFechaDificil;
	 @FXML private TableColumn<Partida, Integer> colTiempoDificil;
	 
	// Columnas Contrarreloj
	 @FXML private TableColumn<Partida, String> colUserContra, colFechaContra;
	 @FXML private TableColumn<Partida, Integer> colTiempoContra;
	
	 // Listas maestras
	 private ObservableList<Partida> datosFacil = FXCollections.observableArrayList();
	 private ObservableList<Partida> datosMedio = FXCollections.observableArrayList();
	 private ObservableList<Partida> datosDificil = FXCollections.observableArrayList();
	 private ObservableList<Partida> datosContra = FXCollections.observableArrayList();
	 
	@FXML
	public void initialize() {
		// Vinculamos las columnas
		vincularColumnas(colUserFacil, colTiempoFacil, colFechaFacil);
		vincularColumnas(colUserMedio, colTiempoMedio, colFechaMedio);
		vincularColumnas(colUserDificil, colTiempoDificil, colFechaDificil);
		vincularColumnas(colUserContra, colTiempoContra, colFechaContra);
		
		// Se configura el combovox
		if (comboPeriodo.getItems().isEmpty()) { // Se evitan duplicados tras importar
			comboPeriodo.setItems(FXCollections.observableArrayList("Todos", "Último Mes", "Hoy"));
		}

	    // Aplicar el sistema de filtrado a cada tabla
	    aplicarLogicaDeFiltrado(tableFacil, datosFacil);
	    aplicarLogicaDeFiltrado(tableMedio, datosMedio);
	    aplicarLogicaDeFiltrado(tableDificil, datosDificil);
	    aplicarLogicaDeFiltrado(tableContrarreloj, datosContra);
	    
		// Se resetean filtros visuales para evitaer errores
		txtBusqueda.clear();
		comboPeriodo.setValue("Todos");
  
		// Refrescar datos
		refrescarDatosRanking();
	    
	    // Personalizar nombre de columna para Contrarreloj
	    colTiempoContra.setText("Segundos Sobrantes");
		
	}

	private void refrescarDatosRanking() {
		// TODO Auto-generated method stub
		datosFacil.setAll(partidaDao.obtenerRankingTop(Nivel.FACIL, 0));
	    datosMedio.setAll(partidaDao.obtenerRankingTop(Nivel.MEDIO, 0));
	    datosDificil.setAll(partidaDao.obtenerRankingTop(Nivel.DIFICIL, 0));
	    datosContra.setAll(partidaDao.obtenerRankingTop(Nivel.CONTRARRELOJ, 0));
	}

	private void aplicarLogicaDeFiltrado(TableView<Partida> tabla, ObservableList<Partida> listaMaestra) {
		// TODO Auto-generated method stub
		FilteredList<Partida> filtrados = new FilteredList<>(listaMaestra, p -> true);
		
		// Listener 
		ChangeListener<Object> refrescarFiltro = (obs, oldV, newV) -> {
			// Se obtiene la fecha de hoy fuera del bucle de filtrado
			LocalDate fechaHoy = LocalDate.now();
			String seleccion = comboPeriodo.getValue();
			String texto = txtBusqueda.getText() == null ? "" : txtBusqueda.getText().toLowerCase();
			
			filtrados.setPredicate(partida -> {
				// Filtro por nombre
	            boolean cumpleNombre = texto.isEmpty() || partida.getUsuario().getNickname().toLowerCase().contains(texto);
	            
	            // Filtro por Fecha
	            boolean cumpleFecha = true;
	            if (partida.getFechaHora() == null) return false; 
	            
	            if("Último Mes".equals(seleccion)) {
	            	cumpleFecha = partida.getFechaHora().isAfter(LocalDateTime.now().minusMonths(1));
	            } else if ("Hoy".equals(seleccion)) {
	            	cumpleFecha = partida.getFechaHora().toLocalDate().equals(fechaHoy);
	            }
	            return cumpleNombre && cumpleFecha;
			});
		};
		
		txtBusqueda.textProperty().addListener(refrescarFiltro);
	    comboPeriodo.valueProperty().addListener(refrescarFiltro);
	    
	    refrescarFiltro.changed(null, null, null);
	    
	    SortedList<Partida> ordenados = new SortedList<>(filtrados);
	    ordenados.comparatorProperty().bind(tabla.comparatorProperty());
	    
	    tabla.setItems(ordenados);
	}

	private void vincularColumnas(TableColumn<Partida, String> colUser,TableColumn<Partida, Integer> colTime, TableColumn<Partida, String> colDate) {
		// TODO Auto-generated method stub
		colUser.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsuario().getNickname()));
		colTime.setCellValueFactory(new PropertyValueFactory<>("tiempoSegundos"));
		colDate.setCellValueFactory(data -> { DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		return new SimpleStringProperty(data.getValue().getFechaHora().format(formato));
				});
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
