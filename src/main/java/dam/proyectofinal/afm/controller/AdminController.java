package dam.proyectofinal.afm.controller;

import dam.proyectofinal.afm.dao.UsuarioDAO;
import dam.proyectofinal.afm.dao.UsuarioDAOImpl;
import dam.proyectofinal.afm.model.Usuario;
import dam.proyectofinal.afm.util.AppShell;
import dam.proyectofinal.afm.util.View;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminController {
	@FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, String> colNickname;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, String> colFecha;
    
    private UsuarioDAO  usuarioDAO = new UsuarioDAOImpl();

    @FXML
    public void initialize() {
    	// Se vinculan las columnas con los altributos del usuario
    	colNickname.setCellValueFactory(new PropertyValueFactory<>("nickname"));
    	colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    	colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));
    	
    	cargarUsuarios();
    }

	private void cargarUsuarios() {
		// TODO Auto-generated method stub
		tablaUsuarios.getItems().setAll(usuarioDAO.obtenerTodos());
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
			usuarioDAO.eliminar(seleccionado.getNickname());
			cargarUsuarios(); // refrescar la tabla
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
