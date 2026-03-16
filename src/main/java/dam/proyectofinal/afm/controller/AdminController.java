package dam.proyectofinal.afm.controller;

import java.util.Observable;
import java.util.Optional;

import dam.proyectofinal.afm.dao.UsuarioDAO;
import dam.proyectofinal.afm.dao.UsuarioDAOImpl;
import dam.proyectofinal.afm.model.Usuario;
import dam.proyectofinal.afm.util.AppShell;
import dam.proyectofinal.afm.util.View;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminController {
	@FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, String> colNickname;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, String> colFecha;
    @FXML private TextField txtFiltro;
    
    private UsuarioDAO  usuarioDAO = new UsuarioDAOImpl();
    
    // Lista maestra con todos los usuarios
    private ObservableList<Usuario> listaMaestra = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
    	// Se vinculan las columnas con los altributos del usuario
    	colNickname.setCellValueFactory(new PropertyValueFactory<>("nickname"));
    	colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    	colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));
    	
    	cargarUsuarios();
    	configurarFiltro();
    }

	private void configurarFiltro() {
		// TODO Auto-generated method stub
		FilteredList<Usuario> filteredData = new FilteredList<>(listaMaestra, p -> true);
		
		// Se añade un listener
		txtFiltro.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(usuario -> {
				// Filtro vacio, se muestran todos los usuarios
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				String lowerCaseFilter = newValue.toLowerCase();
				
				// Se comprueba si el nickname contiene el texto buscado
				if (usuario.getNickname().toLowerCase().contains(lowerCaseFilter)) {
					return true;
				}
				return false; // No coincide
			});
		});
		
		SortedList<Usuario> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(tablaUsuarios.comparatorProperty());
		// Se actualizan los items de la tabla 
		tablaUsuarios.setItems(sortedData);
	}

	private void cargarUsuarios() {
		// TODO Auto-generated method stub
		// Se obtienen los usuarios y se guardan en la lista
		listaMaestra.setAll(usuarioDAO.obtenerTodos());
		// Se pasa toda la lista a la tabla
		tablaUsuarios.setItems(listaMaestra);
	}
	
	@FXML
	private void handleEliminar() {
		Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
		if (seleccionado != null) {
			// El admin no se puede borrar así mismo
			if (seleccionado.getNickname().equals("admin")) {
				mostrarAlerta("Error", "No puedes eliminar la cuenta de administrador principal.");
                return;
			}
			// Alerta de confirmación
			Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
			confirmacion.setTitle("Confirmar eliminación");
			confirmacion.setHeaderText("¿Estás seguro de que deseas eliminar al usuario?");
			confirmacion.setContentText("Está acción borrará a " + seleccionado.getNickname() + " de forma permanente.");
			
			Optional<ButtonType> resultado = confirmacion.showAndWait();
			if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
				// Si el usuario pulsa aceptar
				usuarioDAO.eliminar(seleccionado.getNickname());
				cargarUsuarios(); // refrescar la tabla
				System.out.println("Usuario eliminado: " + seleccionado.getNickname());
			} else {
				// Si pulsa cancelar o cierra la ventana
				System.out.println("Eliminación cancelada");
			}	
		} else {
			mostrarAlerta("Selección necesaria", "Por favor, selecciona un usuario de la tabla para eliminarlo.");
		}
		}

	private void mostrarAlerta(String titulo, String mensaje) {
		// TODO Auto-generated method stub
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
	}
	@FXML
    private void handleVolver() {
        AppShell.getInstance().loadView(View.MENU);
    }
	
	@FXML
	private void handleVerEstadisticasUsuario() {
		// Se obtiene el usuario seleccionado
		Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
		
		if (seleccionado != null) {
			// Se carga la vista
			EstadisticasController controller = (EstadisticasController) AppShell.getInstance().loadView(View.ESTADISTICAS);
			if (controller != null) {
				controller.cargarDatos(seleccionado);
			}
		} else {
			mostrarAlerta("Selección necesario", "Por favor, selecciona un usuario de la tabla para ver sus estadísticas");
		}
	}
}
