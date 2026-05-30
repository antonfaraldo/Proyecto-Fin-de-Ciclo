package dam.proyectofinal.afm.model;

import jakarta.persistence.*;

@Entity
@Table(name = "logros")
public class Logro {
    @Id
    @Column(name = "id_logro")
	private int idLogro;
    @Column(name = "nombre", nullable = false, unique = true, length = 100)
	private String nombre;
    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
	private String descripcion;
    @Column(name = "tiempo_objetivo")
	private int tiempoObjetivo;
    @Transient
	private boolean desbloqueado;
	
	public Logro() {}

	public Logro(int idLogro, String nombre, String descripcion, int tiempoObjetivo, boolean desbloqueado) {
		super();
		this.idLogro = idLogro;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.tiempoObjetivo = tiempoObjetivo;
		this.desbloqueado = desbloqueado;
	}

	public int getIdLogro() {
		return idLogro;
	}

	public void setIdLogro(int idLogro) {
		this.idLogro = idLogro;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getTiempoObjetivo() {
		return tiempoObjetivo;
	}

	public void setTiempoObjetivo(int tiempoObjetivo) {
		this.tiempoObjetivo = tiempoObjetivo;
	}
	
	public boolean isDesbloqueado() {
        return desbloqueado;
    }

    public void setDesbloqueado(boolean desbloqueado) {
        this.desbloqueado = desbloqueado;
    }
	

}
