package dam.proyectofinal.afm.model;

import java.time.LocalDate;

public class Usuario {
	private int idUsuario;
	private String nickname;
	private String password;
	private String email;
	private LocalDate fechaRegistro;
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
	}
	public Usuario() {}
}
