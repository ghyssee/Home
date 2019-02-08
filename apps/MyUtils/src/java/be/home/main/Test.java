package be.home.main;

import be.home.common.main.BatchJobV2;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class Test extends BatchJobV2 {

    private static final Logger log = getMainLog(HelloWorld.class);

    public static void main(String args[]) {
        connectDB();
    }

    public static void connectDB(){
        System.out.println("-------- Oracle JDBC Connection Testing ------");

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

        } catch (ClassNotFoundException e) {

            System.out.println("Where is your Oracle JDBC Driver?");
            e.printStackTrace();
            return;

        }

        System.out.println("Oracle JDBC Driver Registered!");

        Connection connection = null;

        try {

            connection = DriverManager.getConnection(
                "jdbc:oracle:thin:@ldap://oid.netpost/FARD2,cn=OracleContext,dc=pr,dc=netpost,dc=be", "DATA_MGR", "MGR_DATA");

        } catch (SQLException e) {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;

        }

        if (connection != null) {
            System.out.println("You made it, take control your database now!");
        } else {
            System.out.println("Failed to make connection!");
        }
    }

    public static void test1(){
        String path = "Test - 0001-0003";
        if (path.matches("(.* - )?[0-9]{3,4} ?- ?[0-9]{3,4}")){
            System.out.println("Not Main Path");
        }
        else {
            System.out.println(path);

        }

    }

    @Override
    public void run() {

    }
}
