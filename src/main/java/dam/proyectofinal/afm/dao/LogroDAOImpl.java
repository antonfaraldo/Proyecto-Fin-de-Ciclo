package dam.proyectofinal.afm.dao;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import dam.proyectofinal.afm.model.Logro;
import dam.proyectofinal.afm.model.Usuario;
import dam.proyectofinal.afm.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class LogroDAOImpl  implements LogroDAO {
    // Diccionario que simula la tabla intermedia
    private static Map<String, List<Logro>> usuariosLogrosMemoria = new HashMap<>();

    @Override
    public List<Logro> listarTodos() {
        // TODO Auto-generated method stub
       try (Session session = HibernateUtil.getSessionFactory().openSession()) {
           return session.createQuery("FROM Logro ORDER BY idLogro ASC", Logro.class).list();
       } catch (Exception e) {
           System.err.println("ERROR: No se pudo mapear la vitrina global de logros: " + e.getMessage());
           return new ArrayList<>();
       }
    }

    @Override
    public List<Logro> obtenerLogrosPorUsuario(Usuario usuario) {
       try (Session session = HibernateUtil.getSessionFactory().openSession()) {
           String hql = "SELECT u.logros FROM Usuario u WHERE lower(u.nickname) = lower(:nick)";
           return session.createQuery(hql, Logro.class)
                   .setParameter("nick", usuario.getNickname())
                   .list();
       } catch (Exception e) {
           return new ArrayList<>();
       }
    }
    @Override
    public boolean asignarLogroAUsuario(Usuario usuario, Logro logro) {
        // TODO Auto-generated method stub
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Usuario usuarioDB = session.get(Usuario.class, usuario.getIdUsuario());
            Logro logroDB = session.get(Logro.class, logro.getIdLogro());

            if (usuarioDB != null && logroDB != null) {
                if (!usuarioDB.getLogros().contains(logroDB)) {
                    usuarioDB.getLogros().add(logroDB);
                    session.merge(usuarioDB);
                }
                tx.commit();
                System.out.println("SISTEMA: Trofeo guardado de forma permanente en MySQL.");
                return true;
            }
        } catch (Exception e) {
            if (tx != null) tx.rollback();
        }
        return false;
    }

    @Override
    public boolean tieneUsuarioLogro(Usuario usuario, String nombreLogro) {
        // TODO Auto-generated method stub
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(l) FROM Usuario u JOIN u.logros l WHERE lower(u.nickname) = lower(:nick) AND lower(l.nombre) = lower(:nombre)";
            Long conteo = session.createQuery(hql, Long.class)
                    .setParameter("nick", usuario.getNickname())
                    .setParameter("nombre", nombreLogro)
                    .uniqueResult();
            return conteo != null && conteo > 0;
        } catch (Exception e) {
            return false;
        }
    }
    }
