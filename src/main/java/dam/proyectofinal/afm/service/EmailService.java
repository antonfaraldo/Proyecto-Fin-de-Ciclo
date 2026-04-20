package dam.proyectofinal.afm.service;

import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailService {
	// Configuración del servidor
	private final String remitente = "anton.estudios.11@gmail.com";
	private final String password = "phdhptlzozxrjmgi"; // Contraseña de aplicación
	private final String host = "smtp.gmail.com";
	
	public void enviarCorreo(String destinatario, String asunto, String cuerpo) {
		// Se configuran las propiedades del servidor SMTP
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true"); // Seguridad TLS
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        
     // Solución para la ADVERTENCIA amarilla de la consola
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        
        // Se crea la sesión con autenticación
        Session session = Session.getInstance(props, new Authenticator() {
        	@Override
        	protected PasswordAuthentication getPasswordAuthentication() {
        		return new PasswordAuthentication(remitente, password);
        	}
        });
        try {
        	Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);
            message.setText(cuerpo);
            
            Transport.send(message);
            System.out.println("DEBUG: ¡Correo enviado con éxito!");
        } catch (Exception e) {
			// TODO: handle exception
        	System.err.println("ERROR: No se pudo enviar el correo: " + e.getMessage());
            e.printStackTrace();
		}
        	
	}
	// DEBUG
	public static void main(String[] args) {
        EmailService service = new EmailService();
        // Prueba
        service.enviarCorreo("antonfaraldomosquera@liceolapaz.net", "Prueba MineManager", "¡Funciona! El sistema de recuperación está listo.");
    }
	
	public void enviarCorreoActivacion(String destinatario, String codigo) {
		String asunto = "Activa ty cuenta en Mine Manager";
		String cuerpo = "¡Hola! Gracias por registrarte.\n\n" + 
						"Tu código dr activación es: " + codigo + "\n" + 
						"Este código expirará en 15 minutos";
		enviarCorreo(destinatario, asunto, cuerpo);
	}

}
