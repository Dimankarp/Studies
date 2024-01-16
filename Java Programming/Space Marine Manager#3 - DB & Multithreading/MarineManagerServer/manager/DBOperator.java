package manager;

import java.sql.*;
import java.util.Properties;

public class DBOperator {

    private String currentURL;

    public String getURL(){return currentURL;}

    private Connection currConnection;



    //Trying into PostGreSQL

    public DBOperator(String hostname, String dbName, Properties props) throws SQLException {
        currentURL = String.format("jdbc:postgresql://%s/%s", hostname, dbName);
        try {
            currConnection = DriverManager.getConnection(currentURL, props);
        } catch (SQLException e) {
            throw new SQLException(String.format("Couldn't connect to %s: %s", currentURL, e.getMessage()));
        }
    }

    public void close() throws SQLException {
        currConnection.close();
    }

    public PreparedStatement getStatement(String query) throws SQLException {
        return currConnection.prepareStatement(query);
    }

    public PreparedStatement getStatement(String query, int options) throws SQLException {
        return currConnection.prepareStatement(query, options);
    }

    //Doesn't look that protected, yeah...
    public ResultSet executeQuery(QueryCreator creator, Object ... queryEntities) throws SQLException {
        return creator.getQuery(queryEntities).executeQuery();
    }

    public int executeUpdate(QueryCreator creator, Object ... queryEntities) throws SQLException {
        return creator.getQuery(queryEntities).executeUpdate();
    }

    public boolean tableExists(String tableName) throws SQLException {
        DatabaseMetaData meta = currConnection.getMetaData();
        ResultSet resultSet = meta.getTables(null, null, tableName, new String[] {"TABLE"});
        return resultSet.next();
    }







}
