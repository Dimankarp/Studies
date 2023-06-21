package manager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface QueryCreator{

    abstract PreparedStatement getQuery (Object ... queryEntities) throws SQLException;

}
