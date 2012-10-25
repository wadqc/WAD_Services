/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Ralph Berendsen <>
 */
public class PacsDatabaseConnection {
    
    
    public static Connection conDb(DatabaseParameters dbParam) {
        java.sql.Connection con = null;        

        try {
            Class.forName(dbParam.driverclass).newInstance();            
            //Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(dbParam.connectionURL, dbParam.username, dbParam.password);            
            return con;
        } catch (SQLException ex) {
            Logger.getLogger(PacsDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(PacsDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(PacsDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PacsDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(PacsDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
    } 
}
