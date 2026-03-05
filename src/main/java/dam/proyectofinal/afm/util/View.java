package dam.proyectofinal.afm.util;

public enum View {
	GAME("/dam/proyectofinal/afm/view/game-view.fxml"),
	LOGIN("/dam/proyectofinal/afm/view/login-view.fxml"),
	MENU("/dam/proyectofinal/view/menu-view.fxml");
	
	private final String fxmlPath;

	View(String fxmlPath) {
		// TODO Auto-generated constructor stub
		this.fxmlPath = fxmlPath;
	}
	public String getFxmlPath() {
		return fxmlPath;
	}

}