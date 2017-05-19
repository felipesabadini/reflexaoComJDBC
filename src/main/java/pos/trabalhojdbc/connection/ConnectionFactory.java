package pos.trabalhojdbc.connection;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private ConnectionFactory() {}

    public static Connection getInstance(DatabaseType databaseType, String user, String password, String database) throws SQLException {
        System.out.println( "Criando conexão !" );

        String url;
        switch (databaseType) {
            case MYSQL:
                url = "jdbc:mysql://localhost:3306/" + database;
                break;
            default:
                throw new SQLException( "Connector not found" );
        }

        return DriverManager.getConnection(url, user, password);
    }
}
