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
 * @author Ralph Berendsen <>
 */
public class PacsDatabaseConnection {
    
    private static Log log = LogFactory.getLog(PacsDatabaseConnection.class);
    
    public static Connection conDb(DatabaseParameters dbParam) {
        java.sql.Connection con = null;        

        try {
            Class.forName(dbParam.driverclass).newInstance();            
            //Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(dbParam.connectionURL, dbParam.username, dbParam.password);            
            return con;
        } catch (SQLException ex) {
            log.error(ex);
            //Logger.getLogger(PacsDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
            //LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}", new Object[]{PacsDatabaseConnection.class.getName(), ex});
        } catch (InstantiationException ex) {
            log.error(ex);
            //Logger.getLogger(PacsDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
            //LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}", new Object[]{PacsDatabaseConnection.class.getName(), ex});
        } catch (IllegalAccessException ex) {
            log.error(ex);
            //Logger.getLogger(PacsDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
            //LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}", new Object[]{PacsDatabaseConnection.class.getName(), ex});
        } catch (ClassNotFoundException ex) {
            log.error(ex);
            //Logger.getLogger(PacsDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
            //LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}", new Object[]{PacsDatabaseConnection.class.getName(), ex});
        }
        return con;
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
                    log.error(ex);
                    //Logger.getLogger(PacsDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
                    //LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}", new Object[]{PacsDatabaseConnection.class.getName(), ex});
                }
    } 
}
