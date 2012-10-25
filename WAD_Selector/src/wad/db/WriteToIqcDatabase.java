/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.db;

import java.sql.ResultSetMetaData;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ralph Berendsen <>
 */
public class WriteToIqcDatabase {
    
    public static void WriteResulsetInTable(DatabaseParameters dbParam, ResultSet rsRead, String tableName){
        Connection dbConnection;                
        Statement stmt_Write;
        ResultSet rs_Write;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        
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
                    //String value = rsRead.getString(colomnLabel);
                    Object value = rsRead.getObject(colomnLabel);
                    if (value==null){
                        sqlValues = sqlValues+rsRead.getString(colomnLabel);
                    } else {
                        //sqlValues = sqlValues+"'"+rsRead.getString(colomnLabel)+"'";
                        if (value instanceof Integer){
                            sqlValues = sqlValues+value;
                        } else if (value instanceof Boolean){
                            sqlValues = sqlValues+value;
                        } else if (value instanceof String){
                            String strValue = (String) value;
                            strValue = strValue.replace("\\", "\\\\");
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
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(WriteToIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
    }
    
    //Vullen collector_study_status 
    public static void WriteStudyStatus(DatabaseParameters dbParam, String studyFk, String status){
        Connection dbConnection;                
        Statement stmt_Write;        
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        Timestamp creationTime = new Timestamp(System.currentTimeMillis());
        
        //INSERT INTO tablename (colomns,..,) values ('','')        
        String sqlStatement = "INSERT INTO collector_study_status (study_fk, study_status) values ('"+studyFk+"','"+status+"')";            
        
        try {                        
            stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            int count = stmt_Write.executeUpdate(sqlStatement); 
            stmt_Write.close();
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(WriteToIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
    }
    
    //Vullen collector_study_status 
    public static void UpdateStudyStatus(DatabaseParameters dbParam, String studyFk, String status){
        Connection dbConnection;                
        Statement stmt_Write;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        Timestamp creationTime = new Timestamp(System.currentTimeMillis());
        
        //Update tablename (colomns,..,) values ('','')        
        String sqlStatement = "UPDATE collector_study_status SET study_status='"+status+"' WHERE study_fk='"+studyFk+"'";            
        
        try {                        
            stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            int count = stmt_Write.executeUpdate(sqlStatement); 
            stmt_Write.close();
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(WriteToIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
    }
    
    //Vullen collector_series_status 
    public static void WriteSeriesStatus(DatabaseParameters dbParam, String seriesFk, String status){
        Connection dbConnection;                
        Statement stmt_Write;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        Timestamp creationTime = new Timestamp(System.currentTimeMillis());
        
        //INSERT INTO tablename (colomns,..,) values ('','')        
        String sqlStatement = "INSERT INTO collector_series_status (series_fk, series_status) values ('"+seriesFk+"','"+status+"')";            
        
        try {                        
            stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            int count = stmt_Write.executeUpdate(sqlStatement); 
            stmt_Write.close();
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(WriteToIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
    }
    
    //Update collector_series_status 
    public static void UpdateSeriesStatus(DatabaseParameters dbParam, String seriesFk, String status){
        Connection dbConnection;                
        Statement stmt_Write;
        ResultSet rs_Write;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        Timestamp creationTime = new Timestamp(System.currentTimeMillis());
        
        //Update tablename (colomns,..,) values ('','')        
        String sqlStatement = "UPDATE collector_series_status SET series_status='"+status+"' WHERE series_fk='"+seriesFk+"'";            
        
        try {                        
            stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            int count = stmt_Write.executeUpdate(sqlStatement);  
            stmt_Write.close();
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(WriteToIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
    }
}
