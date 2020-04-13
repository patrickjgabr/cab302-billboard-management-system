import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public interface Database {

    boolean isInTable();

    void updateDatabase();

    void addToDatabase();
}
