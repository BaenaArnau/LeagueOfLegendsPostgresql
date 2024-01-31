import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.List;
import java.util.Locale;

/**
 * Clase que controla las operaciones relacionadas con la entidad "Region" en la base de datos.
 */
public class RegionController {
    private Connection connection; // Conexión a la base de datos.

    /**
     * Constructor de la clase RegionController.
     *
     * @param connection Conexión a la base de datos.
     */
    public RegionController(Connection connection) {
        this.connection = connection;
    }

    /**
     * Método para agregar una nueva región a la base de datos.
     *
     * @throws SQLException Excepción de SQL.
     * @throws IOException  Excepción de entrada/salida.
     */
    public void addRegion() throws SQLException, IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Inserta Nombre de la Región: ");
        String nombre = br.readLine();
        nombre = nombre.toUpperCase(Locale.ROOT);
        System.out.println("Inserta Descripción de la Región: ");
        String descripcion = br.readLine();

        int historiasRelacionada;
        while (true) {
            System.out.println("Inserta Historias Relacionadas: ");
            try {
                historiasRelacionada = Integer.parseInt(br.readLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingresa un número válido.");
            }
        }

        String sql = "INSERT INTO Region (nombre, descripcion, historiasRelacionada) VALUES (?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, nombre);
            pst.setString(2, descripcion);
            pst.setInt(3, historiasRelacionada);

            int filasAfectadas = pst.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Región insertada correctamente.");
            } else {
                System.out.println("No se pudo insertar la región.");
            }
        }
    }

    /**
     * Método para vaciar la tabla de regiones y reiniciar la secuencia de ID.
     *
     * @throws SQLException Excepción de SQL.
     */
    public void vaciarTabla() throws SQLException {
        String sqlDelete = "DELETE FROM Region CASCADE";

        try (PreparedStatement pst = connection.prepareStatement(sqlDelete)) {
            int filasAfectadas = pst.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("La tabla Region ha sido vaciada correctamente.");
                try (Statement statement = connection.createStatement()) {
                    String sqlRestartSeq = "ALTER SEQUENCE region_id_seq RESTART WITH 1";
                    statement.executeUpdate(sqlRestartSeq);
                    System.out.println("La secuencia ha sido reiniciada correctamente.");
                } catch (SQLException e) {
                    System.out.println("Error al reiniciar la secuencia: " + e.getMessage());
                }
            } else {
                System.out.println("La tabla Region ya estaba vacía.");
            }
        }
    }

    /**
     * Método para listar las regiones ordenadas por una columna específica.
     *
     * @param columna Nombre de la columna por la cual ordenar.
     * @throws SQLException Excepción de SQL.
     */
    public void listarRegionesOrdenadoPor(String columna) throws SQLException {
        String sql = "SELECT * FROM Region ORDER BY " + columna;

        try (PreparedStatement pst = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            try (ResultSet rs = pst.executeQuery()) {
                int idMaxLength = 5;
                int nombreMaxLength = 20;
                int descripcionMaxLength = 30;
                int historiasRelacionadasMaxLength = 20;

                while (rs.next()) {
                    idMaxLength = Math.max(idMaxLength, String.valueOf(rs.getInt("id")).length());
                    nombreMaxLength = Math.max(nombreMaxLength, rs.getString("nombre").length());
                    descripcionMaxLength = Math.max(descripcionMaxLength, rs.getString("descripcion").length());
                    historiasRelacionadasMaxLength = Math.max(historiasRelacionadasMaxLength, String.valueOf(rs.getInt("historiasRelacionada")).length());
                }

                rs.beforeFirst();

                System.out.println("+-" + repeat("-", idMaxLength) + "-+-" + repeat("-", nombreMaxLength) + "-+-" + repeat("-", descripcionMaxLength) + "-+-" + repeat("-", historiasRelacionadasMaxLength) + "-+");
                System.out.println("| " + padRight("ID", idMaxLength) + " | " + padRight("Nombre", nombreMaxLength) + " | " + padRight("Descripcion", descripcionMaxLength) + " | " + padRight("Hist. Relacionadas", historiasRelacionadasMaxLength) + " |");
                System.out.println("+-" + repeat("-", idMaxLength) + "-+-" + repeat("-", nombreMaxLength) + "-+-" + repeat("-", descripcionMaxLength) + "-+-" + repeat("-", historiasRelacionadasMaxLength) + "-+");

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nombre = rs.getString("nombre");
                    String descripcion = rs.getString("descripcion");
                    int historiasRelacionadas = rs.getInt("historiasRelacionada");

                    System.out.println("| " + padRight(String.valueOf(id), idMaxLength) + " | " + padRight(nombre, nombreMaxLength) + " | " + padRight(descripcion, descripcionMaxLength) + " | " + padRight(String.valueOf(historiasRelacionadas), historiasRelacionadasMaxLength) + " |");
                    System.out.println("+-" + repeat("-", idMaxLength) + "-+-" + repeat("-", nombreMaxLength) + "-+-" + repeat("-", descripcionMaxLength) + "-+-" + repeat("-", historiasRelacionadasMaxLength) + "-+");
                }
            }
        }
    }

    /**
     * Método para eliminar una región por su ID, eliminando también campeones y habilidades asociados.
     *
     * @throws SQLException Excepción de SQL.
     * @throws IOException  Excepción de entrada/salida.
     */
    public void eliminarRegionPorId() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Inserta la ID de la Región que deseas eliminar: ");
        int id;
        while (true) {
            try {
                id = Integer.parseInt(br.readLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingresa un número válido.");
            }
        }

        eliminarCampeonesYHabilidadesPorRegion(id);

        String sqlDelete = "DELETE FROM Region WHERE id = ?";

        try (PreparedStatement pstDelete = connection.prepareStatement(sqlDelete)) {
            pstDelete.setInt(1, id);

            int filasAfectadas = pstDelete.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Región con ID " + id + " eliminada correctamente.");

                if (id < obtenerUltimoIdRegion()) {
                    String sqlUpdate = "UPDATE Region SET id = id - 1 WHERE id > ?";
                    try (PreparedStatement pstUpdate = connection.prepareStatement(sqlUpdate)) {
                        pstUpdate.setInt(1, id);
                        pstUpdate.executeUpdate();
                    }

                    System.out.println("IDs actualizadas correctamente.");
                }
            } else {
                System.out.println("No se encontró una región con ID " + id + ".");
            }
        }
    }

    /**
     * Método privado auxiliar para obtener el último ID de la tabla "Region".
     *
     * @return Último ID de la tabla "Region".
     * @throws SQLException Excepción de SQL.
     */
    private int obtenerUltimoIdRegion() throws SQLException {
        String sqlSelectMaxId = "SELECT MAX(id) AS max_id FROM Region";

        try (PreparedStatement pstSelectMaxId = connection.prepareStatement(sqlSelectMaxId)) {
            try (ResultSet rs = pstSelectMaxId.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("max_id");
                } else {
                    throw new SQLException("No se pudo obtener el último ID de la tabla Region");
                }
            }
        }
    }

    /**
     * Método privado auxiliar para eliminar campeones y habilidades asociados a una región por su ID.
     *
     * @param regionId ID de la región.
     * @throws SQLException Excepción de SQL.
     */
    private void eliminarCampeonesYHabilidadesPorRegion(int regionId) throws SQLException {
        String sqlSelectCampeones = "SELECT id FROM Campeon WHERE region_id = ?";

        try (PreparedStatement pstSelectCampeones = connection.prepareStatement(sqlSelectCampeones)) {
            pstSelectCampeones.setInt(1, regionId);
            try (ResultSet rs = pstSelectCampeones.executeQuery()) {
                while (rs.next()) {
                    int campeonId = rs.getInt("id");
                    eliminarHabilidadesPorCampeon(campeonId);
                }
            }
        }

        String sqlDeleteCampeones = "DELETE FROM Campeon WHERE region_id = ?";

        try (PreparedStatement pstDeleteCampeones = connection.prepareStatement(sqlDeleteCampeones)) {
            pstDeleteCampeones.setInt(1, regionId);
            int filasAfectadas = pstDeleteCampeones.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Campeones asociados a la región con ID " + regionId + " eliminados correctamente.");
            } else {
                System.out.println("No se encontraron campeones asociados a la región con ID " + regionId + ".");
            }
        }
    }

    /**
     * Método privado auxiliar para eliminar habilidades asociadas a un campeón por su ID.
     *
     * @param campeonId ID del campeón.
     * @throws SQLException Excepción de SQL.
     */
    private void eliminarHabilidadesPorCampeon(int campeonId) throws SQLException {
        String sqlDeleteHabilidades = "DELETE FROM Habilidad WHERE campeon_id = ?";

        try (PreparedStatement pstDeleteHabilidades = connection.prepareStatement(sqlDeleteHabilidades)) {
            pstDeleteHabilidades.setInt(1, campeonId);
            pstDeleteHabilidades.executeUpdate();
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
     * Método para cargar datos desde un archivo CSV a la tabla de regiones.
     *
     * @throws IOException   Excepción de entrada/salida.
     * @throws CsvException  Excepción relacionada con CSV.
     * @throws SQLException  Excepción de SQL.
     */
    public void cargarDatosDesdeCSV() throws IOException, CsvException, SQLException {
        String sql = "INSERT INTO Region (nombre, descripcion, historiasRelacionada) VALUES (?, ?, ?)";

        try (CSVReader reader = new CSVReader(new FileReader("files/Regiones.csv"))) {
            List<String[]> allData = reader.readAll();

            boolean primeraLinea = true;

            for (String[] nextLine : allData) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }

                if (nextLine.length >= 3) {
                    String nombre = nextLine[0];
                    String descripcion = nextLine[1];
                    String historiasRelacionadasStr = nextLine[2];

                    int historiasRelacionadas = Integer.parseInt(historiasRelacionadasStr);

                    try (PreparedStatement pst = connection.prepareStatement(sql)) {
                        pst.setString(1, nombre);
                        pst.setString(2, descripcion);
                        pst.setInt(3, historiasRelacionadas);

                        int filasAfectadas = pst.executeUpdate();

                        if (filasAfectadas > 0) {
                            System.out.println("Región insertada correctamente.");
                        } else {
                            System.out.println("No se pudo insertar la región: " + nombre);
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
     * Método para editar una región por el usuario.
     *
     * @throws SQLException Excepción de SQL.
     * @throws IOException  Excepción de entrada/salida.
     */
    public void editarRegionPorUsuario() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Ingrese el ID del registro que desea editar: ");
        int id = Integer.parseInt(br.readLine());

        String sqlSelect = "SELECT nombre, descripcion, historiasRelacionada FROM Region WHERE id = ?";
        String nombreActual = null;
        String descripcionActual = null;
        int historiasRelacionadaActual = 0;

        try (PreparedStatement pstSelect = connection.prepareStatement(sqlSelect)) {
            pstSelect.setInt(1, id);
            ResultSet rs = pstSelect.executeQuery();
            if (rs.next()) {
                nombreActual = rs.getString("nombre");
                descripcionActual = rs.getString("descripcion");
                historiasRelacionadaActual = rs.getInt("historiasRelacionada");
            } else {
                System.out.println("No se encontró el registro con el ID proporcionado: " + id);
                return;
            }
        }

        System.out.print("¿Desea cambiar el nombre? (Si/No): ");
        String cambiarNombre = br.readLine();
        String nuevoNombre;
        if (cambiarNombre.equalsIgnoreCase("si")) {
            nuevoNombre = obtenerNuevoValor(br, "Introduzca el nuevo nombre: ");
        } else {
            nuevoNombre = nombreActual;
        }
        System.out.println("Nuevo nombre: " + nuevoNombre);

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

        System.out.print("¿Desea cambiar la historia relacionada? (Si/No): ");
        String cambiarHistoria = br.readLine();
        int nuevaHistoria;
        if (cambiarHistoria.equalsIgnoreCase("si")) {
            nuevaHistoria = obtenerNuevoValorEntero(br, "Introduzca la nueva historia relacionada: ");
        } else {
            nuevaHistoria = historiasRelacionadaActual;
        }
        System.out.println("Nueva historia relacionada: " + nuevaHistoria);

        String sqlUpdate = "UPDATE Region SET nombre = ?, descripcion = ?, historiasRelacionada = ? WHERE id = ?";

        try (PreparedStatement pstUpdate = connection.prepareStatement(sqlUpdate)) {
            pstUpdate.setString(1, nuevoNombre);
            pstUpdate.setString(2, nuevaDescripcion);
            pstUpdate.setInt(3, nuevaHistoria);
            pstUpdate.setInt(4, id);

            int filasActualizadas = pstUpdate.executeUpdate();

            if (filasActualizadas > 0) {
                System.out.println("Registro con ID " + id + " actualizado correctamente.");
            } else {
                System.out.println("No se encontró el registro con el ID proporcionado: " + id);
            }
        }
    }

    /**
     * Método privado auxiliar para obtener un nuevo valor introducido por el usuario desde la consola.
     *
     * @param br     BufferedReader para leer desde la consola.
     * @param mensaje Mensaje que se muestra al usuario.
     * @return Nuevo valor introducido por el usuario.
     * @throws IOException Excepción de entrada/salida.
     */
    private String obtenerNuevoValor(BufferedReader br, String mensaje) throws IOException {
        System.out.print(mensaje);
        return br.readLine();
    }

    /**
     * Método privado auxiliar para obtener un nuevo valor entero introducido por el usuario desde la consola.
     *
     * @param br     BufferedReader para leer desde la consola.
     * @param mensaje Mensaje que se muestra al usuario.
     * @return Nuevo valor entero introducido por el usuario.
     * @throws IOException Excepción de entrada/salida.
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
