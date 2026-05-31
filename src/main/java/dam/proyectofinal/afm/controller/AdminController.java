package dam.proyectofinal.afm.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Observable;
import java.util.Optional;

import dam.proyectofinal.afm.dao.PartidaDAO;
import dam.proyectofinal.afm.dao.PartidaDAOImpl;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class AdminController {
	@FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, String> colNickname;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, String> colFecha;
    @FXML private TableColumn<Usuario, LocalDateTime> colUltimoAcceso;
    @FXML private TextField txtFiltro;

    @FXML private Button btnEliminar;
    @FXML private Button btnVerStats;
    @FXML private Button btnVolver;
    @FXML private Button btnCambiarRol;

    @FXML private Label lblTotalUsuarios;

    @FXML private VBox panePrincipal;
    @FXML private VBox paneConfirmacion;
    @FXML private Label lblConfirmarTitulo;
    @FXML private Label lblConfirmarMensaje;
    @FXML private Button btnAceptarConfirmacion;

    @FXML private VBox paneAviso;
    @FXML private Label lblAvisoTitulo;
    @FXML private Label lblAvisoMensaje;

    private UsuarioDAO  usuarioDAO = new UsuarioDAOImpl();
    private PartidaDAO partidaDAO = new PartidaDAOImpl();

    // Lista maestra con todos los usuarios
    private ObservableList<Usuario> listaMaestra = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
    	// Se vinculan las columnas con los altributos del usuario
    	colNickname.setCellValueFactory(new PropertyValueFactory<>("nickname"));
    	colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    	colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));
    	colUltimoAcceso.setCellValueFactory(new PropertyValueFactory<>("fechaUltimoAcceso"));

    	// Se formatea la fecha
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    	colUltimoAcceso.setCellFactory(column -> new TableCell<Usuario, LocalDateTime>() {
    		@Override
    		protected void updateItem(LocalDateTime item, boolean empty) {
    			super.updateItem(item, empty);

    			// SI la fila esta vacia porque no hay usuario no se pone ningun texto
    			if (empty) {
    				setText(null);
    				setGraphic(null);
    				}
    			// SI hay usuario pero el campo es null porque se ha registrado pero nunca ha iniciado sesion
    			else if (item == null) {
    				setText("Nunca");
    				setStyle("-fx-text-fill: #e67e22; -fx-font-style: italic;");
    			}
    			// Se formatea la fecha
    			else {
    				setText(item.format(formatter));
    				setStyle("-fx-text-fill: black; -fx-font-style: normal;");
    			}
			}
    	});

    	configurarTooltips();

    	cargarUsuarios();
    	configurarFiltro();

    	int total = usuarioDAO.obtenerTotalUsuarios();
    	lblTotalUsuarios.setText("Usuarios totales: " + total);
    }

	private void configurarTooltips() {
		// TODO Auto-generated method stub
		// ToolTIp para el buscador
		Tooltip tipFiltro = new Tooltip("Escribe un nickname para filtrar la lista en tiempo real");
        tipFiltro.setShowDelay(Duration.millis(300));
        txtFiltro.setTooltip(tipFiltro);

        // Tooltip para los botones
        if (btnEliminar != null) {
        	Tooltip tipEliminar = new Tooltip("Borra permanentemente al usuario seleccionado");
        	tipEliminar.setShowDelay(Duration.millis(300));
        	btnEliminar.setTooltip(tipEliminar);
        }
        if (btnVerStats != null) {
            Tooltip tipStats = new Tooltip("Abre el panel detallado de estadísticas del usuario");
            tipStats.setShowDelay(Duration.millis(300));
            btnVerStats.setTooltip(tipStats);
        }
        if (btnVolver != null) {
            btnVolver.setTooltip(new Tooltip("Regresar al menú principal"));
        }
        if (btnCambiarRol != null) {
            Tooltip tipRol = new Tooltip("Alterna los permisos del usuario seleccionado (ADMIN <=> USER)");
            tipRol.setShowDelay(Duration.millis(300));
            btnCambiarRol.setTooltip(tipRol);
        }
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

    private void mostrarAvisoPersonalizado(String titulo, String mensaje) {
        lblAvisoTitulo.setText(titulo);
        lblAvisoMensaje.setText(mensaje);

        panePrincipal.setOpacity(0.3);
        panePrincipal.setDisable(true);
        paneAviso.setVisible(true);
    }
    @FXML
    private void handleCerrarAviso() {
        paneAviso.setVisible(false);
        panePrincipal.setOpacity(1.0);
        panePrincipal.setDisable(false);
    }
    private void mostrarConfirmacionPersonalizada(String titulo, String mensaje, Runnable accionConfirmada) {
        lblConfirmarTitulo.setText(titulo);
        lblConfirmarMensaje.setText(mensaje);

        btnAceptarConfirmacion.setOnAction(e -> {
            accionConfirmada.run(); // Ejecuta el borrado o cambio de rol
            ocultarConfirmacion();
        });

        panePrincipal.setOpacity(0.3);
        panePrincipal.setDisable(true);
        paneConfirmacion.setVisible(true);
    }
    @FXML
    private void handleCancelarConfirmacion() {
        ocultarConfirmacion();
    }

    private void ocultarConfirmacion() {
        paneConfirmacion.setVisible(false);
        panePrincipal.setOpacity(1.0);
        panePrincipal.setDisable(false);
    }

	@FXML
	private void handleEliminar() {
		Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
		if (seleccionado != null) {
            // El admin no se puede borrar así mismo
            if (seleccionado.getNickname().equals("admin")) {
                mostrarAvisoPersonalizado("🚫 ACCIÓN DENEGADA", "No puedes eliminar la cuenta de administrador principal.");
                return;
            }
            mostrarConfirmacionPersonalizada(
                "⚠️ ELIMINAR USUARIO",
                "¿Estás seguro de que deseas eliminar permanentemente a " + seleccionado.getNickname() + "? Esta acción no se puede deshacer.",
                () -> {
                    partidaDAO.eliminarPartidasPorUsuario(seleccionado.getNickname());
                    usuarioDAO.eliminar(seleccionado.getNickname());
                    cargarUsuarios();

                    int nuevoTotal = usuarioDAO.obtenerTotalUsuarios();
                    lblTotalUsuarios.setText("Usuarios totales: " + nuevoTotal);
                    System.out.println("Usuario eliminado: " + seleccionado.getNickname());
                }
            );
        } else {
            mostrarAvisoPersonalizado("Selección necesaria", "Por favor, selecciona un usuario de la tabla para eliminarlo.");
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
    @FXML
    private void handleCambiarRol() {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {
            // Seguridad, el admin principal no puede ser cambiado a user
            if (seleccionado.getNickname().equals("admin")) {
                mostrarAlerta("Acción denegada", "No es posible alterar el rol de la cuenta de administrador raíz.");
                return;
            }
            String rolActual = seleccionado.getRol() != null ? seleccionado.getRol().toUpperCase() : "USER";
            String nuevoRol = rolActual.equals("ADMIN") ? "USER" : "ADMIN";

            mostrarConfirmacionPersonalizada(
                    "⚙️ CAMBIAR ROL DE USUARIO",
                    "¿Deseas modificar los permisos de " + seleccionado.getNickname() + "? Pasará de rango: [" + rolActual + " ➔ " + nuevoRol + "]",
                    () -> {
                        if (usuarioDAO.cambiarRol(seleccionado.getNickname(), nuevoRol)) {
                            System.out.println("SISTEMA: Rol actualizado en BD para " + seleccionado.getNickname());
                            cargarUsuarios();
                            tablaUsuarios.getSelectionModel().select(seleccionado);
                        } else {
                            mostrarAvisoPersonalizado("Error de Persistencia", "No se pudo actualizar el rol debido a un fallo en el servidor.");
                        }
                    }
            );
        } else {
            mostrarAvisoPersonalizado("Selección necesaria", "Por favor, selecciona un usuario de la tabla para cambiar su rol.");
        }
    }
}
