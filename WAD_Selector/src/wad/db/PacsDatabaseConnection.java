/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.db;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Ralph Berendsen <>
 */
public class PacsDatabaseConnection {
    public static final String DATABASE_USER = "user";
    public static final String DATABASE_PASSWORD = "password";
    public static final String MYSQL_AUTO_RECONNECT = "autoReconnect";
    public static final String MYSQL_MAX_RECONNECTS = "maxReconnects";
    
    
    public static Connection conDb(DatabaseParameters dbParam) {
         String driver = dbParam.driverclass;

         try {Class.forName(driver);
            String dbURL = dbParam.connectionURL;
            String dbUsername = dbParam.username;
            String dbPassword = dbParam.password;

            java.util.Properties connProperties = new java.util.Properties();
            connProperties.put(DATABASE_USER, dbUsername);
            connProperties.put(DATABASE_PASSWORD, dbPassword);

            connProperties.put(MYSQL_AUTO_RECONNECT, "true");

            connProperties.put(MYSQL_MAX_RECONNECTS, "4");
            return DriverManager.getConnection(dbURL, connProperties);
        } catch (SQLException ex) {
            Logger.getLogger(PacsDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PacsDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
        }
         return null;
        
    }
    
    public static void closeDb(Connection con, Statement stmt, ResultSet rs){
        try {
            if (rs!=null){                
                rs.close();  
            }
            if (stmt!=null){
                stmt.close();
            } 
            if (con!=null){
                con.close();
            }
        } catch (SQLException ex) {
                    Logger.getLogger(PacsDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
    } 
}
