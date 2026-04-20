package dam.proyectofinal.afm.controller;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

import dam.proyectofinal.afm.dao.UsuarioDAO;
import dam.proyectofinal.afm.dao.UsuarioDAOImpl;
import dam.proyectofinal.afm.model.Usuario;
import dam.proyectofinal.afm.service.EmailService;
import dam.proyectofinal.afm.util.AppShell;
import dam.proyectofinal.afm.util.FiltroNombre;
import dam.proyectofinal.afm.util.View;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class LoginController {
	@FXML private TextField loginNicknameField;
	@FXML private TextField registerNicknameField;
	@FXML private TextField registerEmailField;
	
	@FXML private PasswordField loginPasswordField;
	@FXML private TextField loginPasswordVisibleField; 
	@FXML private Button btnMostrarPassword;
	
	@FXML private PasswordField registerPasswordField;
	@FXML private TextField registerPasswordVisibleField; 
	@FXML private Button btnMostrarRegisterPassword;
	
	@FXML private Label feedbackLabel;
	@FXML private TabPane authTabPane;
	
	@FXML private Label lblMayusAviso;
    @FXML private Label lblMayusAvisoReg;
    
    @FXML private VBox paneVerificarCodigo;
    @FXML private TextField txtCodigoVerificacion;
	
	private boolean isPasswordVisible = false;
	private boolean isRegisterPasswordVisible = false;
	
	private UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
	
	private Usuario usuarioPendiente;
	
	@FXML
	public void initialize() {
		// Se escuchan las teclas del TabPane para qe afecte a amabas pestañas
		authTabPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			int selectedIndex = authTabPane.getSelectionModel().getSelectedIndex();
			Node focusNode = authTabPane.getScene().getFocusOwner();
			
			// Aviso de que el usuario esta escribiendo en mayusculas
			TextInputControl tControl = null;
			if (focusNode instanceof TextInputControl) {
				tControl = (TextInputControl) focusNode;
			}
			
			switch (event.getCode()) {
			// Eje Horizontal
			// Navegacion entre pestañas
			case RIGHT:
				// Solo se cambia de pestaña si se esta en el login y el foco NO esta en un campo de texto
				if (tControl != null) {
					if (tControl.getCaretPosition() == tControl.getText().length()) {
						if (selectedIndex == 0) {
							authTabPane.getSelectionModel().select(1); // Ir al registro
							registerNicknameField.requestFocus();
							event.consume();
						}
					}
				}
						
				break;
				
			case LEFT:
				// Solo se cambia a login si el usuario esta en registro y al inicio del texto
				if (tControl != null) {
                    if (tControl.getCaretPosition() == 0) {
                        if (selectedIndex == 1) {
                            authTabPane.getSelectionModel().select(0);
                            loginNicknameField.requestFocus();
                            event.consume();
                        }
                    }
                }
				break;
			
			// Eje Vertical
			// Navegacion hacia abajo
			case DOWN:
				if (selectedIndex == 0) { // Pestaña del login
					if (loginNicknameField.isFocused()) 
						loginPasswordField.requestFocus();
				} else { // Pestaña de registro
					if (registerEmailField.isFocused())
						registerNicknameField.requestFocus();
					else if (registerNicknameField.isFocused())
						registerPasswordField.requestFocus();	
				}
				event.consume(); // Bloquea el cambio de pestaña con flecha abajo				
				break;
				
			// Navegacion hacia arriba 
			case UP:
				if (selectedIndex == 0) { // Pestaña del login
					if (loginPasswordField.isFocused() || loginPasswordVisibleField.isFocused())
						loginNicknameField.requestFocus();
				} else { // Pestaña de registro
					if (registerPasswordField.isFocused() || registerPasswordVisibleField.isFocused())
						registerNicknameField.requestFocus();
					else if (registerNicknameField.isFocused())
						registerEmailField.requestFocus();
				}
				event.consume();
				break;
				
			case ENTER:
				if (selectedIndex == 0) handleLogin();
				else handleRegister();
				event.consume();
				break;

			default:
				break;
			}
		});
		// Inicializar detectores de mayusculas
		configurarDetectorMayus(loginPasswordField, lblMayusAviso);
		configurarDetectorMayus(loginPasswordVisibleField, lblMayusAviso);
        configurarDetectorMayus(registerPasswordField, lblMayusAvisoReg);
        configurarDetectorMayus(registerPasswordVisibleField, lblMayusAvisoReg);
	}
	
	// Se detecta el estado del bloqueo de mayusculas atraves del estado de la tecla Shift
	private void configurarDetectorMayus(TextInputControl campo, Label aviso) {
		// TODO Auto-generated method stub
		campo.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
			String texto = event.getText();
			// Se comprueba si se ha pulsado una letra
			if (texto != null && texto.length() == 1 && Character.isLetter(texto.charAt(0))) {
				char caracter = texto.charAt(0);
				boolean esMayuscula = Character.isUpperCase(caracter);
				boolean shiftPulsado = event.isShiftDown();
				
				// Lógica de tecla del Shift para saber si es mayuscula
				if ((esMayuscula && !shiftPulsado) || (!esMayuscula && shiftPulsado)) {
					aviso.setVisible(true);
					aviso.setManaged(true);
				} else {
					aviso.setVisible(false);
					aviso.setManaged(false);
				}
			}
		});
		
		// Se oculta el aviso cuando no se esta usando mayusculas
		campo.focusedProperty().addListener((obs, oldVal, newVal) -> {
			if (!newVal) {
				aviso.setVisible(false);
				aviso.setManaged(false);
			}
		});
	}

	@FXML
	private void togglePasswordVisibility() {
		isPasswordVisible = !isPasswordVisible;
		
		if (isPasswordVisible) {
			// Se sincroniza el texto y se muestra el campo visible
			loginPasswordVisibleField.setText(loginPasswordField.getText());
			loginPasswordVisibleField.setVisible(true);
			loginPasswordField.setVisible(false);
			btnMostrarPassword.setText("🔒");
		} else {
			// Sincronizar texto y ocultar la contraseña
			loginPasswordField.setText(loginPasswordVisibleField.getText());
			loginPasswordField.setVisible(true);
			loginPasswordVisibleField.setVisible(false);
			btnMostrarPassword.setText("👁");
		}
	}
	@FXML
	private void toggleRegisterPasswordVisibility() {
		isRegisterPasswordVisible = !isRegisterPasswordVisible;
		
		if (isRegisterPasswordVisible) {
			registerPasswordVisibleField.setText(registerPasswordField.getText());
			registerPasswordVisibleField.setVisible(true);
			registerPasswordField.setVisible(false);
			btnMostrarRegisterPassword.setText("🔒");
		} else {
			registerPasswordField.setText(registerPasswordVisibleField.getText());
			registerPasswordField.setVisible(true);
			registerPasswordVisibleField.setVisible(false);
			btnMostrarRegisterPassword.setText("👁");
		}
	}
	
	@FXML
	private void handleLogin() {
		String nickname = loginNicknameField.getText().trim(); // Con el trim se limpian espacios accidentales delante o detrás
		String password = isPasswordVisible ? loginPasswordVisibleField.getText() : loginPasswordField.getText();
		
		Usuario usuario = usuarioDAO.login(nickname, password);
		
		if (usuario != null) {
			AppShell.getInstance().setUsuario(usuario);
			AppShell.getInstance().loadView(View.MENU);
			AppShell.getInstance().ajustarVentana();
		} else {
			feedbackLabel.setText("Nickname o contraseña incorrectos. O cuenta no activada.");
			feedbackLabel.setStyle("-fx-text-fill: red;");
		}
	}
	@FXML
	private void handleRegister() {
		String nickname = registerNicknameField.getText().trim();
		String email = registerEmailField.getText().trim().toLowerCase(); // se convierte a minusculas
		String password = isRegisterPasswordVisible ? registerPasswordVisibleField.getText() : registerPasswordField.getText();
		
		// Validación de campos vacios
		if (nickname.isEmpty() || email.isEmpty() || password.isEmpty()) {
			feedbackLabel.setText("Por favor, rellena todos los campos");
			feedbackLabel.setStyle("-fx-text-fill: red;");
			return;
		}
		
		// Filtro de insultos y nombres prohibidos
		if (!FiltroNombre.esValido(nickname)) {
			feedbackLabel.setText("Nombre no permitido o contiene insultos.");
			feedbackLabel.setStyle("-fx-text-fill: #e74c3c;");
			return;
		}
		
		// Validacion de correo
		if (!email.contains("@") || !email.contains(".")) {
			feedbackLabel.setText("El formato del email no es válido");
	        feedbackLabel.setStyle("-fx-text-fill: #e74c3c;");
	        return;
		}
		
		// Validación de nickanme duplicado
		if (usuarioDAO.existeNickname(nickname)) {
			feedbackLabel.setText("El nickname " + nickname + " ya está ocupado");
			feedbackLabel.setStyle("-fx-text-fill: #e74c3c;");
	        return;
		}
		
		Usuario nuevoUsuario = new Usuario();
		nuevoUsuario.setNickname(nickname);
		nuevoUsuario.setEmail(email);
		nuevoUsuario.setPassword(password);
		nuevoUsuario.setFechaRegistro(LocalDate.now());
		
		// Se genera el codigo de 6 y digitos y la expiracion
		String codigo = String.format("%06d", new Random().nextInt(999999));
		nuevoUsuario.setCodigoActivacion(codigo);
		nuevoUsuario.setFechaExpiracionCodigo(LocalDateTime.now().plusMinutes(15));
		nuevoUsuario.setActivo(false);
		
		if (usuarioDAO.registrar(nuevoUsuario)) {
			this.usuarioPendiente = nuevoUsuario; // SE guarda la referencia para la validacion
			new EmailService().enviarCorreoActivacion(email, codigo);
			
			// Se muestra el panel de verificación
			paneVerificarCodigo.setVisible(true);
			paneVerificarCodigo.setManaged(true);
			
			feedbackLabel.setStyle("-fx-text-fill: green;");
			feedbackLabel.setText("Registro con éxito. Ya puedes iniciar sesión");
			
			// Se limpian los campos después del registro
			registerNicknameField.clear();
	        registerEmailField.clear();
	        registerPasswordField.clear();
	        
			authTabPane.getSelectionModel().select(0);
		} else {
			feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Error: El email ya está registrado.");
		}
	}
	
	@FXML
	private void handleValidarCodigo() {
		String codigoIntroducido = txtCodigoVerificacion.getText().trim();
		
		if (usuarioPendiente != null && codigoIntroducido.equals(usuarioPendiente.getCodigoActivacion())) {
			if (LocalDateTime.now().isBefore(usuarioPendiente.getFechaExpiracionCodigo())) {
				usuarioPendiente.setActivo(true);
				
				// Se oculta el panel
				paneVerificarCodigo.setVisible(false);
				paneVerificarCodigo.setManaged(false);
				
				feedbackLabel.setStyle("-fx-text-fill: green;");
				feedbackLabel.setText("¡Cuenta activada con éxito! Ya puedes entrar.");
				authTabPane.getSelectionModel().select(0); // Se mueve a la pestaña del login
			} else {
				feedbackLabel.setText("El código ha expirado (validez 15 min).");
			}
		} else {
			feedbackLabel.setText("Código de verificación incorrecto.");
		}
	}
	
	@FXML
	private void handleCancelarVerificacion() {
		paneVerificarCodigo.setVisible(false);
		paneVerificarCodigo.setManaged(false);
		usuarioPendiente = null;
		txtCodigoVerificacion.clear();
	}
	
	@FXML
	private void handleRecuperarPassword() {
		// Por implementar aun
	}
}
