package dam.proyectofinal.afm.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "partidas")
public class Partida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_partida")
	private int idPartida;
    @Column(name = "tiempo_segundos", nullable = false)
	private int tiempoSegundos;
    @Column(name = "fecha_hora", nullable = false)
	private LocalDateTime fechaHora;
    @Column(name = "victoria", nullable = false)
	private boolean victoria;
    @Column(name = "num_banderas_usadas")
	private int numBanderasUsadas;
	
	//Relaciones del diagrama
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
	private Usuario usuario;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dificultad", nullable = false)
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

	public int getNumBanderasUsadas() {
		return numBanderasUsadas;
	}

	public void setNumBanderasUsadas(int numBanderasUsadas) {
		this.numBanderasUsadas = numBanderasUsadas;
	}

	
}
