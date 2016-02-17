package gov.boc.trade.odbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class OdbcTest {

    public static void main(String[] args) throws SQLException {
        Connection con = getConnection();
        viewTable(con);
    }

    public static Connection getConnection() {
        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", "root");
        connectionProps.put("password", "root");

        try {
            conn = DriverManager.getConnection("jdbc:odbc:MarkLogicSQL", connectionProps);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("Connected to database");

        return conn;
    }

    public static void viewTable(Connection con) throws SQLException {

            Statement stmt = null;
            String query = "select * from employees";
            try {
                stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    String coffeeName = rs.getString("employee_uri");
                    String supplierID = rs.getString("employee_collection");
                    long price = rs.getLong("employee_id");
                    String sales = rs.getString("employee_name");
                    String role = rs.getString("employee_role");
                    String opCode = rs.getString("employee_operatorcode");

                    System.out.println(coffeeName + "\t" + supplierID +
                                       "\t" + price + "\t" + sales +
                                       "\t" + role + "\t" + opCode);
                }
            } catch (SQLException e ) {
                e.printStackTrace();
            } finally {
                if (stmt != null) { stmt.close(); }
            }
        }

}
