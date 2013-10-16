/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_collector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Ralph Berendsen <>
 */
public class CheckCollectorVersion {
    private static Log log = LogFactory.getLog(CheckNewStudy.class);
    public boolean passedCheck = false;
            
    public void CheckVersion(Connection iqcConnection, String version){
        log.debug("Controleren van versies:");
        
        try {
            String dbVersion = getDatabaseVersion(iqcConnection);
            UpdateVersion(iqcConnection, version); 
            
            Integer dbMajor= Integer.parseInt(dbVersion.split("\\.")[0]);
            //Integer dbMinor= Integer.parseInt(dbVersion.split("\\.")[1]);
            //Integer dbPatch= Integer.parseInt(dbVersion.split("\\.")[2]);
            
            Integer versionMajor = Integer.parseInt(version.split("\\.")[0]);
            //Integer versionMinor = Integer.parseInt(version.split("\\.")[1]);
            //Integer versionPatch = Integer.parseInt(version.split("\\.")[2]); 
            
            if (dbMajor==versionMajor) {
            passedCheck = true;
                log.info("Database versie:"+dbVersion);
                log.info("Collector versie:"+version);
            } else {
                log.fatal("Database major versie: "+dbVersion+" komt niet overeen met Collector major versie:"+version);
                log.fatal("Collector wordt niet gestart");
            }
            
        } catch (NullPointerException ex) {
            log.error("database error: "+ex);
        }
    }
    
    public static String getDatabaseVersion(Connection dbConnection){
        ResultSet rs_version;        
        Statement stmt_dbversion;        
        
        try {
            stmt_dbversion = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String statement = "SELECT value FROM config WHERE property='Version_Database'";
            rs_version = stmt_dbversion.executeQuery(statement);            
            if (rs_version.next()) {
                String version = rs_version.getString("value");
                return version;                               
            } else {
                return null;
            }
        } catch (SQLException ex) {
            log.error("Check version - getDatabaseVersion : "+ex);
        }
        return null;
    }
    
    public static void UpdateVersion(Connection dbConnection, String version){
        Statement stmt_Write;
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        
        String sqlStatement = "UPDATE config SET value='"+version+"', date_modified='"+dateFormat.format(date)+"'  WHERE property='Version_Collector'";            
        
        try {                        
            stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            int count = stmt_Write.executeUpdate(sqlStatement); 
            stmt_Write.close();
        } catch (SQLException ex) {
            log.error(ex);            
        }        
    }
}
