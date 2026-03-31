package dam.proyectofinal.afm.controller;

import dam.proyectofinal.afm.dao.PartidaDAOImpl;
import dam.proyectofinal.afm.dto.ViciadoDTO;
import dam.proyectofinal.afm.model.Nivel;
import dam.proyectofinal.afm.model.Partida;
import dam.proyectofinal.afm.util.AppShell;
import dam.proyectofinal.afm.util.CSVManager;
import dam.proyectofinal.afm.util.View;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
	
	@FXML private TabPane tabPaneRanking;
    @FXML private Tab tabViciados;
    @FXML private TableView<ViciadoDTO> tableViciados;
    @FXML private TableColumn<ViciadoDTO, String> colUserViciado;
    @FXML private TableColumn<ViciadoDTO, String> colTiempoViciado;
    @FXML private ComboBox<Nivel> comboNivelViciados;
	
	@FXML private CheckBox checkTop10; 	
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
		
		// Listener para el Top 10
		checkTop10.selectedProperty().addListener((obs, oldV, newV) -> {
			txtBusqueda.setText(txtBusqueda.getText());
		});

	    // Aplicar el sistema de filtrado a cada tabla
	    aplicarLogicaDeFiltrado(tableFacil, datosFacil);
	    aplicarLogicaDeFiltrado(tableMedio, datosMedio);
	    aplicarLogicaDeFiltrado(tableDificil, datosDificil);
	    aplicarLogicaDeFiltrado(tableContrarreloj, datosContra);
	    
		// Se resetean filtros visuales para evitaer errores
		txtBusqueda.clear();
		comboPeriodo.setValue("Todos");
		checkTop10.setSelected(true); // Valor por defecto al entrar 
  
		// Refrescar datos
		refrescarDatosRanking();
	    
	    // Personalizar nombre de columna para Contrarreloj
	    colTiempoContra.setText("Segundos Sobrantes");
	    
	    // Tabla de los jugadores con más minutos jugados restringida al admin
	    if (!AppShell.getInstance().getUsuario().isEsAdmin()) {
	    	tabPaneRanking.getTabs().remove(tabViciados);
	    } else {
	    	configurarTablaViciados();
	    }
	    
		
	}

	private void configurarTablaViciados() {
		// TODO Auto-generated method stub
		colUserViciado.setCellValueFactory(new PropertyValueFactory<>("nickname"));
		// Vinculación de timepo formateado
		colTiempoViciado.setCellValueFactory(data -> {
			int totalSegundos = data.getValue().getTiempoTotal();
			int minutos = totalSegundos / 60;
			int segundos = totalSegundos % 60;
			
			String formato = (minutos > 0) ? minutos + "m " + segundos + "s" : segundos + "s";
	        return new SimpleStringProperty(formato);
		});
        
        // Cargar combo de niveles
        ObservableList<Nivel> niveles = FXCollections.observableArrayList();
        niveles.add(null); // Todos los niveles
        niveles.addAll(Nivel.FACIL, Nivel.MEDIO, Nivel.DIFICIL, Nivel.CONTRARRELOJ);
        
        comboNivelViciados.setItems(niveles);
        
        comboNivelViciados.setCellFactory(lb -> new ListCell<Nivel>() {
        	@Override
        	protected void updateItem(Nivel item, boolean empty) {
        		super.updateItem(item, empty);
        		setText(empty || item == null? "Todos los niveles" : item.toString());
        	}
        });
        comboNivelViciados.setButtonCell(comboNivelViciados.getCellFactory().call(null));

        // Listener para actualizar la tabla al cambiar el combo
        comboNivelViciados.valueProperty().addListener((obs, oldV, newV) -> {
            tableViciados.getItems().setAll(((PartidaDAOImpl)partidaDao).obtenerRankingViciados(newV));
        });

        // Carga inicial
        tableViciados.getItems().setAll(((PartidaDAOImpl)partidaDao).obtenerRankingViciados(null));
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
	    
	    // Se actualiza la tabla según el checkbox
	    Runnable actualizarVistaRecortada = () -> {
	    	if (checkTop10.isSelected()) {
	    		// Para tener solo el top 10 se sublista la lista filtrada y se ordena
	    		int limite = Math.min(ordenados.size(), 10);
	    		tabla.setItems(FXCollections.observableArrayList(ordenados.subList(0, limite)));
	    	} else {
	    		// Se muestran todas las partidas
	    		tabla.setItems(ordenados);
	    	}
	    };
	    
	    ordenados.addListener((ListChangeListener<? super Partida>) c -> actualizarVistaRecortada.run());
	    checkTop10.selectedProperty().addListener((o, ov, nv) -> actualizarVistaRecortada.run());
	    
	    // Se ejecuta por primera vez
	    actualizarVistaRecortada.run();
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
