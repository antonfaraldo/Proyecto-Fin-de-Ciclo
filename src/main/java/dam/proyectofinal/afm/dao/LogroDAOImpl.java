package dam.proyectofinal.afm.dao;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import dam.proyectofinal.afm.model.Logro;
import dam.proyectofinal.afm.model.Usuario;

public class LogroDAOImpl  implements LogroDAO {
    // Diccionario que simula la tabla intermedia
    private static Map<String, List<Logro>> usuariosLogrosMemoria = new HashMap<>();

    @Override
    public List<Logro> listarTodos() {
        // TODO Auto-generated method stub
        // Temporalmente devolvemos una lista
    	List<Logro> todos = new ArrayList<>();
    	
    	// Se añanden los logros
    	todos.add(new Logro(1, "Principiante Veloz", "Gana en menos de 30 segundos en nivel FÁCIL.", 30, false));
		todos.add(new Logro(2, "Pura Suerte", "Gana una partida en menos de 5 segundos.", 5, false));
		todos.add(new Logro(3, "Maestro Minero", "Supera el desafío del nivel DIFÍCIL.", 0, false));
		todos.add(new Logro(4, "Campo de Minas Superado", "Gana una partida en nivel MEDIO.", 0, false));
		todos.add(new Logro(5, "Desactivador Novato", "Acumula un total de 5 victorias.", 0, false));
		todos.add(new Logro(6, "Estratega Preciso", "Gana una partida sin colocar ni una sola bandera.", 0, false));
		todos.add(new Logro(7, "Arquitecto", "Gana una partida en un nivel personalizado.", 0, false));
		todos.add(new Logro(8, "Purista del Medio", "Gana 3 partidas en nivel MEDIO sin usar ni una sola bandera.", 0, false));
		todos.add(new Logro(9, "Novato del Hierro", "Acumula un total de 10 victorias", 0, false));
		todos.add(new Logro(10, "Experto de Plata", "Acumula un total de 50 victorias", 0, false));
		todos.add(new Logro(11, "Maestro de Oro", "Acumula un total de 100 victorias", 0, false));
		todos.add(new Logro(12, "Veterano de Guerra", "Acumula un total de 200 victorias.", 0, false));
	    todos.add(new Logro(13, "Leyenda de las Minas", "Acumula un total de 500 victorias.", 0, false));
	    todos.add(new Logro(14, "Maestro del Cálculo", "Gana en nivel DIFÍCIL sin colocar ninguna bandera.", 0, false));
	    todos.add(new Logro(15, "Superviviente del Tiempo", "Gana tu primera partida en modo Contrarreloj.", 0, false));
	    todos.add(new Logro(16, "Leyenda del Abismo", "Gana 50 partidas en modo DIFICIL.", 0, false));
	    todos.add(new Logro(17, "Instinto Ciego", "Gana una partida en modo Contrarreloj sin colocar ni una sola bandera.", 0, false));
		
        return todos;
    }

    @Override
    public List<Logro> obtenerLogrosPorUsuario(Usuario usuario) {
        // TODO Auto-generated method stub
        return usuariosLogrosMemoria.getOrDefault(usuario.getNickname(), new ArrayList<>());
    }
    @Override
    public boolean asignarLogroAUsuario(Usuario usuario, Logro logro) {
        // TODO Auto-generated method stub
        String nickname = usuario.getNickname();

        // Si el usuario no tiene ninguna lista de trofeos se crea
        usuariosLogrosMemoria.putIfAbsent(nickname, new ArrayList<>());

        // Se añade el logro
        usuariosLogrosMemoria.get(nickname).add(logro);

        System.out.println("Logro guardado en DAO: " + logro.getNombre() + " para " + nickname);
        return true;
    }

    @Override
    public boolean tieneUsuarioLogro(Usuario usuario, String nombreLogro) {
        // TODO Auto-generated method stub
        List<Logro> logros = obtenerLogrosPorUsuario(usuario);

        // Verificar que no tiene el logro repetido
        return logros.stream().anyMatch(l -> l.getNombre().equalsIgnoreCase(nombreLogro));
    }
    }
