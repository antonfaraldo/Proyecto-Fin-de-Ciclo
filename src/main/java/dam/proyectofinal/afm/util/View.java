package dam.proyectofinal.afm.util;

public enum View {
	GAME("/fxml/game-view.fxml"),
	LOGIN("/fxml/login-view.fxml"),
	MENU("/fxml/menu-view.fxml"),
	RANKING("/fxml/ranking-view.fxml"),
	LOGROS("/fxml/logros-view.fxml"),
	PERSONALIZAR("/fxml/personalizarnivel-view.fxml"),
	ADMIN("/fxml/admin-view.fxml"),
	ESTADISTICAS("/fxml/estadisticas-view.fxml");
	
	private final String fxmlPath;

	View(String fxmlPath) {
		// TODO Auto-generated constructor stub
		this.fxmlPath = fxmlPath;
	}
	public String getFxmlPath() {
		return fxmlPath;
	}

}