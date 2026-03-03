package dam.proyectofinal.afm.model;

public class Logro {
	private int idLogro;
	private String nombre;
	private String descripcion;
	private int tiempoObjetivo;
	
	public Logro() {}

	public Logro(int idLogro, String nombre, String descripcion, int tiempoObjetivo) {
		super();
		this.idLogro = idLogro;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.tiempoObjetivo = tiempoObjetivo;
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
	
	

}
