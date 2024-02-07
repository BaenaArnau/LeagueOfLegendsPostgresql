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
 * Clase que representa un controlador para operaciones relacionadas con los campeones.
 */
public class CampeonController {
    private Connection connection;

    /**
     * Constructor que recibe una conexión a la base de datos.
     *
     * @param connection Conexión a la base de datos.
     */
    public CampeonController(Connection connection) {
        this.connection = connection;
    }

    /**
     * Método para agregar un nuevo campeón a la base de datos.
     *
     * @throws SQLException Si hay un error al ejecutar la consulta SQL.
     * @throws IOException  Si hay un error al leer la entrada del usuario.
     */
    public void addCampeon() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Inserta el nombre del campeón: ");
        String nombreCampeon = br.readLine();
        nombreCampeon = nombreCampeon.toUpperCase(Locale.ROOT);

        String nombreRegion = "";
        boolean regionValida = false;

        while (!regionValida) {
            System.out.println("Inserta el nombre de la región: ");
            nombreRegion = br.readLine();
            nombreRegion = nombreRegion.toUpperCase(Locale.ROOT);

            if (!existeRegion(nombreRegion)) {
                System.out.println("La región ingresada no existe. Por favor, ingresa un nombre de región válido.");
            } else {
                regionValida = true;
            }
        }

        int regionId = obtenerIdRegionPorNombre(nombreRegion);

        System.out.println("Inserta el apodo del campeón: ");
        String apodo = br.readLine();

        System.out.println("Inserta el número de campeones con relación: ");
        int campeonesConRelacion = Integer.parseInt(br.readLine());

        System.out.println("Inserta la biografía del campeón: ");
        String biografia = br.readLine();

        System.out.println("¿Aparece en cinemáticas? (Sí/No): ");
        boolean aparicionEnCinematicas = br.readLine().equalsIgnoreCase("Sí");

        System.out.println("Inserta el número de relatos cortos: ");
        int numRelatosCortos = Integer.parseInt(br.readLine());

        System.out.println("Inserta el rol del campeón: ");
        String rol = br.readLine();

        System.out.println("Inserta la raza del campeón: ");
        String raza = br.readLine();

        System.out.println("Inserta el número de aspectos del campeón: ");
        int numDeAspectos = Integer.parseInt(br.readLine());

        System.out.println("Inserta la dificultad del campeón: ");
        String dificultad = br.readLine();

        String sql = "INSERT INTO Campeon (nombre, apodo, campeonesConRelacion, biografia, " +
                "aparicionEnCinematicas, numRelatosCortos, rol, raza, region, numDeAspectos, dificultad, region_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, nombreCampeon);
            pst.setString(2, apodo);
            pst.setInt(3, campeonesConRelacion);
            pst.setString(4, biografia);
            pst.setBoolean(5, aparicionEnCinematicas);
            pst.setInt(6, numRelatosCortos);
            pst.setString(7, rol);
            pst.setString(8, raza);
            pst.setString(9, nombreRegion);
            pst.setInt(10, numDeAspectos);
            pst.setString(11, dificultad);
            pst.setInt(12, regionId);

