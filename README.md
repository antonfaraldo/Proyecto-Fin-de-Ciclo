# 💣 MineManager FX

**Plataforma de Gestión de Competición y Gamificación Basada en Buscaminas**[cite: 4]

MineManager FX es una aplicación de escritorio multiplataforma orientada a modernizar el clásico juego del Buscaminas[cite: 4]. Este proyecto no solo ofrece una experiencia de entretenimiento, sino que integra un completo ecosistema de gestión de usuarios, persistencia de datos y un sistema de gamificación y estadísticas[cite: 4].

Proyecto desarrollado como Trabajo Fin de Ciclo (TFC) para el Ciclo Formativo de Grado Superior en Desarrollo de Aplicaciones Multiplataforma (DAM) en el C.P.R. Liceo "La Paz"[cite: 1].

---

## 🚀 Características Principales

*   **Módulo de Juego:** Tablero interactivo con algoritmos de expansión recursiva y generación aleatoria de minas[cite: 4].
*   **Gestión de Usuarios y Seguridad:** Sistema de autenticación con roles diferenciados (Usuario/Administrador) y protección de credenciales[cite: 4].
*   **Estadísticas y Rankings:** Registro de tiempos de finalización y visualización de marcas personales y clasificaciones globales[cite: 4].
*   **Sistema de Gamificación:** Desbloqueo de logros basado en el rendimiento del jugador[cite: 4].
*   **Panel de Administración (CRUD):** Herramienta dedicada para gestionar el ciclo de vida de los datos del sistema (usuarios, partidas, etc.)[cite: 4].
*   **Interoperabilidad:** Funcionalidad de importación y exportación de rankings mediante ficheros CSV[cite: 4].

---

## 🛠️ Stack Tecnológico

El proyecto hace uso de las siguientes tecnologías y herramientas[cite: 4]:

| Categoría | Tecnología |
| :--- | :--- |
| **Lenguaje** | Java 17+ |
| **Interfaz Gráfica** | JavaFX, FXML, CSS, Scene Builder |
| **Patrón de Arquitectura** | MVC (Modelo-Vista-Controlador) |
| **Base de Datos** | MySQL |
| **Persistencia (ORM)** | Hibernate / JDBC |
| **Gestión de Dependencias** | Maven |
| **Control de Versiones** | Git / GitHub |

---

## 🗄️ Modelo de Datos

El sistema garantiza la persistencia en un servidor relacional apoyado sobre cuatro entidades principales[cite: 4]:

1.  **USUARIO:** Gestión de cuentas (Nickname, Email, Password encriptada)[cite: 4].
2.  **PARTIDA:** Registro del rendimiento del usuario (Tiempo, Victoria/Derrota, Fecha)[cite: 4].
3.  **DIFICULTAD:** Configuración dinámica de los niveles del juego (Filas, Columnas, Número de Minas)[cite: 4].
4.  **LOGRO:** Sistema de recompensas y medallas obtenidas[cite: 4].

---

## ⚙️ Instalación y Ejecución (Manual del Administrador)

Para desplegar MineManager FX en un entorno local, sigue estos pasos:

1.  **Clonar el repositorio:**
```bash
    git clone [https://github.com/tu-usuario/proyecto-fin-de-ciclo.git](https://github.com/tu-usuario/proyecto-fin-de-ciclo.git)
    ```
2.  **Configurar la Base de Datos:**
    *   Asegúrate de tener un servidor MySQL en ejecución.
    *   Ejecuta el script SQL proporcionado en el directorio `/scripts` (o equivalente) para crear la estructura de tablas y cargar los datos de prueba[cite: 2].
3.  **Configurar credenciales:**
    *   Modifica el archivo `hibernate.cfg.xml` o `application.properties` con tus credenciales locales de MySQL[cite: 2].
4.  **Compilar y Ejecutar:**
    *   Utiliza Maven para descargar las dependencias y lanzar la aplicación:
```bash
    mvn clean install
    mvn javafx:run
    ```

---

## 👨‍💻 Autor

*   **Antón Faraldo Mosquera**[cite: 4]
