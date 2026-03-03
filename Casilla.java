package dam.proyectofinal.afm.model;

public class Casilla {
	private boolean esMina; // Indica si la casilla contiene una bomba, Si es true cuando el jugador la pulse se acaba la partida
	private boolean revelada; 
	private boolean marcada; // Representa la bandera
	private int minasAlrededor; // Indica el número entre el0 y el 8 de minas que hay en las casillas vecinas
	
	public Casilla() {
		this.esMina = false; // por defecto todas las casillas son minas hasta que el algoritmo las coloque
		this.revelada = false; // Todas las casillas empiezan tapadas
		this.marcada = false; // No hay banderas puestas al inicio
		this.minasAlrededor = 0; // El contador empieza en cero
	}
	
// Este constructor lo usare mayomente para hacer pruebas por ejemplo comprobar si funciona una casilla con mina
	public Casilla(boolean esMina, boolean revelada, boolean marcada, int minasAlrededor) {
		super();
		this.esMina = esMina;
		this.revelada = revelada;
		this.marcada = marcada;
		this.minasAlrededor = minasAlrededor;
	}


	public boolean isEsMina() {
		return esMina;
	}

	public void setEsMina(boolean esMina) {
		this.esMina = esMina;
	}

	public boolean isRevelada() {
		return revelada;
	}

	public void setRevelada(boolean revelada) {
		this.revelada = revelada;
	}

	public boolean isMarcada() {
		return marcada;
	}

	public void setMarcada(boolean marcada) {
		this.marcada = marcada;
	}

	public int getMinasAlrededor() {
		return minasAlrededor;
	}

	public void setMinasAlrededor(int minasAlrededor) {
		this.minasAlrededor = minasAlrededor;
	}
	
	
}
