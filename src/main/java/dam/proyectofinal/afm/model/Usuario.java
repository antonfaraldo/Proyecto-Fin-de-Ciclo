package dam.proyectofinal.afm.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Usuario {
	private int idUsuario;
	private String nickname;
	private String password;
	private String email;
	private LocalDate fechaRegistro;
	private LocalDateTime fechaUltimoAcceso;
	
	
	public LocalDateTime getFechaUltimoAcceso() {
		return fechaUltimoAcceso;
	}

	public void setFechaUltimoAcceso(LocalDateTime fechaUltimoAcceso) {
		this.fechaUltimoAcceso = fechaUltimoAcceso;
	}

	public boolean isEsAdmin() {
		return "admin".equalsIgnoreCase(this.nickname);
	}
	
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public List<Logro> getLogros() {
		return logros;
	}
	public void setLogros(List<Logro> logros) {
		this.logros = logros;
	}
	private List<Logro> logros = new ArrayList<>();
	
	public int getId() {
		return idUsuario;
	}
	public void setId(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public LocalDate getFechaRegistro() {
		return fechaRegistro;
	}
	public void setFechaRegistro(LocalDate fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}
	public Usuario(int idUsuario, String nickname, String password, String email, LocalDate fechaRegistro) {
		super();
		this.idUsuario = idUsuario;
		this.nickname = nickname;
		this.password = password;
		this.email = email;
		this.fechaRegistro = fechaRegistro;
		this.logros = new ArrayList<>();
		
		inicializarLogrosPorDefecto();
	}
	private void inicializarLogrosPorDefecto() {
		// TODO Auto-generated method stub
	}
	public Usuario() {}
}
