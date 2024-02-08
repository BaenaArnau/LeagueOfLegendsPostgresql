import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * Clase principal que contiene el método main para ejecutar la aplicación del Catálogo de Campeones y Habilidades (ACB).
 */
public class ACBMain {

	/**
	 * Método principal que inicia la ejecución de la aplicación ACB.
	 *
	 * @param args Argumentos de la línea de comandos (no se utilizan).
	 * @throws IOException    Si hay un error de entrada/salida.
	 * @throws SQLException   Si hay un error de base de datos.
	 * @throws ParseException Si hay un error al analizar una cadena en una fecha.
	 * @throws CsvException   Si hay un error relacionado con el formato CSV.
	 */
	public static void main(String[] args) throws IOException, SQLException, ParseException, CsvException {
		ACBMenu menu = new ACBMenu();
		
		ConnectionFactory connectionFactory = ConnectionFactory.getInstance();
		Connection c = connectionFactory.connect();

		RegionController regionController = new RegionController(c);
		CampeonController campeonController = new CampeonController(c);
		HabilidadController habilidadController = new HabilidadController(c);

		int opciones;

		int option = menu.mainMenu();
		while (option >= 0 && option < 17) {
			switch (option) {
			case 1:
				regionController.addRegion();
				break;

			case 2:
				regionController.eliminarRegionPorId();
				break;

			case 3:
				campeonController.addCampeon();
				break;

			case 4:
				campeonController.eliminarCampeonPorId();
				break;

			case 5:
				habilidadController.addHabilidad();
				break;

			case 6:
				habilidadController.eliminarHabilidadesPorId();
				break;

			case 7:
				regionController.editarRegionPorUsuario();
				break;

			case 8:
				campeonController.editarCampeonPorUsuario();
				break;

			case 9:
				habilidadController.editarHabilidadPorUsuario();
				break;

			case 10:
				opciones = menu.listarRegiones();
				while(opciones >= 0 && opciones < 4){
					switch (opciones){
						case 1:
							regionController.listarRegionesOrdenadoPor("nombre");
							break;

						case 2:
							regionController.listarRegionesOrdenadoPor("id");
							break;

						case 3:
							regionController.listarRegionesOrdenadoPor("historiasRelacionada");
							break;

						case 0:
							System.out.println("Saliendo...");
							break;

						default:
							System.out.println("Introdueixi una de les opcions anteriors");
							break;
					}
					if (opciones == 0) {
						break;
					}

					opciones = menu.listarRegiones();
				}
				break;

			case 11:
				opciones = menu.listarCampeones();
				while(opciones >= 0 && opciones < 8){
					switch (opciones){
						case 1:
							campeonController.listarCampeonesOrdeandoPor("nombre");
							break;

						case 2:
							campeonController.listarCampeonesOrdeandoPor("apodo");
							break;

						case 3:
							campeonController.listarCampeonesOrdeandoPor("id");
							break;

						case 4:
							campeonController.listarCampeonesOrdeandoPor("region");
							break;

						case 5:
							campeonController.listarCampeonesOrdeandoPor("rol");
							break;

						case 6:
							campeonController.listarCampeonesOrdeandoPor("raza");
							break;

						case 7:
							campeonController.listarCampeonesOrdeandoPor("dificultad");
							break;

						case 0:
							System.out.println("Saliendo...");
							break;

						default:
							System.out.println("Introdueixi una de les opcions anteriors");
							break;
					}
					if (opciones == 0) {
						break;
					}

					opciones = menu.listarCampeones();
				}
				break;

			case 12:
				opciones = menu.listarHabilidades();
				while(opciones >= 0 && opciones < 6){
					switch (opciones){
						case 1:
							habilidadController.listarHabilidadesOrdenandoPor("nombre");
							break;

						case 2:
							habilidadController.listarHabilidadesOrdenandoPor("id");
							break;

						case 3:
							habilidadController.listarHabilidadesOrdenandoPor("campeon");
							break;

						case 4:
							habilidadController.listarHabilidadesOrdenandoPor("pasiva");
							break;

						case 5:
							habilidadController.listarHabilidadesOrdenandoPor("asignacionDeTecla");
							break;

						case 0:
							System.out.println("Saliendo...");
							break;

						default:
							System.out.println("Introdueixi una de les opcions anteriors");
							break;
					}
					if (opciones == 0) {
						break;
					}

					opciones = menu.listarHabilidades();
				}
				break;

			case 13:
				regionController.cargarDatosDesdeCSV();
				campeonController.cargarDatosDesdeCSV();
				habilidadController.cargarDatosHabilidadesDesdeCSV();
				break;

			case 14:
				habilidadController.vaciarTabla();
				campeonController.vaciarTabla();
				regionController.vaciarTabla();
				break;

			case 15:
				habilidadController.vaciarTabla();
				campeonController.vaciarTabla();
				break;

			case 16:
				habilidadController.vaciarTabla();
				break;

			case 0:
				System.exit(0);
				break;

			default:
				System.out.println("Introdueixi una de les opcions anteriors");
				break;

			}
			option = menu.mainMenu();
		}
	}
}