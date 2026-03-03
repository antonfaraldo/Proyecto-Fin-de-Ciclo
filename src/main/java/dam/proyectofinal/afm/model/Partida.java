package dam.proyectofinal.afm.model;

import java.time.LocalDateTime;

public class Partida {
	private int idPartida;
	private int tiempoSegundos;
	private LocalDateTime fechaHora;
	private boolean victoria;
	
	//Relaciones del diagrama
	private Usuario usuario;
	private Dificultad dificultad;

	public Partida() {}

	public Partida(int idPartida, int tiempoSegundos, LocalDateTime fechaHora, boolean victoria, Usuario usuario,
			Dificultad dificultad) {
		super();
		this.idPartida = idPartida;
		this.tiempoSegundos = tiempoSegundos;
		this.fechaHora = fechaHora;
		this.victoria = victoria;
		this.usuario = usuario;
		this.dificultad = dificultad;
	}

	public int getIdPartida() {
		return idPartida;
	}

	public void setIdPartida(int idPartida) {
		this.idPartida = idPartida;
	}

	public int getTiempoSegundos() {
		return tiempoSegundos;
	}

	public void setTiempoSegundos(int tiempoSegundos) {
		this.tiempoSegundos = tiempoSegundos;
	}

	public LocalDateTime getFechaHora() {
		return fechaHora;
	}

	public void setFechaHora(LocalDateTime fechaHora) {
		this.fechaHora = fechaHora;
	}

	public boolean isVictoria() {
		return victoria;
	}

	public void setVictoria(boolean victoria) {
		this.victoria = victoria;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Dificultad getDificultad() {
		return dificultad;
	}

	public void setDificultad(Dificultad dificultad) {
		this.dificultad = dificultad;
	}
	
	
}
