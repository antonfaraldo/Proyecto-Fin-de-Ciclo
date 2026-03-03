package dam.proyectofinal.afm.model;

public class Dificultad {
	private int idDificultad;
	private Nivel nivel;
	private int filas;
	private int columnas;
	private int numMInas;
	
	public Dificultad() {}

	public Dificultad(int idDificultad, Nivel nivel, int filas, int columnas, int numMInas) {
		super();
		this.idDificultad = idDificultad;
		this.nivel = nivel;
		this.filas = filas;
		this.columnas = columnas;
		this.numMInas = numMInas;
	}

	public int getIdDificultad() {
		return idDificultad;
	}

	public void setIdDificultad(int idDificultad) {
		this.idDificultad = idDificultad;
	}

	public Nivel getNivel() {
		return nivel;
	}

	public void setNivel(Nivel nivel) {
		this.nivel = nivel;
	}

	public int getFilas() {
		return filas;
	}

	public void setFilas(int filas) {
		this.filas = filas;
	}

	public int getColumnas() {
		return columnas;
	}

	public void setColumnas(int columnas) {
		this.columnas = columnas;
	}

	public int getNumMInas() {
		return numMInas;
	}

	public void setNumMInas(int numMInas) {
		this.numMInas = numMInas;
	}
	
	
}
