package dam.proyectofinal.afm.util;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Toast {
	public static void show (Stage ownerStage, String titulo, String mensaje) {
		Stage toastStage = new Stage();
		toastStage.initOwner(ownerStage);
		toastStage.setResizable(false);
		toastStage.initStyle(StageStyle.TRANSPARENT);
		
		// Diseño del Toast
		Label lblIcon = new Label("🏆");
		lblIcon.setFont(Font.font("System", FontWeight.BOLD, 24));
		
		Label lblTitle = new Label(titulo);
        lblTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label lblMsg = new Label(mensaje);
        lblMsg.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 12px;");
        
        VBox vText = new VBox(lblTitle, lblMsg);
        HBox root = new HBox(15, lblIcon, vText);
        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(15));
        // Fondo oscuro redondeado con sombra
        root.setStyle("-fx-background-color: rgba(44, 62, 80, 0.9); -fx-background-radius: 10; " +
                     "-fx-border-color: #3498db; -fx-border-radius: 10; -fx-border-width: 2;");
        
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        toastStage.setScene(scene);
        
        // Se posiciona en la esquina inferior derecha
        toastStage.setOnShown(e -> {
            toastStage.setX(ownerStage.getX() + ownerStage.getWidth() - toastStage.getWidth() - 20);
            toastStage.setY(ownerStage.getY() + ownerStage.getHeight() - toastStage.getHeight() - 20);
        });
        
        // Animación de entrada y salida
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), root);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(3)); // Se queda visible 3 segundos
        fadeOut.setOnFinished(e -> toastStage.close());

        toastStage.show();
        fadeIn.play();
        fadeOut.play();
	}

}
