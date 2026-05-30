package dam.proyectofinal.afm.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.*;

@Entity
@Table(name = "dificultades")
public class Dificultad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dificultad")
	private int idDificultad;
    @Column(name = "nivel", nullable = false)
    @Enumerated(EnumType.STRING)
	private Nivel nivel;
    @Column(name = "filas", nullable = false)
	private int filas;
    @Column(name = "columnas", nullable = false)
	private int columnas;
    @Column(name = "num_minas", nullable = false)
	private int numMinas;
	
	public Dificultad() {}

	public Dificultad(int idDificultad, Nivel nivel, int filas, int columnas, int numMinas) {
		super();
		this.idDificultad = idDificultad;
		this.nivel = nivel;
		this.filas = filas;
		this.columnas = columnas;
		this.numMinas = numMinas;
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

	public int getNumMinas() {
		return numMinas;
	}

	public void setNumMinas(int numMinas) {
		this.numMinas = numMinas;
	}
	
	
}
