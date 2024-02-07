import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

/**
 * Clase que gestiona las operaciones relacionadas con las habilidades de los campeones.
 */
public class HabilidadController {

    private Connection connection;

    /**
     * Constructor de la clase HabilidadController.
     *
     * @param connection Objeto Connection para establecer la conexión con la base de datos.
     */
    public HabilidadController(Connection connection) {
        this.connection = connection;
    }

    /**
     * Método para añadir una nueva habilidad a la base de datos.
     *
     * @throws SQLException Si hay un error al ejecutar la consulta SQL.
     * @throws IOException  Si hay un error al leer la entrada del usuario.
     */
    public void addHabilidad() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String campeon = obtenerNombreCampeonValido(br);

        System.out.println("Inserta el nombre de la habilidad: ");
        String nombre = br.readLine();
        nombre = nombre.toUpperCase(Locale.ROOT);

        System.out.println("¿Es pasiva? (Sí/No): ");
        boolean pasiva = br.readLine().equalsIgnoreCase("Sí");

        System.out.println("Inserta la asignación de tecla: ");
        char asignacionDeTecla = br.readLine().charAt(0);

        System.out.println("Inserta la descripción de la habilidad: ");
        String descripcion = br.readLine();

        System.out.println("Inserta el enlace del video: ");
        String linkVideo = br.readLine();

        int campeonId = obtenerIdCampeonPorNombre(campeon);

        String sql = "INSERT INTO Habilidad (campeon, nombre, pasiva, asignacionDeTecla, descripcion, linkVideo, campeon_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, campeon);
            pst.setString(2, nombre);
            pst.setBoolean(3, pasiva);
            pst.setString(4, String.valueOf(asignacionDeTecla));
            pst.setString(5, descripcion);
            pst.setString(6, linkVideo);
            pst.setInt(7, campeonId);

