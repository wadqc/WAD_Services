/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Ralph Berendsen 
 */
public class WriteToIqcDatabase {
    
    private static Log log = LogFactory.getLog(GetPatientFromIqcDatabase.class);
    
    public static void WriteResulsetInTable(Connection dbConnection, ResultSet rsRead, String tableName){
        Statement stmt_Write;
        ResultSet rs_Write;
                
        //INSERT INTO tablename (colomns,..,) values ('','')
        String sqlTable = "INSERT INTO "+tableName+"(";
        String sqlColomn = "";
        String sqlMiddle = ") values (";
        String sqlValues = "";
        String sqlEnd = ")";
        String colomnLabel;
        String sqlStatement = "";
        try {
            ResultSetMetaData rsWriteMetaData = rsRead.getMetaData();
            int nrOfColomns = rsWriteMetaData.getColumnCount();
            while (rsRead.next()){
                for (int i=1;i<=nrOfColomns;i++){
                    if (!sqlColomn.equals("")){
                        sqlColomn = sqlColomn+", ";
                        sqlValues = sqlValues+", ";
                    }
                    colomnLabel = rsWriteMetaData.getColumnLabel(i);  
                    sqlColomn = sqlColomn+colomnLabel;                    
                    Object value = rsRead.getObject(colomnLabel);
                    if (value==null){
                        sqlValues = sqlValues+rsRead.getString(colomnLabel);
                    } else {                        
                        if (value instanceof Integer){
                            sqlValues = sqlValues+value;
                        } else if (value instanceof Boolean){
                            sqlValues = sqlValues+value;
                        } else if (value instanceof String){
                            String strValue = (String) value;
                            strValue = strValue.replace("\\", "\\\\");
                            strValue = strValue.replace("\'", "\\'");
                            sqlValues = sqlValues+"'"+strValue+"'";
                        } else {
                            sqlValues = sqlValues+"'"+value+"'";
                        }
                    }
                }
                sqlStatement = sqlTable+sqlColomn+sqlMiddle+sqlValues+sqlEnd;
                System.out.println("SQl-statement : " + sqlStatement);
                stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                int count = stmt_Write.executeUpdate(sqlStatement);
                stmt_Write.close();
                System.out.println("Output executeUpdate : " + count);
            }
        } catch (SQLException ex) {                        
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{WriteToIqcDatabase.class.getName(), ex});
            log.error(ex);
        }        
    }
    
    //Vullen collector_study_status 
    public static void WriteStudyStatus(Connection dbConnection, String studyFk, String status){
        Statement stmt_Write;        
        
        Timestamp creationTime = new Timestamp(System.currentTimeMillis());
        
        //INSERT INTO tablename (colomns,..,) values ('','')        
        String sqlStatement = "INSERT INTO collector_study_status (study_fk, study_status) values ('"+studyFk+"','"+status+"')";            
        
        try {                        
            stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            int count = stmt_Write.executeUpdate(sqlStatement); 
            stmt_Write.close();
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{WriteToIqcDatabase.class.getName(), ex});
            log.error(ex);
        }
    }
    
    //Vullen collector_study_status 
    public static void UpdateStudyStatus(Connection dbConnection, String studyFk, String status){
        Statement stmt_Write;
        
        Timestamp creationTime = new Timestamp(System.currentTimeMillis());
        
        //Update tablename (colomns,..,) values ('','')        
        String sqlStatement = "UPDATE collector_study_status SET study_status='"+status+"' WHERE study_fk='"+studyFk+"'";            
        
        try {                        
            stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            int count = stmt_Write.executeUpdate(sqlStatement); 
            stmt_Write.close();
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{WriteToIqcDatabase.class.getName(), ex});
            log.error(ex);
        }        
    }
    
    //Vullen collector_series_status 
    public static void WriteSeriesStatus(Connection dbConnection, String seriesFk, String status){
        Statement stmt_Write;
        
        Timestamp creationTime = new Timestamp(System.currentTimeMillis());
        
        //INSERT INTO tablename (colomns,..,) values ('','')        
        String sqlStatement = "INSERT INTO collector_series_status (series_fk, series_status) values ('"+seriesFk+"','"+status+"')";            
        
        try {                        
            stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            int count = stmt_Write.executeUpdate(sqlStatement); 
            stmt_Write.close();
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{WriteToIqcDatabase.class.getName(), ex});
            log.error(ex);
        }        
    }
    
    //Update collector_series_status 
    public static void UpdateSeriesStatus(Connection dbConnection, String seriesFk, String status){
        Statement stmt_Write;        
        
        Timestamp creationTime = new Timestamp(System.currentTimeMillis());
        
        //Update tablename (colomns,..,) values ('','')        
        String sqlStatement = "UPDATE collector_series_status SET series_status='"+status+"' WHERE series_fk='"+seriesFk+"'";            
        
        try {                        
            stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            int count = stmt_Write.executeUpdate(sqlStatement);  
            stmt_Write.close();
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{WriteToIqcDatabase.class.getName(), ex});
            log.error(ex);
        }
    }
}
