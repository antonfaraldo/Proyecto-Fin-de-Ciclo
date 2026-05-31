package dam.proyectofinal.afm.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
	private int idUsuario;
    @Column(name = "nickname", nullable = false, unique = true, length = 50)
	private String nickname;
    @Column(name = "password", nullable = false, length = 255)
	private String password;
    @Column(name = "email", nullable = false, unique = true, length = 100)
	private String email;
    @Column(name = "fecha_registro", nullable = false)
	private LocalDate fechaRegistro;
    @Column(name = "fecha_ultimo_acceso")
	private LocalDateTime fechaUltimoAcceso;
    @Column(name = "activo")
	private boolean activo = false;
    @Column(name = "codigo_activacion", length = 100)
	private String codigoActivacion;
    @Column(name = "fecha_expiracion_codigo")
	private LocalDateTime fechaExpiracionCodigo;
    @Column(name = "token_recuperacion", length = 100)
	private String tokenRecuperacion;
    @Column(name = "fecha_expiracion_token")
	private LocalDateTime fechaExpiracionToken;
    @Column(name = "rol", columnDefinition = "ENUM('ADMIN', 'USER')")
    private String rol = "USER"; // Nota: Asegúrate de crear el Enum o mapearlo como String si usas texto

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "usuarios_logros",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_logro")
    )
    private List<Logro> logros = new ArrayList<>();
	
	
	public String getTokenRecuperacion() {
		return tokenRecuperacion;
	}

	public void setTokenRecuperacion(String tokenRecuperacion) {
		this.tokenRecuperacion = tokenRecuperacion;
	}

	public LocalDateTime getFechaExpiracionToken() {
		return fechaExpiracionToken;
	}

	public void setFechaExpiracionToken(LocalDateTime fechaExpiracionToken) {
		this.fechaExpiracionToken = fechaExpiracionToken;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public String getCodigoActivacion() {
		return codigoActivacion;
	}

	public void setCodigoActivacion(String codigoActivacion) {
		this.codigoActivacion = codigoActivacion;
	}

	public LocalDateTime getFechaExpiracionCodigo() {
		return fechaExpiracionCodigo;
	}

	public void setFechaExpiracionCodigo(LocalDateTime fechaExpiracionCodigo) {
		this.fechaExpiracionCodigo = fechaExpiracionCodigo;
	}

	public LocalDateTime getFechaUltimoAcceso() {
		return fechaUltimoAcceso;
	}

	public void setFechaUltimoAcceso(LocalDateTime fechaUltimoAcceso) {
		this.fechaUltimoAcceso = fechaUltimoAcceso;
	}

	public boolean isEsAdmin() {
		return "admin".equalsIgnoreCase(this.nickname) || "ADMIN".equalsIgnoreCase(this.rol);
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
    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
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
