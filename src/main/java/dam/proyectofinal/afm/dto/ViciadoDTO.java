package dam.proyectofinal.afm.dto;

public class ViciadoDTO {
	private String nickname;
	private int tiempoTotal;
	
	public ViciadoDTO(String nickname, int tiempoTotal) {
		this.nickname = nickname;
		this.tiempoTotal = tiempoTotal;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public int getTiempoTotal() {
		return tiempoTotal;
	}
	public void setTiempoTotal(int tiempoTotal) {
		this.tiempoTotal = tiempoTotal;
	}
	
	

}
