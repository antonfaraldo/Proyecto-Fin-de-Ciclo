package dam.proyectofinal.afm.dao;

import java.util.List; 
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import dam.proyectofinal.afm.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;
import dam.proyectofinal.afm.model.Usuario;

public class UsuarioDAOImpl  implements UsuarioDAO{
	public UsuarioDAOImpl() {}

	@Override
	public boolean registrar(Usuario usuario) {
		// TODO Auto-generated method stub
		// TEMPORAL
		// Se impide que alguien se registre con el admin
		if (usuario.getNickname().trim().equalsIgnoreCase("admin") || existeEmail(usuario.getEmail())) {
			return false;
		}
		// Cifrar antes de guardar la contraseña
		usuario.setPassword(BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt()));
		
		// El usuario se registra como inactivo por defecto
		usuario.setActivo(false);

        // Hibernate
        Transaction tx =  null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            session.persist(usuario); // Se guarda en la tabla usuarios

            tx.commit();
            System.out.println("SISTEMA: Usuario guardado de forma permanente en base de datos: " + usuario.getNickname());
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("ERROR: No se pudo registrar el usuario en base de datos: " + e.getMessage());
            return false;
        }
	}

	@Override
	public Usuario login(String nickname, String password) {
		// TODO Auto-generated method stub
        // Hibernate
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Usuario usuario = (Usuario) session.createQuery("FROM Usuario WHERE lower(nickname) = lower(:nick) ", Usuario.class)
                    .setParameter("nick", nickname)
                    .uniqueResult();

            // Se verifican las credenciales
            if (usuario != null && BCrypt.checkpw(password, usuario.getPassword())) {
                // Se bloquea el login si la cuenta no ha sido activada
                if (!usuario.isActivo()) {
                    System.out.println("Intento de login en cuenta no activa: " + nickname);
                    return null;
                }
                Transaction tx =  session.beginTransaction();
                usuario.setFechaUltimoAcceso(LocalDateTime.now());
                session.merge(usuario);
                tx.commit();

                return usuario;
            }
        } catch (Exception e) {
            System.err.println("ERROR: Fallo en el proceso de autenticación: " + e.getMessage());
        }
		return null;
	}
	
	@Override
	public boolean existeEmail(String email) {
		// TODO Auto-generated method stub
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long conteo = session.createQuery("SELECT count(u) FROM Usuario u WHERE lower(u.email) = lower(:email)", Long.class)
                    .setParameter("email", email)
                    .uniqueResult();
            return conteo != null && conteo > 0;
        }
	}
	@Override
	public List<Usuario> obtenerTodos() {
	    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Usuario", Usuario.class).list();
        } catch (Exception e) {
            System.err.println("ERROR: No se pudieron listar los usuarios de la base de datos: " + e.getMessage());
            return new ArrayList<>();
        }
	}

	@Override
	public boolean eliminar(String nickname) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Usuario usuario = (Usuario) session.createQuery("FROM Usuario WHERE nickname = :nick", Usuario.class)
                    .setParameter("nick", nickname)
                    .uniqueResult();

            if (usuario != null) {
                session.remove(usuario);
                tx.commit();
                return true;
            }
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("ERROR: No se pudo eliminar al usuario: " + e.getMessage());
        }
        return false;
	}
	
	@Override
	public boolean existeNickname(String nickname) {
	   try (Session session = HibernateUtil.getSessionFactory().openSession()) {
           Long conteo = session.createQuery("SELECT count(u) FROM Usuario u WHERE lower(u.nickname) = lower(:nick)", Long.class)
                   .setParameter("nick", nickname)
                   .uniqueResult();
           return conteo != null && conteo > 0;
       }
	}
	
	@Override
	public int obtenerTotalUsuarios() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long total = session.createQuery("SELECT count(u) FROM Usuario u", Long.class).uniqueResult();
            return total != null ? total.intValue() : 0;
        }
	}
	
	@Override
	public Usuario buscarPorEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Usuario WHERE lower(email) = lower(:email)", Usuario.class)
                    .setParameter("email", email)
                    .uniqueResult();
        }
	}
	
	@Override
	public boolean guardarTokenRecuperacion(String email, String token, LocalDateTime expiracion) {
		Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Usuario u = session.createQuery("FROM Usuario WHERE lower(email) = lower(:email)", Usuario.class)
                    .setParameter("email", email)
                    .uniqueResult();
            if (u != null) {
                u.setTokenRecuperacion(token);
                u.setFechaExpiracionToken(expiracion);
                session.merge(u);
                tx.commit();
                return true;
            }
        } catch (Exception e) {
            if (tx != null) tx.rollback();
        }
        return false;
	}

	@Override
	public boolean actualizarPassword(String email, String nuevaPassword) {
		Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Usuario u = session.createQuery("FROM Usuario WHERE lower(email) = lower(:email)", Usuario.class)
                    .setParameter("email", email)
                    .uniqueResult();
            if (u != null) {
                u.setPassword(BCrypt.hashpw(nuevaPassword, BCrypt.gensalt()));
                u.setTokenRecuperacion(null);
                u.setFechaExpiracionToken(null);
                session.merge(u);
                tx.commit();
                return true;
            }
        } catch (Exception e) {
            if (tx != null) tx.rollback();
        }
        return false;
	}
	
}
