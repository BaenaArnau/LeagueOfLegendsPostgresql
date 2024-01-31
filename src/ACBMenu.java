import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Clase que representa un menú para interactuar con el sistema.
 */
public class ACBMenu {
	private int option; // Variable para almacenar la opción seleccionada en el menú.

	/**
	 * Constructor de la clase ACBMenu.
	 */
	public ACBMenu() {
		super();
	}

	/**
	 * Método para mostrar el menú principal y obtener la opción seleccionada.
	 *
	 * @return La opción seleccionada.
	 */
	public int mainMenu() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		do {
			System.out.println("┌───────────────────────────────────┐");
			System.out.println("│        MENU  PRINCIPAL            |");
			System.out.println("├───────────────────────────────────┤");
			System.out.println("│  (1)  - Añadir Region             │");
			System.out.println("│  (2)  - Eliminar Region           │");
			System.out.println("│  (3)  - Añadir Campeon            │");
			System.out.println("│  (4)  - Eliminar Campeon          │");
			System.out.println("│  (5)  - Añadir Habilidad          │");
			System.out.println("│  (6)  - Eliminar Habilidad        │");
			System.out.println("│  (7)  - Editar Region             │");
			System.out.println("│  (8)  - Editar Campeon            │");
			System.out.println("│  (9)  - Editar Habilidad          │");
			System.out.println("│ (10)  - Listar Regiones           │");
			System.out.println("│ (11)  - Listar Campeones          │");
			System.out.println("│ (12)  - Listar Habilidades        │");
			System.out.println("│ (13)  - Añadir CSV                │");
			System.out.println("│ (14)  - Eliminar campos Region    │");
			System.out.println("│ (15)  - Eliminar campos Campeon   │");
			System.out.println("│ (16)  - Eliminar campos Habilidad │");
			System.out.println("│  (0)  - Salir                     │");
			System.out.println("└───────────────────────────────────┘");
			try {
				option = Integer.parseInt(br.readLine());
			} catch (NumberFormatException | IOException e) {
				System.out.println("valor no vàlid");
				e.printStackTrace();

			}

		} while (option != 0 && option != 1 && option != 2 && option != 3 && option != 4 && option != 5 && option != 6 && option != 7
				&& option != 8 && option != 9 && option != 10 && option != 11 && option != 12 && option != 13 && option != 14 && option != 15 && option != 16);

		return option;
	}

	/**
	 * Método para mostrar el menú de lista de regiones y obtener la opción seleccionada.
	 *
	 * @return La opción seleccionada.
	 */
	public int listarRegiones() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		do {
			System.out.println("┌───────────────────────────────────────┐");
			System.out.println("│       MENU DE LISTA DE REGIONES       |");
			System.out.println("├───────────────────────────────────────┤");
			System.out.println("│  (1)  - Listar por abecedario         │");
			System.out.println("│  (2)  - Listar por id                 │");
			System.out.println("│  (3)  - Listar por num de historias   │");
			System.out.println("│  (0)  - Salir                         │");
			System.out.println("└───────────────────────────────────────┘");
			try {
				option = Integer.parseInt(br.readLine());
			} catch (NumberFormatException | IOException e) {
				System.out.println("valor no vàlid");
				e.printStackTrace();

			}

		} while (option != 1 && option != 2 && option != 3 && option != 0);

		return option;
	}

	/**
	 * Método para mostrar el menú de lista de campeones y obtener la opción seleccionada.
	 *
	 * @return La opción seleccionada.
	 */
	public int listarCampeones() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		do {
			System.out.println("┌───────────────────────────────────────┐");
			System.out.println("│       MENU DE LISTA DE CAMPEONES      |");
			System.out.println("├───────────────────────────────────────┤");
			System.out.println("│  (1)  - Listar nombre por abecedario  │");
			System.out.println("│  (2)  - Listar apodo por abecedario   │");
			System.out.println("│  (3)  - Listar por id                 │");
			System.out.println("│  (4)  - Listar por Region             │");
			System.out.println("│  (5)  - Listar por Rol                │");
			System.out.println("│  (6)  - Listar por Raza               │");
			System.out.println("│  (7)  - Listar por Dificultad         │");
			System.out.println("│  (0)  - Salir                         │");
			System.out.println("└───────────────────────────────────────┘");
			try {
				option = Integer.parseInt(br.readLine());
			} catch (NumberFormatException | IOException e) {
				System.out.println("valor no vàlid");
				e.printStackTrace();

			}

		} while (option != 0 && option != 1 && option != 2 && option != 3 && option != 4 && option != 5 && option != 6 && option != 7);

		return option;
	}

	/**
	 * Método para mostrar el menú de lista de habilidades y obtener la opción seleccionada.
	 *
	 * @return La opción seleccionada.
	 */
	public int listarHabilidades() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		do {
			System.out.println("┌───────────────────────────────────────┐");
			System.out.println("│      MENU DE LISTA DE HABILIDADES     |");
			System.out.println("├───────────────────────────────────────┤");
			System.out.println("│  (1)  - Listar por abecedario         │");
			System.out.println("│  (2)  - Listar por id                 │");
			System.out.println("│  (3)  - Listar por Campeon            │");
			System.out.println("│  (4)  - Listar solo pasivas           │");
			System.out.println("│  (5)  - Listar por tecla              │");
			System.out.println("│  (0)  - Salir                         │");
			System.out.println("└───────────────────────────────────────┘");
			try {
				option = Integer.parseInt(br.readLine());
			} catch (NumberFormatException | IOException e) {
				System.out.println("valor no vàlid");
				e.printStackTrace();

			}

		} while (option != 0 && option != 1 && option != 2 && option != 3 && option != 4 && option != 5);

		return option;
	}
}
