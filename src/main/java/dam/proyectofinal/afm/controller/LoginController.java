package dam.proyectofinal.afm.controller;


import java.time.LocalDate;

import dam.proyectofinal.afm.dao.UsuarioDAO;
import dam.proyectofinal.afm.dao.UsuarioDAOImpl;
import dam.proyectofinal.afm.model.Usuario;
import dam.proyectofinal.afm.util.AppShell;
import dam.proyectofinal.afm.util.View;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

public class LoginController {
	@FXML private TextField loginNicknameField;
	@FXML private TextField registerNicknameField;
	@FXML private TextField registerEmailField;
	
	@FXML private PasswordField loginPasswordField;
	@FXML private PasswordField registerPasswordField;
	
	@FXML private Label feedbackLabel;
	@FXML private TabPane authTabPane;
	
	private UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
	
	@FXML
	private void handleLogin() {
		String nickname = loginNicknameField.getText().trim(); // Con el trim se limpian espacios accidentales delante o detrás
		String password = loginPasswordField.getText();
		
		Usuario usuario = usuarioDAO.login(nickname, password);
		
		if (usuario != null) {
			AppShell.getInstance().setUsuario(usuario);
			AppShell.getInstance().loadView(View.MENU);
			AppShell.getInstance().ajustarVentana();
		} else {
			feedbackLabel.setText("Nickname o contraseña incorrectos.");
			feedbackLabel.setStyle("-fx-text-fill: red;");
		}
	}
	@FXML
	private void handleRegister() {
		String nickname = registerNicknameField.getText().trim();
		String email = registerEmailField.getText().trim().toLowerCase(); // se convierte a minusculas
		String password = registerPasswordField.getText();
		
		if (nickname.isEmpty() || email.isEmpty() || password.isEmpty()) {
			feedbackLabel.setText("Por favor, rellena todos los campos");
			feedbackLabel.setStyle("-fx-text-fill: red;");
			return;
		}
		// Validacion de correo
		if (!email.contains("@") || !email.contains(".")) {
			feedbackLabel.setText("El formato del email no es válido");
	        feedbackLabel.setStyle("-fx-text-fill: #e74c3c;");
	        return;
		}
		
		Usuario nuevoUsuario = new Usuario();
		nuevoUsuario.setNickname(nickname);
		nuevoUsuario.setEmail(email);
		nuevoUsuario.setPassword(password);
		nuevoUsuario.setFechaRegistro(LocalDate.now());
		
		if (usuarioDAO.registrar(nuevoUsuario)) {
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
	
	
}
