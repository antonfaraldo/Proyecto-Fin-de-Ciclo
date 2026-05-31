package dam.proyectofinal.afm.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import dam.proyectofinal.afm.model.Dificultad;
import dam.proyectofinal.afm.model.Nivel;
import dam.proyectofinal.afm.model.Partida;
import dam.proyectofinal.afm.model.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class CSVManager {
	private static final String ficheroPartidas = "historial.csv";
	private static final DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	
	public static void exportarPartida(Partida partida) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(ficheroPartidas, true))) {
			// Se define un formato para la fecha 
			String fechaStr = partida.getFechaHora().format(formato);
				
				String datos = String.format("%s;%s;%d;%b;%s%n",
						partida.getUsuario().getNickname(),
						partida.getDificultad().getNivel(),
						partida.getTiempoSegundos(),
						partida.isVictoria(),
                        fechaStr
						);
				writer.write(datos);
				System.out.println("Datos guardados en CSV: " + datos);
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static List<Partida> importarPartidas(File archivoSeleccionado) {
		// TODO Auto-generated method stub
		List<Partida> partidas = new ArrayList<>();
		
		File archivoLeer = (archivoSeleccionado != null) ? archivoSeleccionado : new File(ficheroPartidas);
		
		if (!archivoLeer.exists()) return partidas;
		
		try (BufferedReader br = new BufferedReader(new FileReader(archivoLeer));
            Session session = HibernateUtil.getSessionFactory().openSession()) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue; // Se saltan lineas vacias

                String[] datos = linea.split(";");
                if (datos.length >= 5) {
                    try {
                        Partida p = new Partida();

                        String nickCSV = datos[0].trim();
                        Usuario user = session.createQuery("FROM Usuario WHERE lower(nickname) = lower(:n)", Usuario.class)
                                .setParameter("n", nickCSV).uniqueResult();

                        if (user == null) {
                            user = new Usuario();
                            user.setNickname(nickCSV);
                            user.setEmail(nickCSV.toLowerCase() + "@minemanager.local");
                            user.setPassword("$2a$10$mRgnPdSshQPQR6ks06DNiO0.z.t0uF8kOsnS5E7pTf.WnSsh6q.6G"); // Clave temporal "1234"
                            user.setFechaRegistro(LocalDate.now());
                            user.setActivo(true);

                            Transaction txUser = session.beginTransaction();
                            session.persist(user);
                            txUser.commit();
                        }
                        p.setUsuario(user);

                        Nivel nivel = Nivel.valueOf(datos[1].toUpperCase().trim());

                        int filas = 8, columnas = 8, minas = 10;
                        if (nivel == Nivel.MEDIO) {
                            filas = 16;
                            columnas = 16;
                            minas = 40;
                        } else if (nivel == Nivel.DIFICIL) {
                            filas = 16;
                            columnas = 30;
                            minas = 99;
                        } else if (nivel == Nivel.CONTRARRELOJ) {
                            filas = 16;
                            columnas = 16;
                            minas = 50;
                        }

                        Dificultad difBD = session.createQuery(
                                        "FROM Dificultad WHERE nivel = :niv AND filas = :f AND columnas = :c AND numMinas = :m", Dificultad.class)
                                .setParameter("niv", nivel)
                                .setParameter("f", filas)
                                .setParameter("c", columnas)
                                .setParameter("m", minas)
                                .uniqueResult();

                        if (difBD == null) {
                            difBD = new Dificultad(0, nivel, filas, columnas, minas);
                            Transaction txDif = session.beginTransaction();
                            session.persist(difBD);
                            txDif.commit();
                        }
                        p.setDificultad(difBD);

                        p.setTiempoSegundos(Integer.parseInt(datos[2].trim()));
                        p.setVictoria(Boolean.parseBoolean(datos[3].trim()));
                        p.setFechaHora(LocalDateTime.parse(datos[4].trim(), formato));

                        partidas.add(p);
                    } catch (Exception e) {
                        System.err.println("Aviso: Saltando línea corrupta del CSV -> " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("ERROR: Fallo al leer el archivo CSV: " + e.getMessage());
        }
        return partidas;
	}
	
	public static void guardarPartidas(List<Partida> partidas) {
		// No se usa el true para sobreescribir el fichero completo
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(ficheroPartidas))) {
			for (Partida partida : partidas) {
				String fechaStr = partida.getFechaHora().format(formato);
				String datos = String.format("%s;%s;%d;%b;%s%n", 
						partida.getUsuario().getNickname(),
						partida.getDificultad().getNivel(),
						partida.getTiempoSegundos(),
						partida.isVictoria(),
						fechaStr
						);
				writer.write(datos);
			}
			System.out.println("CSV actualizado: se han guardado " + partidas.size() + " partidas.");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
