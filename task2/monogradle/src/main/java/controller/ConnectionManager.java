package controller;

import java.sql.Connection;
import java.sql.SQLException;
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

/**
 * Diese Klasse realisiert das Connection-Pooling. Dabei müssen folgende Konstanten
 * gesetzt werden:<br><br>
 * DRIVER - die Driver-Klasse<br>
 * USER - der Benutzername, unter welchem die Anmeldung erfolgen soll<br>
 * PASSWORD - Das Passwort des Benutzers der sich anmeldet<br>
 * SERVERNAME - der Name des Datenbankservers<br>
 * DATABASENAME - der Name der Datenbank auf die zugegriffen wird
 * @author Michael Wild
 */
public class ConnectionManager
{
	/*
	 * Private, statische Konstanten
	 */
	private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String USER = "root";
	private static final String PASSWORD = "root";
	private static final String SERVERNAME = "172.17.0.2";
	private static final String DATABASENAME = "MARS";
	private static final int LOGINTIMEOUT = 5;
	
	/*
	 * Private, statische Variable zur Verwaltung der DataSource
	 */
	private static MysqlConnectionPoolDataSource ds = null;
	
	// Nachfolgender Block wird nur einmal beim ersten Zugriff auf
	// Klasse durchgeführt
	static {
		try {
			// Treiber instanziieren
			Class.forName(DRIVER);
			// DataSource instanziieren und Eigenschaften bestimmen
			ds = new MysqlConnectionPoolDataSource();
			ds.setUser(USER);
			ds.setPassword(PASSWORD);
			ds.setServerName(SERVERNAME);
			ds.setDatabaseName(DATABASENAME);
			ds.setLoginTimeout(LOGINTIMEOUT);
		} catch (ClassNotFoundException e) {
			// Treiber nicht gefunden
			System.out.println("ConnectionManager: " + e.getMessage());
		}	catch (SQLException e) {
			// LoginTimeout nicht richtig gesetzt
			System.out.println("ConnectionManager: " + e.getMessage());
		}
	}
	
	/**
	 * Eine Verbindung aus dem Connection Pool der DataSource wird bereit
	 * gestellt
	 * @return eine Verbindung aus dem Connection Pool
	 * @throws SQLException wenn bei der Bereitstellung ein Fehler aufgetreten
	 * ist
	 */
	public static Connection getConnection() throws SQLException {
		return ds.getConnection();
	}
}