            int filasAfectadas = pst.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Habilidad insertada correctamente.");
            } else {
                System.out.println("No se pudo insertar la habilidad.");
            }
        }
    }

    /**
     * Solicita al usuario que ingrese el nombre de un campeón y verifica si el nombre ingresado es válido,
     * es decir, si existe en la base de datos de campeones.
     *
     * @param br BufferedReader para leer la entrada del usuario.
     * @return El nombre válido del campeón.
     * @throws IOException  Si ocurre un error de entrada/salida.
     * @throws SQLException Si ocurre un error de SQL al verificar la existencia del campeón.
     */
    private String obtenerNombreCampeonValido(BufferedReader br) throws IOException, SQLException {
        String nombreCampeon = "";
        boolean nombreCampeonValido = false;

        while (!nombreCampeonValido) {
            System.out.println("Inserta el nombre del campeón: ");
            nombreCampeon = br.readLine();
            nombreCampeon = nombreCampeon.toUpperCase(Locale.ROOT);

            if (existeCampeon(nombreCampeon)) {
                nombreCampeonValido = true;
            } else {
                System.out.println("El nombre del campeón ingresado no existe. Por favor, ingrese un nombre de campeón válido.");
            }
        }

        return nombreCampeon;
    }

    /**
     * Verifica si un campeón con el nombre especificado existe en la base de datos.
     *
     * @param nombreCampeon El nombre del campeón a verificar.
     * @return true si el campeón existe, false en caso contrario.
     * @throws SQLException Si ocurre un error de SQL al ejecutar la consulta.
     */
    private boolean existeCampeon(String nombreCampeon) throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM Campeon WHERE nombre = ?";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, nombreCampeon);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    return count > 0;
                }
            }
        }

        return false;
    }


    /**
     * Método privado para obtener el ID de un campeón por su nombre.
     *
     * @param nombreCampeon Nombre del campeón.
     * @return ID del campeón.
     * @throws SQLException Si hay un error al ejecutar la consulta SQL.
     */
    private int obtenerIdCampeonPorNombre(String nombreCampeon) throws SQLException {
        String sqlSelect = "SELECT id FROM Campeon WHERE nombre = ?";

        try (PreparedStatement pstSelect = connection.prepareStatement(sqlSelect)) {
            pstSelect.setString(1, nombreCampeon);

            try (ResultSet rs = pstSelect.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("No se encontró el campeón con nombre: " + nombreCampeon);
                }
            }
        }
    }

    /**
     * Método para vaciar la tabla de habilidades y reiniciar la secuencia.
     *
     * @throws SQLException Si hay un error al ejecutar la consulta SQL.
     */
    public void vaciarTabla() throws SQLException {
        String sql = "DELETE FROM Habilidad CASCADE";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            int filasAfectadas = pst.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("La tabla Habilidad ha sido vaciada correctamente.");
                try (Statement statement = connection.createStatement()) {
                    String sqlRestartSeq = "ALTER SEQUENCE habilidad_id_seq RESTART WITH 1";
                    statement.executeUpdate(sqlRestartSeq);
                    System.out.println("La secuencia ha sido reiniciada correctamente.");
                }
            } else {
                System.out.println("La tabla Habilidad ya estaba vacía.");
            }
        }
    }

    /**
     * Método para eliminar habilidades por su ID.
     *
     * @throws SQLException Si hay un error al ejecutar la consulta SQL.
     * @throws IOException  Si hay un error al leer la entrada del usuario.
     */
    public void eliminarHabilidadesPorId() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Inserta la ID de la Habilidad que deseas eliminar: ");
        int id;
        while (true) {
            try {
                id = Integer.parseInt(br.readLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingresa un número válido.");
            }
        }

        boolean esUltimaHabilidad = esUltimaHabilidadEnTabla(id);

        String sqlDelete = "DELETE FROM Habilidad WHERE id = ?";

        try (PreparedStatement pstDelete = connection.prepareStatement(sqlDelete)) {
            pstDelete.setInt(1, id);

            int filasAfectadas = pstDelete.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Habilidad con ID " + id + " eliminada correctamente.");

                if (!esUltimaHabilidad) {
                    String sqlUpdate = "UPDATE Habilidad SET id = id - 1 WHERE id > ?";
                    try (PreparedStatement pstUpdate = connection.prepareStatement(sqlUpdate)) {
                        pstUpdate.setInt(1, id);
                        pstUpdate.executeUpdate();
                    }

                    System.out.println("IDs actualizadas correctamente.");
                } else {
                    System.out.println("Esta es la última habilidad en la tabla. No se actualizarán las IDs.");
                }
            } else {
                System.out.println("No se encontró una habilidad con ID " + id + ".");
            }
        }

        if (!esUltimaHabilidad) {
            String sqlUpdate = "SELECT setval('habilidad_id_seq', (SELECT MAX(id) FROM Habilidad), false)";
            try (PreparedStatement pstUpdate = connection.prepareStatement(sqlUpdate)) {
                pstUpdate.execute();
            }
            System.out.println("Secuencia actualizada correctamente.");
        }
    }

    /**
     * Método privado para verificar si una habilidad es la última en la tabla.
     *
     * @param habilidadId ID de la habilidad.
     * @return `true` si es la última habilidad, `false` de lo contrario.
     * @throws SQLException Si hay un error al ejecutar la consulta SQL.
     */
    private boolean esUltimaHabilidadEnTabla(int habilidadId) throws SQLException {
        String sqlSelect = "SELECT MAX(id) FROM Habilidad";
        try (PreparedStatement pstSelect = connection.prepareStatement(sqlSelect)) {
            try (ResultSet rs = pstSelect.executeQuery()) {
                if (rs.next()) {
                    int ultimaId = rs.getInt(1);
                    return habilidadId == ultimaId;
                } else {
                    throw new SQLException("No se pudo obtener el máximo ID de la tabla Habilidad.");
                }
            }
        }
    }

    /**
     * Método para cargar datos de habilidades desde un archivo CSV a la tabla de habilidades.
     *
     * @throws IOException   Si hay un error al leer el archivo CSV.
     * @throws CsvException  Si hay un error relacionado con el formato CSV.
     * @throws SQLException  Si hay un error al ejecutar la consulta SQL.
     */
    public void cargarDatosHabilidadesDesdeCSV() throws IOException, CsvException, SQLException {
        String sqlInsertHabilidad = "INSERT INTO Habilidad (campeon, nombre, pasiva, asignacionDeTecla, descripcion, linkVideo, campeon_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlSelectCampeonId = "SELECT id FROM Campeon WHERE nombre = ?";

        try (CSVReader reader = new CSVReader(new FileReader("files/Habilidades.csv"))) {
            List<String[]> allData = reader.readAll();

            boolean primeraLinea = true;

            for (String[] nextLine : allData) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }

                if (nextLine.length >= 6) {
                    String campeon = nextLine[0];
                    String nombre = nextLine[1];
                    boolean pasiva = Boolean.parseBoolean(nextLine[2]);
                    String asignacionDeTecla = nextLine[3];
                    String descripcion = nextLine[4];
                    String linkVideo = nextLine[5];

                    try (PreparedStatement pstSelectCampeonId = connection.prepareStatement(sqlSelectCampeonId)) {
                        pstSelectCampeonId.setString(1, campeon);
                        ResultSet rs = pstSelectCampeonId.executeQuery();

                        int campeonId;
                        if (rs.next()) {
                            campeonId = rs.getInt("id");

                            try (PreparedStatement pstInsertHabilidad = connection.prepareStatement(sqlInsertHabilidad)) {
                                pstInsertHabilidad.setString(1, campeon);
                                pstInsertHabilidad.setString(2, nombre);
                                pstInsertHabilidad.setBoolean(3, pasiva);
                                pstInsertHabilidad.setString(4, asignacionDeTecla);
                                pstInsertHabilidad.setString(5, descripcion);
                                pstInsertHabilidad.setString(6, linkVideo);
                                pstInsertHabilidad.setInt(7, campeonId);

                                int filasAfectadas = pstInsertHabilidad.executeUpdate();

                                if (filasAfectadas > 0) {
                                    System.out.println("Habilidad insertada correctamente.");
                                } else {
                                    System.out.println("No se pudo insertar la habilidad: " + nombre);
                                }
                            }
                        } else {
                            System.out.println("No se encontró el campeón con nombre: " + campeon);
                        }
                    } catch (SQLException e) {
                        System.out.println("Error al ejecutar la consulta SQL: " + e.getMessage());
                    }
                } else {
                    System.out.println("Error: La línea no tiene el formato esperado: " + String.join(",", nextLine));
                }
            }
        }
    }

    /**
     * Método para listar habilidades ordenadas por una columna específica.
     *
     * @param columna Nombre de la columna por la cual ordenar.
     * @throws SQLException Si hay un error al ejecutar la consulta SQL.
     */
    public void listarHabilidadesOrdenandoPor(String columna) throws SQLException {
        String sql = "SELECT * FROM Habilidad ORDER BY " + columna;

        try (PreparedStatement pst = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            try (ResultSet rs = pst.executeQuery()) {
                int idMaxLength = 5;
                int campeonMaxLength = 20;
                int nombreMaxLength = 20;
                int pasivaMaxLength = 5;
                int asignacionDeTeclaMaxLength = 10;
                int descripcionMaxLength = 30;
                int linkVideoMaxLength = 255;
                int campeonIdMaxLength = 5;

                while (rs.next()) {
                    idMaxLength = Math.max(idMaxLength, String.valueOf(rs.getInt("id")).length());
                    campeonMaxLength = Math.max(campeonMaxLength, rs.getString("campeon").length());
                    nombreMaxLength = Math.max(nombreMaxLength, rs.getString("nombre").length());
                    pasivaMaxLength = Math.max(pasivaMaxLength, String.valueOf(rs.getBoolean("pasiva")).length());
                    asignacionDeTeclaMaxLength = Math.max(asignacionDeTeclaMaxLength, rs.getString("asignacionDeTecla").length());
                    descripcionMaxLength = Math.max(descripcionMaxLength, rs.getString("descripcion").length());
                    linkVideoMaxLength = Math.max(linkVideoMaxLength, rs.getString("linkVideo").length());
                    campeonIdMaxLength = Math.max(campeonIdMaxLength, String.valueOf(rs.getInt("campeon_id")).length());
                }

                rs.beforeFirst();

                System.out.println("+-" + repeat("-", idMaxLength) + "-+-" + repeat("-", campeonMaxLength) + "-+-" + repeat("-", nombreMaxLength) + "-+-" + repeat("-", pasivaMaxLength) + "-+-" +
                        repeat("-", asignacionDeTeclaMaxLength) + "-+-" + repeat("-", descripcionMaxLength) + "-+-" + repeat("-", linkVideoMaxLength) + "-+-" +
                        repeat("-", campeonIdMaxLength) + "-+");
                System.out.println("| " + padRight("ID", idMaxLength) + " | " + padRight("Campeon", campeonMaxLength) + " | " + padRight("Nombre", nombreMaxLength) + " | " +
                        padRight("Pasiva", pasivaMaxLength) + " | " + padRight("Asignacion de Tecla", asignacionDeTeclaMaxLength) + " | " +
                        padRight("Descripcion", descripcionMaxLength) + " | " + padRight("Link Video", linkVideoMaxLength) + " | " +
                        padRight("Campeon ID", campeonIdMaxLength) + " |");
                System.out.println("+-" + repeat("-", idMaxLength) + "-+-" + repeat("-", campeonMaxLength) + "-+-" + repeat("-", nombreMaxLength) + "-+-" + repeat("-", pasivaMaxLength) + "-+-" +
                        repeat("-", asignacionDeTeclaMaxLength) + "-+-" + repeat("-", descripcionMaxLength) + "-+-" + repeat("-", linkVideoMaxLength) + "-+-" +
                        repeat("-", campeonIdMaxLength) + "-+");

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String campeon = rs.getString("campeon");
                    String nombre = rs.getString("nombre");
                    boolean pasiva = rs.getBoolean("pasiva");
                    String asignacionDeTecla = rs.getString("asignacionDeTecla");
                    String descripcion = rs.getString("descripcion");
                    String linkVideo = rs.getString("linkVideo");
                    int campeonId = rs.getInt("campeon_id");

                    System.out.println("| " + padRight(String.valueOf(id), idMaxLength) + " | " + padRight(campeon, campeonMaxLength) + " | " +
                            padRight(nombre, nombreMaxLength) + " | " + padRight(String.valueOf(pasiva), pasivaMaxLength) + " | " +
                            padRight(asignacionDeTecla, asignacionDeTeclaMaxLength) + " | " + padRight(descripcion, descripcionMaxLength) + " | " +
                            padRight(linkVideo, linkVideoMaxLength) + " | " + padRight(String.valueOf(campeonId), campeonIdMaxLength) + " |");
                    System.out.println("+-" + repeat("-", idMaxLength) + "-+-" + repeat("-", campeonMaxLength) + "-+-" + repeat("-", nombreMaxLength) + "-+-" + repeat("-", pasivaMaxLength) + "-+-" +
                            repeat("-", asignacionDeTeclaMaxLength) + "-+-" + repeat("-", descripcionMaxLength) + "-+-" + repeat("-", linkVideoMaxLength) + "-+-" +
                            repeat("-", campeonIdMaxLength) + "-+");
                }
            }
        }
    }

    /**
     * Método privado auxiliar para repetir una cadena determinado número de veces.
     *
     * @param s     Cadena a repetir.
     * @param times Número de veces que se repetirá la cadena.
     * @return Cadena repetida.
     */
    private String repeat(String s, int times) {
        return new String(new char[times]).replace("\0", s);
    }

    /**
     * Método privado auxiliar para rellenar una cadena con espacios en blanco en el lado derecho hasta alcanzar una longitud específica.
     *
     * @param s      Cadena a rellenar.
     * @param length Longitud deseada.
     * @return Cadena rellenada.
     */
    private String padRight(String s, int length) {
        return String.format("%-" + length + "s", s);
    }

    /**
     * Método para editar una habilidad según la entrada del usuario.
     *
     * @throws SQLException Si hay un error al ejecutar la consulta SQL.
     * @throws IOException  Si hay un error al leer la entrada del usuario.
     */
    public void editarHabilidadPorUsuario() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Ingrese el ID del registro que desea editar: ");
        int id = Integer.parseInt(br.readLine());

        String sqlSelect = "SELECT campeon, nombre, pasiva, asignacionDeTecla, descripcion, linkVideo, campeon_id FROM Habilidad WHERE id = ?";
        String campeonActual = null;
        String nombreActual = null;
        boolean pasivaActual = false;
        String asignacionDeTeclaActual = null;
        String descripcionActual = null;
        String linkVideoActual = null;
        int campeonIdActual = 0;

        try (PreparedStatement pstSelect = connection.prepareStatement(sqlSelect)) {
            pstSelect.setInt(1, id);
            ResultSet rs = pstSelect.executeQuery();
            if (rs.next()) {
                campeonActual = rs.getString("campeon");
                nombreActual = rs.getString("nombre");
                pasivaActual = rs.getBoolean("pasiva");
                asignacionDeTeclaActual = rs.getString("asignacionDeTecla");
                descripcionActual = rs.getString("descripcion");
                linkVideoActual = rs.getString("linkVideo");
                campeonIdActual = rs.getInt("campeon_id");
            } else {
                System.out.println("No se encontró el registro con el ID proporcionado: " + id);
                return;
            }
        }

        System.out.print("¿Desea cambiar el campeón? (Si/No): ");
        String cambiarCampeon = br.readLine();
        String nuevoCampeon = "";
        int nuevoCampeonId = campeonIdActual;
        boolean validador = false;

        if (cambiarCampeon.equalsIgnoreCase("si")) {
            while (!validador){
                nuevoCampeon = obtenerNuevoValor(br, "Introduzca el nuevo campeón: ");
                if (existeCampeon(nuevoCampeon)){
                    nuevoCampeonId = obtenerIdCampeonPorNombre(nuevoCampeon);
                    validador = true;
                }else {
                    System.out.println("Introduzca un campeon valido");
                }
            }
        } else {
            nuevoCampeon = campeonActual;
        }
        System.out.println("Nuevo campeón: " + nuevoCampeon);

        System.out.print("¿Desea cambiar el nombre? (Si/No): ");
        String cambiarNombre = br.readLine();
        String nuevoNombre;
        if (cambiarNombre.equalsIgnoreCase("si")) {
            nuevoNombre = obtenerNuevoValor(br, "Introduzca el nuevo nombre: ");
        } else {
            nuevoNombre = nombreActual;
        }
        System.out.println("Nuevo nombre: " + nuevoNombre);

        System.out.print("¿Desea cambiar si es pasiva? (Si/No): ");
        String cambiarPasiva = br.readLine();
        boolean nuevaPasiva;
        if (cambiarPasiva.equalsIgnoreCase("si")) {
            nuevaPasiva = obtenerNuevoValorBooleano(br, "Introduzca si es pasiva (true/false): ");
        } else {
            nuevaPasiva = pasivaActual;
        }
        System.out.println("Nueva pasiva: " + nuevaPasiva);

        System.out.print("¿Desea cambiar la asignación de tecla? (Si/No): ");
        String cambiarAsignacionDeTecla = br.readLine();
        String nuevaAsignacionDeTecla;
        if (cambiarAsignacionDeTecla.equalsIgnoreCase("si")) {
            nuevaAsignacionDeTecla = obtenerNuevoValor(br, "Introduzca la nueva asignación de tecla: ");
        } else {
            nuevaAsignacionDeTecla = asignacionDeTeclaActual;
        }
        System.out.println("Nueva asignación de tecla: " + nuevaAsignacionDeTecla);

        System.out.print("¿Desea cambiar la descripción? (Si/No): ");
        String cambiarDescripcion = br.readLine();
        String nuevaDescripcion;
        if (cambiarDescripcion.equalsIgnoreCase("si")) {
            System.out.print("Introduzca la nueva descripción: ");
            nuevaDescripcion = br.readLine();
        } else {
            nuevaDescripcion = descripcionActual;
        }
        System.out.println("Nueva descripción: " + nuevaDescripcion);

        System.out.print("¿Desea cambiar el link del video? (Si/No): ");
        String cambiarLinkVideo = br.readLine();
        String nuevoLinkVideo;
        if (cambiarLinkVideo.equalsIgnoreCase("si")) {
            nuevoLinkVideo = obtenerNuevoValor(br, "Introduzca el nuevo link del video: ");
        } else {
            nuevoLinkVideo = linkVideoActual;
        }
        System.out.println("Nuevo link del video: " + nuevoLinkVideo);

        String sqlUpdate = "UPDATE Habilidad SET campeon = ?, nombre = ?, pasiva = ?, asignacionDeTecla = ?, descripcion = ?, linkVideo = ?, campeon_id = ? WHERE id = ?";

        try (PreparedStatement pstUpdate = connection.prepareStatement(sqlUpdate)) {
            pstUpdate.setString(1, nuevoCampeon);
            pstUpdate.setString(2, nuevoNombre);
            pstUpdate.setBoolean(3, nuevaPasiva);
            pstUpdate.setString(4, nuevaAsignacionDeTecla);
            pstUpdate.setString(5, nuevaDescripcion);
            pstUpdate.setString(6, nuevoLinkVideo);
            pstUpdate.setInt(7, nuevoCampeonId);
            pstUpdate.setInt(8, id);

            int filasActualizadas = pstUpdate.executeUpdate();

            if (filasActualizadas > 0) {
                System.out.println("Registro con ID " + id + " actualizado correctamente.");
            } else {
                System.out.println("No se encontró el registro con el ID proporcionado: " + id);
            }
        }
    }

    /**
     * Método privado para obtener un nuevo valor booleano desde la entrada del usuario.
     *
     * @param br      BufferedReader para leer la entrada del usuario.
     * @param mensaje Mensaje a mostrar al usuario.
     * @return Nuevo valor booleano ingresado por el usuario.
     * @throws IOException Si hay un error al leer la entrada del usuario.
     */
    private boolean obtenerNuevoValorBooleano(BufferedReader br, String mensaje) throws IOException {
        System.out.print(mensaje);
        String nuevoValorStr = br.readLine();
        return Boolean.parseBoolean(nuevoValorStr);
    }

    /**
     * Método privado para obtener un nuevo valor desde la entrada del usuario.
     *
     * @param br      BufferedReader para leer la entrada del usuario.
     * @param mensaje Mensaje a mostrar al usuario.
     * @return Nuevo valor ingresado por el usuario.
     * @throws IOException Si hay un error al leer la entrada del usuario.
     */
    private String obtenerNuevoValor(BufferedReader br, String mensaje) throws IOException {
        System.out.print(mensaje);
        return br.readLine();
    }

    /**
     * Método privado para obtener un nuevo valor entero desde la entrada del usuario.
     *
     * @param br      BufferedReader para leer la entrada del usuario.
     * @param mensaje Mensaje a mostrar al usuario.
     * @return Nuevo valor entero ingresado por el usuario.
     * @throws IOException Si hay un error al leer la entrada del usuario.
     */
    private int obtenerNuevoValorEntero(BufferedReader br, String mensaje) throws IOException {
        System.out.print(mensaje);
        String nuevoValorStr = br.readLine();
        try {
            return Integer.parseInt(nuevoValorStr);
        } catch (NumberFormatException e) {
            System.out.println("Valor no válido. Se mantendrá el valor actual.");
            return obtenerNuevoValorEntero(br, mensaje);
        }
    }

}
