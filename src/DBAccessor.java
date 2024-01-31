//import java.io.IOException;
//import java.io.InputStream;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.Properties;
//
//public class DBAccessor {
//	private String dbname;
//	private String host;
//	private String port;
//	private String user;
//	private String password;
//	private String schema;
//	Connection conn = null;
//
//
//	/**
//	 * Initializes the class loading the database properties file and assigns values
//	 * to the instance variables.
//	 * 
//	 * @throws RuntimeException Properties file could not be found.
//	 */
//	public void init() {
//		Properties prop = new Properties();
//		InputStream propStream = this.getClass().getClassLoader().getResourceAsStream("db.properties");
//
//		try {
//			prop.load(propStream);
//			this.host = prop.getProperty("host");
//			this.port = prop.getProperty("port");
//			this.dbname = prop.getProperty("dbname");
//			this.schema = prop.getProperty("schema");
//		} catch (IOException e) {
//			String message = "ERROR: db.properties file could not be found";
//			System.err.println(message);
//			throw new RuntimeException(message, e);
//		}
//	}
//
//	
//	/**
//	 * Obtains a {@link Connection} to the database, based on the values of the
//	 * <code>db.properties</code> file.
//	 * 
//	 * @return DB connection or null if a problem occurred when trying to connect.
//	 */
//	public Connection getConnection(Identity identity) {
//
//		// Implement the DB connection
//		String url = null;
//		try {
//			// Loads the driver
//			Class.forName("org.postgresql.Driver");
//
//			// Preprara connexió a la base de dades
//			StringBuffer sbUrl = new StringBuffer();
//			sbUrl.append("jdbc:postgresql:");
//			if (host != null && !host.equals("")) {
//				sbUrl.append("//").append(host);
//				if (port != null && !port.equals("")) {
//					sbUrl.append(":").append(port);
//				}
//			}
//			sbUrl.append("/").append(dbname);
//			url = sbUrl.toString();
//
//			// Utilitza connexió a la base de dades
//			conn = DriverManager.getConnection(url, identity.getUser(), identity.getPassword());
//			conn.setAutoCommit(false);
//		} catch (ClassNotFoundException e1) {
//			System.err.println("ERROR: Al Carregar el driver JDBC");
//			System.err.println(e1.getMessage());
//		} catch (SQLException e2) {
//			System.err.println("ERROR: No connectat  a la BD " + url);
//			System.err.println(e2.getMessage());
//		}
//
//		// Sets the search_path
//		if (conn != null) {
//			Statement statement = null;
//			try {
//				statement = conn.createStatement();
//				statement.executeUpdate("SET search_path TO " + this.schema);
//				// missatge de prova: verificació
//				System.out.println("OK: connectat a l'esquema " + this.schema + " de la base de dades " + url
//						+ " usuari: " + user + " password:" + password);
//				System.out.println();
//				//
//			} catch (SQLException e) {
//				System.err.println("ERROR: Unable to set search_path");
//				System.err.println(e.getMessage());
//			} finally {
//				try {
//					statement.close();
//				} catch (SQLException e) {
//					System.err.println("ERROR: Closing statement");
//					System.err.println(e.getMessage());
//				}
//			}
//		}
//
//		return conn;
//	}
//
//	
////	public void altaAutor() throws SQLException, IOException {
////		Scanner reader = new Scanner(System.in);
////		System.out.println("Introdueix el id de l'autor");
////		int id = reader.nextInt();
////		System.out.println("Introdueix el nom");
////		reader.nextLine();
////		String nom = reader.nextLine();
////		System.out.println("Introdueix l'any de naixement");
////		String any_naixement = reader.nextLine();
////		System.out.println("Introdueix la nacionalitat");
////		String nacionalitat = reader.nextLine();
////		System.out.println("Es actiu? (S/N)");
////		String actiu = reader.nextLine();
////
////		Statement statement = null;
////		statement = conn.createStatement();
////		statement.executeUpdate("INSERT INTO autors VALUES (" + id + ",'" + nom + "','" + any_naixement + "','"
////				+ nacionalitat + "','" + actiu + "')");
////
////		conn.commit();
////
////	}
////
////	public void altaRevista() throws SQLException, NumberFormatException, IOException, ParseException {
////		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
////		Scanner reader = new Scanner(System.in);
////		System.out.println("Introdueix el id de la revista");
////		int id = reader.nextInt();
////		System.out.println("Introdueix el titol");
////		reader.nextLine();
////		String titol = reader.nextLine();
////		System.out.println("Introdueix la data de publicacio (yyyy-mm-dd)");
////		Date date = java.sql.Date.valueOf(reader.nextLine());
////
////		Statement statement = null;
////		statement = conn.createStatement();
////		statement.executeUpdate("INSERT INTO revistes (id_revista, titol, data_publicacio) VALUES (" + id + ",'" + titol
////				+ "','" + date + "')");
////
////		conn.commit();
////
////	}
////
////	public void altaArticle() throws SQLException, NumberFormatException,
////	  IOException, ParseException {
////	  
////	  // TODO demana per consola els valors dels diferents atributs // d'article,
////	  excepte aquells que poden ser nuls , i realitza la // inserció d'un registre
////	  
////	  // https://stackoverflow.com/questions/18614836/using-setdate-in-
////	  preparedstatement
////	  
////	  // SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); Scanner
////	  reader = new Scanner(System.in);
////	  System.out.println("Introdueix el id de l'article"); int id =
////	  reader.nextInt();
////	  System.out.println("Introdueix el id de l'autor de l'article"); int autor =
////	  reader.nextInt(); System.out.println("Introdueix el titol");
////	  reader.nextLine();
////	  
////	  String titol = reader.nextLine();
////	  System.out.println("Introdueix la data de creació (yyyy-mm-dd)"); Date
////	  data_creacio = java.sql.Date.valueOf(reader.nextLine());
////	  System.out.println("Es publicable? (S/N)"); String publicable =
////	  reader.nextLine();
////	  
////	  // Statement statement = null; // statement = conn.createStatement(); // st =
////	  conn.prepare
////	  
////	  String sql =
////	  "INSERT INTO articles (id_article, id_autor, titol, data_creacio, publicable) VALUES (?,?,?,?,?)"
////	  ; PreparedStatement pst = conn.prepareStatement(sql);
////	  
////	  pst.clearParameters(); pst.setInt(1, id); pst.setInt(2, autor);
////	  pst.setString(3, titol); pst.setDate(4, data_creacio); pst.setString(5,
////	  publicable);
////	  
////	  pst.executeUpdate(); // statement.executeUpdate("INSERT INTO articles
////	  (id_article, id_autor, titol, // data_creacio, publicable) VALUES //
////	  ("+id+","+autor+",'"+titol+"','"+data_creacio+"','"+publicable+"')");
////	  
////	  conn.commit();
////	  
////	  }
////
////	public void afegeixArticleARevista(Connection conn) throws SQLException {
////	  
////	  ResultSet rs = null; Statement st =
////	  conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
////	  ResultSet.CONCUR_UPDATABLE); InputStreamReader isr = new
////	  InputStreamReader(System.in); BufferedReader br = new BufferedReader(isr);
////	  
////	  try { rs =
////	  st.executeQuery("SELECT * FROM articles WHERE id_revista IS NULL");
////	  
////	  if (rs.next() == false) {
////	  System.out.println("No hi ha articles pendents d'associar revistes. "); }
////	  else { do { System.out.println("Titol: " + rs.getString("titol"));
////	  
////	  System.out.println("Vol incorporar aquest article a una revista?"); String
////	  resposta = br.readLine();
////	  
////	  if (resposta.equals("si")) { // demana l'identificador de la revista
////	  System.out.println("Introdueix el id de la revista"); int idRevista =
////	  Integer.parseInt(br.readLine()); // actualitza el camp
////	  rs.updateInt("id_revista", idRevista);
////	  
////	  // actualitza la fila rs.updateRow(); } } while (rs.next()); }
////	  
////	  } catch (Exception e) { e.printStackTrace(); } }
////	  
////	  // TODO public void actualitzarTitolRevistes(Connection conn) throws
////	  SQLException { // TODO // seguint l'exemple de la funció
////	  afegeixArticleARevista: // definir variables locals // realitzar la consulta
////	  de totes les revistes // mentre hi hagi revistes: // Mostrar el títol de la
////	  revista // demanar si es vol canviar el seu títol // en cas de que la
////	  resposta sigui "si" // demanar el nou títol per la revista // actualitzar el
////	  camp // actualitzar la fila
////	  
////	  ResultSet rs = null; String resposta = null;
////	  
////	  Statement st = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
////	  ResultSet.CONCUR_UPDATABLE); InputStreamReader isr = new
////	  InputStreamReader(System.in); BufferedReader br = new BufferedReader(isr);
////	  
////	  try { rs = st.executeQuery("SELECT * FROM revistes");
////	  
////	  if (rs.next() == false) {
////	  System.out.println("No hi ha revistes insertades. "); } else { do {
////	  System.out.println("Titol: " + rs.getString("titol"));
////	  
////	  System.out.println("Vols cambiar el títol a aquesta revista? (si/no)");
////	  resposta = br.readLine();
////	  
////	  if (resposta.equals("si")) { // TODO demana l'identificador de la revista
////	  System.out.println("Introdueix el nou títol"); String nouTitol =
////	  br.readLine(); // actualitza el camp rs.updateString("titol", nouTitol); //
////	  actualitza la fila rs.updateRow(); } } while (rs.next()); }
////	  
////	  } catch (Exception e) { e.printStackTrace(); } }
////	  
////	  // TODO public void desassignaArticleARevista(Connection conn) throws
////	  SQLException, IOException {
////	  
////	  // TODO // seguint l'exemple de la funció afegeixArticleARevista: // definir
////	  variables locals // sol·licitar l'identificador de la revista // realitzar la
////	  consulta de tots els articles que corresponen a aquesta // revista // si no
////	  hi ha articles, emetre el missatge corresponent // en altre cas, mentre hi
////	  hagi articles: // Mostrar el títol de l'article i l'identificador de la
////	  revista // demanar si es vol rescindir la seva incorporació a la revista //
////	  en cas de que la resposta sigui "si" // actualitzar el camp corresponent a
////	  null // actualitzar la fila // en altre cas imprimir "operació cancel·lada"
////	  ResultSet rs = null; Statement st =
////	  conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
////	  ResultSet.CONCUR_UPDATABLE); InputStreamReader isr = new
////	  InputStreamReader(System.in); BufferedReader br = new BufferedReader(isr);
////	  
////	  System.out.
////	  println("Introdueix el id de la revista de la que vulguis llistar els articles"
////	  ); int idRevista = Integer.parseInt(br.readLine());
////	  
////	  try { rs = st.executeQuery("SELECT * FROM articles WHERE id_revista = " +
////	  idRevista);
////	  
////	  if (rs.next() == false) {
////	  System.out.println("No hi ha articles assignats a aquesta revista. "); } else
////	  { do { System.out.println("Titol: " + rs.getString("titol"));
////	  System.out.println("ID revista: " + rs.getString("id_revista"));
////	  
////	  System.out.println("Vols rescindir la seva incorporació a la revista?");
////	  String resposta = br.readLine();
////	  
////	  if (resposta.equals("si")) { rs.updateNull("id_revista"); rs.updateRow(); }
////	  else System.out.println("Operació cancel·lada"); } while (rs.next()); } }
////	  catch (Exception e) { e.printStackTrace(); } }
////
////	public void mostraAutors() throws SQLException, IOException {
////		Statement st = conn.createStatement();
////		Scanner reader = new Scanner(System.in);
////		ResultSet rs;
////
////		rs = st.executeQuery("SELECT * FROM autors");
////		while (rs.next())
////			System.out.println("ID: " + rs.getString("id_autor") + "\tNom: " + rs.getString("nom") + "\tAny Naixement: "
////					+ rs.getString("any_naixement") + "\tNacionalitat: " + rs.getString("nacionalitat") + "\tActiu: "
////					+ rs.getString("actiu"));
////		rs.close();
////		st.close();
////	}
////
////	public void mostraRevistes() throws SQLException, IOException {
////		Statement st = conn.createStatement();
////		Scanner reader = new Scanner(System.in);
////		ResultSet rs;
////
////		rs = st.executeQuery("SELECT * FROM revistes");
////		while (rs.next())
////			System.out.println(
////					"ID: " + rs.getString(1) + "\tTitol: " + rs.getString(2) + "\tData Publicacio: " + rs.getString(3));
////		rs.close();
////		st.close();
////	}
////
////	public void mostraRevistesArticlesAutors() throws SQLException, IOException {
////		Statement st = conn.createStatement();
////		Scanner reader = new Scanner(System.in);
////		ResultSet rs;
////
////		rs = st.executeQuery(
////				"SELECT a.nom, r.titol, ar.titol FROM autors a, revistes r, articles ar WHERE ar.id_autor=a.id_autor AND ar.id_revista=r.id_revista");
////		while (rs.next()) {
////			System.out.println("Nom autor: " + rs.getString(1) + "\tNomRevista: " + rs.getString(2) + "\tNom article: "
////					+ rs.getString(3));
////		}
////
////		rs.close();
////		st.close();
////
////	}
////
////	public void sortir() throws SQLException {
////		System.out.println("ADÉU!");
////		conn.close();
////		System.exit(0);
////	}
////
////	// TODO public void carregaAutors(Connection conn) throws SQLException,
////	NumberFormatException,IOException
////
////	{ // TODO // mitjançant Prepared Statement
////	  // per a cada línia del fitxer autors.csv // realitzar la inserció
////	  corresponent String sql =
////	  "INSERT INTO autors (id_autor, nom, any_naixement, nacionalitat, actiu) VALUES (?,?,?,?,?)"
////	  ; PreparedStatement pst = conn.prepareStatement(sql); BufferedReader br = new
////	  BufferedReader(new FileReader("src/autors.csv")); String line; while ((line =
////	  br.readLine()) != null) { StringTokenizer st = new StringTokenizer(line,
////	  ",");
////	  
////	  pst.clearParameters(); pst.setInt(1, Integer.parseInt(st.nextToken()));
////	  pst.setString(2, st.nextToken()); pst.setString(3, st.nextToken());
////	  pst.setString(4, st.nextToken()); pst.setString(5, st.nextToken());
////	  
////	  pst.executeUpdate(); }
////	  
////	  conn.commit(); }
//	 
//}