            int filasAfectadas = pst.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Campeón insertado correctamente.");
            } else {
                System.out.println("No se pudo insertar el campeón.");
            }
        }
    }

    /**
     * Comprueba si existe una región en la base de datos con el nombre especificado.
     *
     * @param nombreRegion El nombre de la región a verificar.
     * @return true si la región existe en la base de datos, false si no.
     * @throws SQLException Si ocurre un error al ejecutar la consulta SQL.
     */
    private boolean existeRegion(String nombreRegion) throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM Region WHERE nombre = ?";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, nombreRegion);

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
     * Método privado para obtener el ID de una región por su nombre.
     *
     * @param nombreRegion Nombre de la región.
     * @return ID de la región.
     * @throws SQLException Si hay un error al ejecutar la consulta SQL.
     */
    private int obtenerIdRegionPorNombre(String nombreRegion) throws SQLException {
        String sqlSelect = "SELECT id FROM Region WHERE nombre = ?";

        try (PreparedStatement pstSelect = connection.prepareStatement(sqlSelect)) {
            pstSelect.setString(1, nombreRegion);

            try (ResultSet rs = pstSelect.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("No se encontró la región con nombre: " + nombreRegion);
                }
            }
        }
    }

    /**
     * Método para vaciar la tabla de campeones en la base de datos.
     *
     * @throws SQLException Si hay un error al ejecutar la consulta SQL.
     */
    public void vaciarTabla() throws SQLException {
        String sql = "DELETE FROM Campeon CASCADE";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            int filasAfectadas = pst.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("La tabla Campeon ha sido vaciada correctamente.");
                try (Statement statement = connection.createStatement()) {
                    String sqlRestartSeq = "ALTER SEQUENCE campeon_id_seq RESTART WITH 1";
                    statement.executeUpdate(sqlRestartSeq);
                    System.out.println("La secuencia ha sido reiniciada correctamente.");
                } catch (SQLException e) {
                    System.out.println("Error al reiniciar la secuencia: " + e.getMessage());
                }
            } else {
                System.out.println("La tabla Campeon ya estaba vacía.");
            }
        }
    }

    /**
     * Método para cargar datos de un archivo CSV a la tabla de campeones en la base de datos.
     *
     * @throws IOException  Si hay un error al leer el archivo CSV.
     * @throws CsvException Si hay un error al procesar el archivo CSV.
     * @throws SQLException Si hay un error al ejecutar la consulta SQL.
     */
    public void cargarDatosDesdeCSV() throws IOException, CsvException, SQLException {
        String sql = "INSERT INTO Campeon (region, nombre, apodo, campeonesConRelacion, biografia, aparicionEnCinematicas, numRelatosCortos, rol, raza, numDeAspectos, dificultad, region_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (CSVReader reader = new CSVReader(new FileReader("files/Campeons.csv"))) {
            List<String[]> allData = reader.readAll();

            boolean primeraLinea = true;

            for (String[] nextLine : allData) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }

                if (nextLine.length >= 11) {
                    String region = nextLine[0];
                    String nombre = nextLine[1];
                    String apodo = nextLine[2];
                    String campeonesConRelacion = nextLine[3];
                    String biografia = nextLine[4];
                    String aparicionEnCinematicas = nextLine[5];
                    String numRelatosCortos = nextLine[6];
                    String rol = nextLine[7];
                    String raza = nextLine[8];
                    String numDeAspectos = nextLine[9];
                    String dificultad = nextLine[10];

                    try (PreparedStatement pst = connection.prepareStatement(sql)) {
                        pst.setString(1, region);
                        pst.setString(2, nombre);
                        pst.setString(3, apodo);
                        pst.setInt(4, Integer.parseInt(campeonesConRelacion));
                        pst.setString(5, biografia);

                        pst.setBoolean(6, Boolean.parseBoolean(aparicionEnCinematicas));

                        pst.setInt(7, Integer.parseInt(numRelatosCortos));

                        pst.setString(8, rol);
                        pst.setString(9, raza);
                        pst.setInt(10, Integer.parseInt(numDeAspectos));

                        pst.setString(11, dificultad);

                        pst.setInt(12, obtenerRegionIdPorNombre(region));

                        int filasAfectadas = pst.executeUpdate();

                        if (filasAfectadas > 0) {
                            System.out.println("Campeón insertado correctamente.");
                        } else {
                            System.out.println("No se pudo insertar el campeón: " + nombre);
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
     * Método privado para obtener el ID de una región por su nombre, utilizado en el método cargarDatosDesdeCSV.
     *
     * @param nombreRegion Nombre de la región.
     * @return ID de la región.
     * @throws SQLException Si hay un error al ejecutar la consulta SQL.
     */
    private int obtenerRegionIdPorNombre(String nombreRegion) throws SQLException {
        String sql = "SELECT id FROM Region WHERE nombre = ?";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, nombreRegion);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            } else {
                System.out.println("No se encontró la región con el nombre proporcionado: " + nombreRegion);
                return -1;
            }
        }
    }

    /**
     * Método para listar campeones ordenados por una columna específica.
     *
     * @param columna Nombre de la columna por la cual ordenar.
     * @throws SQLException Si hay un error al ejecutar la consulta SQL.
     */
    public void listarCampeonesOrdeandoPor(String columna) throws SQLException {
        String sql = "SELECT * FROM Campeon ORDER BY " + columna;

        try (PreparedStatement pst = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            try (ResultSet rs = pst.executeQuery()) {
                int idMaxLength = 5;
                int nombreMaxLength = 20;
                int apodoMaxLength = 20;
                int campeonesConRelacionMaxLength = 20;
                int biografiaMaxLength = 30;
                int aparicionEnCinematicasMaxLength = 6;
                int numRelatosCortosMaxLength = 6;
                int rolMaxLength = 15;
                int razaMaxLength = 15;
                int numDeAspectosMaxLength = 5;
                int dificultadMaxLength = 10;
                int regionMaxLength = 20;

                while (rs.next()) {
                    idMaxLength = Math.max(idMaxLength, String.valueOf(rs.getInt("id")).length());
                    nombreMaxLength = Math.max(nombreMaxLength, rs.getString("nombre").length());
                    apodoMaxLength = Math.max(apodoMaxLength, rs.getString("apodo").length());
                    campeonesConRelacionMaxLength = Math.max(campeonesConRelacionMaxLength, String.valueOf(rs.getInt("campeonesConRelacion")).length());
                    biografiaMaxLength = Math.max(biografiaMaxLength, rs.getString("biografia").length());
                    aparicionEnCinematicasMaxLength = Math.max(aparicionEnCinematicasMaxLength, String.valueOf(rs.getBoolean("aparicionEnCinematicas")).length());
                    numRelatosCortosMaxLength = Math.max(numRelatosCortosMaxLength, String.valueOf(rs.getInt("numRelatosCortos")).length());
                    rolMaxLength = Math.max(rolMaxLength, rs.getString("rol").length());
                    razaMaxLength = Math.max(razaMaxLength, rs.getString("raza").length());
                    numDeAspectosMaxLength = Math.max(numDeAspectosMaxLength, String.valueOf(rs.getInt("numDeAspectos")).length());
                    dificultadMaxLength = Math.max(dificultadMaxLength, rs.getString("dificultad").length());
                    regionMaxLength = Math.max(regionMaxLength, rs.getString("region").length());
                }

                rs.beforeFirst();

                System.out.println("+-" + repeat("-", idMaxLength) + "-+-" + repeat("-", nombreMaxLength) + "-+-" + repeat("-", apodoMaxLength) + "-+-" + repeat("-", campeonesConRelacionMaxLength) + "-+-" +
                        repeat("-", biografiaMaxLength) + "-+-" + repeat("-", aparicionEnCinematicasMaxLength) + "-+-" + repeat("-", numRelatosCortosMaxLength) + "-+-" + repeat("-", rolMaxLength) + "-+-" +
                        repeat("-", razaMaxLength) + "-+-" + repeat("-", numDeAspectosMaxLength) + "-+-" + repeat("-", dificultadMaxLength) + "-+-" + repeat("-", regionMaxLength) + "-+");
                System.out.println("| " + padRight("ID", idMaxLength) + " | " + padRight("Nombre", nombreMaxLength) + " | " + padRight("Apodo", apodoMaxLength) + " | " +
                        padRight("Campeones Con Relación", campeonesConRelacionMaxLength) + " | " + padRight("Biografía", biografiaMaxLength) + " | " +
                        padRight("Aparición en Cinemáticas", aparicionEnCinematicasMaxLength) + " | " + padRight("Num Relatos Cortos", numRelatosCortosMaxLength) + " | " +
                        padRight("Rol", rolMaxLength) + " | " + padRight("Raza", razaMaxLength) + " | " + padRight("Num de Aspectos", numDeAspectosMaxLength) + " | " +
                        padRight("Dificultad", dificultadMaxLength) + " | " + padRight("Región", regionMaxLength) + " |");
                System.out.println("+-" + repeat("-", idMaxLength) + "-+-" + repeat("-", nombreMaxLength) + "-+-" + repeat("-", apodoMaxLength) + "-+-" + repeat("-", campeonesConRelacionMaxLength) + "-+-" +
                        repeat("-", biografiaMaxLength) + "-+-" + repeat("-", aparicionEnCinematicasMaxLength) + "-+-" + repeat("-", numRelatosCortosMaxLength) + "-+-" + repeat("-", rolMaxLength) + "-+-" +
                        repeat("-", razaMaxLength) + "-+-" + repeat("-", numDeAspectosMaxLength) + "-+-" + repeat("-", dificultadMaxLength) + "-+-" + repeat("-", regionMaxLength) + "-+");

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nombre = rs.getString("nombre");
                    String apodo = rs.getString("apodo");
                    int campeonesConRelacion = rs.getInt("campeonesConRelacion");
                    String biografia = rs.getString("biografia");
                    boolean aparicionEnCinematicas = rs.getBoolean("aparicionEnCinematicas");
                    int numRelatosCortos = rs.getInt("numRelatosCortos");
                    String rol = rs.getString("rol");
                    String raza = rs.getString("raza");
                    int numDeAspectos = rs.getInt("numDeAspectos");
                    String dificultad = rs.getString("dificultad");
                    String region = rs.getString("region");

                    System.out.println("| " + padRight(String.valueOf(id), idMaxLength) + " | " + padRight(nombre, nombreMaxLength) + " | " + padRight(apodo, apodoMaxLength) + " | " +
                            padRight(String.valueOf(campeonesConRelacion), campeonesConRelacionMaxLength) + " | " + padRight(biografia, biografiaMaxLength) + " | " +
                            padRight(String.valueOf(aparicionEnCinematicas), aparicionEnCinematicasMaxLength) + " | " + padRight(String.valueOf(numRelatosCortos), numRelatosCortosMaxLength) + " | " +
                            padRight(rol, rolMaxLength) + " | " + padRight(raza, razaMaxLength) + " | " + padRight(String.valueOf(numDeAspectos), numDeAspectosMaxLength) + " | " +
                            padRight(dificultad, dificultadMaxLength) + " | " + padRight(region, regionMaxLength) + " |");
                    System.out.println("+-" + repeat("-", idMaxLength) + "-+-" + repeat("-", nombreMaxLength) + "-+-" + repeat("-", apodoMaxLength) + "-+-" + repeat("-", campeonesConRelacionMaxLength) + "-+-" +
                            repeat("-", biografiaMaxLength) + "-+-" + repeat("-", aparicionEnCinematicasMaxLength) + "-+-" + repeat("-", numRelatosCortosMaxLength) + "-+-" + repeat("-", rolMaxLength) + "-+-" +
                            repeat("-", razaMaxLength) + "-+-" + repeat("-", numDeAspectosMaxLength) + "-+-" + repeat("-", dificultadMaxLength) + "-+-" + repeat("-", regionMaxLength) + "-+");
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
     * Método para eliminar un campeón por su ID.
     *
     * @throws SQLException Si hay un error al ejecutar la consulta SQL.
     * @throws IOException  Si hay un error al leer la entrada del usuario.
     */
    public void eliminarCampeonPorId() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Inserta la ID del Campeon que deseas eliminar: ");
        int id;
        while (true) {
            try {
                id = Integer.parseInt(br.readLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingresa un número válido.");
            }
        }

        String nombreCampeonAEliminar = obtenerNombreCampeonPorId(id);

        eliminarHabilidadesPorNombreCampeon(nombreCampeonAEliminar);

        boolean esUltimoCampeon = esUltimoCampeonEnTabla(id);

        String sqlDelete = "DELETE FROM Campeon WHERE id = ?";

        try (PreparedStatement pstDelete = connection.prepareStatement(sqlDelete)) {
            pstDelete.setInt(1, id);

            int filasAfectadas = pstDelete.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Campeon con ID " + id + " eliminado correctamente.");

                if (!esUltimoCampeon) {
                    String sqlUpdate = "UPDATE Campeon SET id = id - 1 WHERE id > ?";
                    try (PreparedStatement pstUpdate = connection.prepareStatement(sqlUpdate)) {
                        pstUpdate.setInt(1, id);
                        pstUpdate.executeUpdate();
                    }

                    System.out.println("IDs actualizadas correctamente.");
                } else {
                    System.out.println("Este es el último campeon en la tabla. No se actualizarán las IDs.");
                }
            } else {
                System.out.println("No se encontró un campeon con ID " + id + ".");
            }
        }

        if (!esUltimoCampeon) {
            String sqlUpdate = "SELECT setval('campeon_id_seq', (SELECT MAX(id) FROM Campeon), false)";
            try (PreparedStatement pstUpdate = connection.prepareStatement(sqlUpdate)) {
                pstUpdate.execute();
            }
            System.out.println("Secuencia actualizada correctamente.");
        }
    }

    /**
     * Método privado para verificar si un campeón es el último en la tabla.
     *
     * @param campeonId ID del campeón.
     * @return `true` si es el último campeón, `false` de lo contrario.
     * @throws SQLException Si hay un error al ejecutar la consulta SQL.
     */
    private boolean esUltimoCampeonEnTabla(int campeonId) throws SQLException {
        String sqlSelect = "SELECT MAX(id) FROM Campeon";
        try (PreparedStatement pstSelect = connection.prepareStatement(sqlSelect)) {
            try (ResultSet rs = pstSelect.executeQuery()) {
                if (rs.next()) {
                    int ultimoId = rs.getInt(1);
                    return campeonId == ultimoId;
                } else {
                    throw new SQLException("No se pudo obtener el máximo ID de la tabla Campeon.");
                }
            }
        }
    }

    /**
     * Método privado para obtener el nombre de un campeón por su ID.
     *
     * @param id ID del campeón.
     * @return Nombre del campeón.
     * @throws SQLException Si hay un error al ejecutar la consulta SQL.
     */
    private String obtenerNombreCampeonPorId(int id) throws SQLException {
        String sqlSelect = "SELECT nombre FROM Campeon WHERE id = ?";
        try (PreparedStatement pstSelect = connection.prepareStatement(sqlSelect)) {
            pstSelect.setInt(1, id);

            try (ResultSet rs = pstSelect.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nombre");
                } else {
                    throw new SQLException("No se pudo obtener el nombre del campeón con ID: " + id);
                }
            }
        }
    }

    /**
     * Método privado para eliminar las habilidades de un campeón por su nombre.
     *
     * @param nombreCampeon Nombre del campeón.
     * @throws SQLException Si hay un error al ejecutar la consulta SQL.
     */
    private void eliminarHabilidadesPorNombreCampeon(String nombreCampeon) throws SQLException {
        String sqlDeleteHabilidades = "DELETE FROM Habilidad WHERE campeon_id IN (SELECT id FROM Campeon WHERE nombre = ?)";

        try (PreparedStatement pstDeleteHabilidades = connection.prepareStatement(sqlDeleteHabilidades)) {
            pstDeleteHabilidades.setString(1, nombreCampeon);
            pstDeleteHabilidades.executeUpdate();
        }
    }

    /**
     * Método para editar la información de un campeón por parte del usuario.
     *
     * @throws SQLException Si hay un error al ejecutar la consulta SQL.
     * @throws IOException  Si hay un error al leer la entrada del usuario.
     */
    public void editarCampeonPorUsuario() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Ingrese el ID del campeón que desea editar: ");
        int id = Integer.parseInt(br.readLine());

        String sqlSelect = "SELECT nombre, apodo, campeonesConRelacion, biografia, aparicionEnCinematicas, " +
                "numRelatosCortos, rol, raza, region, numDeAspectos, dificultad, region_id " +
                "FROM Campeon WHERE id = ?";
        String nombreActual = null;
        String apodoActual = null;
        int campeonesConRelacionActual = 0;
        String biografiaActual = null;
        boolean aparicionEnCinematicasActual = false;
        int numRelatosCortosActual = 0;
        String rolActual = null;
        String razaActual = null;
        String regionActual = null;
        int numDeAspectosActual = 0;
        String dificultadActual = null;
        int regionIdActual = 0;

        try (PreparedStatement pstSelect = connection.prepareStatement(sqlSelect)) {
            pstSelect.setInt(1, id);
            ResultSet rs = pstSelect.executeQuery();
            if (rs.next()) {
                nombreActual = rs.getString("nombre");
                apodoActual = rs.getString("apodo");
                campeonesConRelacionActual = rs.getInt("campeonesConRelacion");
                biografiaActual = rs.getString("biografia");
                aparicionEnCinematicasActual = rs.getBoolean("aparicionEnCinematicas");
                numRelatosCortosActual = rs.getInt("numRelatosCortos");
                rolActual = rs.getString("rol");
                razaActual = rs.getString("raza");
                regionActual = rs.getString("region");
                numDeAspectosActual = rs.getInt("numDeAspectos");
                dificultadActual = rs.getString("dificultad");
                regionIdActual = rs.getInt("region_id");
            } else {
                System.out.println("No se encontró el campeón con el ID proporcionado: " + id);
                return;
            }
        }

        System.out.print("¿Desea cambiar el nombre? (Si/No): ");
        String cambiarNombre = br.readLine();
        String nuevoNombre = (cambiarNombre.equalsIgnoreCase("si")) ?
                obtenerNuevoValor(br, "Introduzca el nuevo nombre: ") :
                nombreActual;

        System.out.print("¿Desea cambiar el apodo? (Si/No): ");
        String cambiarApodo = br.readLine();
        String nuevoApodo = (cambiarApodo.equalsIgnoreCase("si")) ?
                obtenerNuevoValor(br, "Introduzca el nuevo apodo: ") :
                apodoActual;

        System.out.print("¿Desea cambiar el número de campeones con relación? (Si/No): ");
        String cambiarCampeonesConRelacion = br.readLine();
        int nuevoCampeonesConRelacion = (cambiarCampeonesConRelacion.equalsIgnoreCase("si")) ?
                obtenerNuevoValorEntero(br, "Introduzca el nuevo número de campeones con relación: ") :
                campeonesConRelacionActual;

        System.out.print("¿Desea cambiar la biografía? (Si/No): ");
        String cambiarBiografia = br.readLine();
        String nuevaBiografia = (cambiarBiografia.equalsIgnoreCase("si")) ?
                obtenerNuevoValor(br, "Introduzca la nueva biografía: ") :
                biografiaActual;

        System.out.print("¿Desea cambiar la aparición en cinemáticas? (Si/No): ");
        String cambiarAparicionEnCinematicas = br.readLine();
        boolean nuevaAparicionEnCinematicas = (cambiarAparicionEnCinematicas.equalsIgnoreCase("si")) ?
                Boolean.parseBoolean(obtenerNuevoValor(br, "Introduzca la nueva aparición en cinemáticas (true/false): ")) :
                aparicionEnCinematicasActual;

        System.out.print("¿Desea cambiar el número de relatos cortos? (Si/No): ");
        String cambiarNumRelatosCortos = br.readLine();
        int nuevoNumRelatosCortos = (cambiarNumRelatosCortos.equalsIgnoreCase("si")) ?
                obtenerNuevoValorEntero(br, "Introduzca el nuevo número de relatos cortos: ") :
                numRelatosCortosActual;

        System.out.print("¿Desea cambiar el rol? (Si/No): ");
        String cambiarRol = br.readLine();
        String nuevoRol = (cambiarRol.equalsIgnoreCase("si")) ?
                obtenerNuevoValor(br, "Introduzca el nuevo rol: ") :
                rolActual;

        System.out.print("¿Desea cambiar la raza? (Si/No): ");
        String cambiarRaza = br.readLine();
        String nuevaRaza = (cambiarRaza.equalsIgnoreCase("si")) ?
                obtenerNuevoValor(br, "Introduzca la nueva raza: ") :
                razaActual;

        String nuevaRegion = regionActual;
        boolean nombreRegionValido = false;

        while (!nombreRegionValido) {
            System.out.print("¿Desea cambiar la región? (Si/No): ");
            String cambiarRegion = br.readLine();

            if (cambiarRegion.equalsIgnoreCase("si")) {
                nuevaRegion = obtenerNuevoValor(br, "Introduzca la nueva región: ");
                if (existeRegion(nuevaRegion)) {
                    nombreRegionValido = true;
                } else {
                    System.out.println("La región ingresada no existe. Por favor, ingrese un nombre de región válido.");
                }
            } else {
                nombreRegionValido = true; // No se desea cambiar la región, salir del bucle
            }
        }


        int nuevaRegionId = obtenerIdDeRegionPorNombre(nuevaRegion);

        System.out.print("¿Desea cambiar el número de aspectos? (Si/No): ");
        String cambiarNumDeAspectos = br.readLine();
        int nuevoNumDeAspectos = (cambiarNumDeAspectos.equalsIgnoreCase("si")) ?
                obtenerNuevoValorEntero(br, "Introduzca el nuevo número de aspectos: ") :
                numDeAspectosActual;

        System.out.print("¿Desea cambiar la dificultad? (Si/No): ");
        String cambiarDificultad = br.readLine();
        String nuevaDificultad = (cambiarDificultad.equalsIgnoreCase("si")) ?
                obtenerNuevoValor(br, "Introduzca la nueva dificultad: ") :
                dificultadActual;

        String sqlUpdate = "UPDATE Campeon SET nombre = ?, apodo = ?, campeonesConRelacion = ?, " +
                "biografia = ?, aparicionEnCinematicas = ?, numRelatosCortos = ?, " +
                "rol = ?, raza = ?, region = ?, numDeAspectos = ?, dificultad = ?, " +
                "region_id = ? WHERE id = ?";

        try (PreparedStatement pstUpdate = connection.prepareStatement(sqlUpdate)) {
            pstUpdate.setString(1, nuevoNombre);
            pstUpdate.setString(2, nuevoApodo);  // Ajustar según corresponda para los demás campos...
            pstUpdate.setInt(3, nuevoCampeonesConRelacion);
            pstUpdate.setString(4, nuevaBiografia);
            pstUpdate.setBoolean(5, nuevaAparicionEnCinematicas);
            pstUpdate.setInt(6, nuevoNumRelatosCortos);
            pstUpdate.setString(7, nuevoRol);
            pstUpdate.setString(8, nuevaRaza);
            pstUpdate.setString(9, nuevaRegion);
            pstUpdate.setInt(10, nuevoNumDeAspectos);
            pstUpdate.setString(11, nuevaDificultad);
            pstUpdate.setInt(12, nuevaRegionId);
            pstUpdate.setInt(13, id);

            int filasActualizadas = pstUpdate.executeUpdate();

            if (filasActualizadas > 0) {
                System.out.println("Campeón con ID " + id + " actualizado correctamente.");
            } else {
                System.out.println("No se encontró el campeón con el ID proporcionado: " + id);
            }
        }
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

    /**
     * Método privado para obtener el ID de una región por su nombre, sin importar mayúsculas o minúsculas.
     *
     * @param nombreRegion Nombre de la región.
     * @return ID de la región.
     * @throws SQLException Si hay un error al ejecutar la consulta SQL.
     */
    private int obtenerIdDeRegionPorNombre(String nombreRegion) throws SQLException {
        String sqlSelectRegionId = "SELECT id FROM Region WHERE UPPER(nombre) = UPPER(?)";

        try (PreparedStatement pstSelectRegionId = connection.prepareStatement(sqlSelectRegionId)) {
            pstSelectRegionId.setString(1, nombreRegion);
            ResultSet rs = pstSelectRegionId.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            } else {
                System.out.println("No se encontró la región con el nombre proporcionado: " + nombreRegion);
                return -1;
            }
        }
    }
}
