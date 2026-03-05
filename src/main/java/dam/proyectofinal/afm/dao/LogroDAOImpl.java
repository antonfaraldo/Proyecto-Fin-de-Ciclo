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
        return new ArrayList<>();
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
