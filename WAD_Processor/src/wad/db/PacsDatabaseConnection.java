/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.db;

import java.sql.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 *
 * @author Ralph Berendsen 
 */
public class PacsDatabaseConnection {
    public static final String DATABASE_USER = "user";
    public static final String DATABASE_PASSWORD = "password";
    public static final String MYSQL_AUTO_RECONNECT = "autoReconnect";
    public static final String MYSQL_MAX_RECONNECTS = "maxReconnects";
    
    private static Log log = LogFactory.getLog(PacsDatabaseConnection.class);
    
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
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{PacsDatabaseConnection.class.getName(), ex});
            log.error(ex);
        } catch (ClassNotFoundException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{PacsDatabaseConnection.class.getName(), ex});
            log.error(ex);
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
                    //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{PacsDatabaseConnection.class.getName(), ex});
            log.error(ex);
                }
    } 
}
