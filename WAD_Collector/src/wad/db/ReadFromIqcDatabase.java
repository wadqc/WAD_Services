/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.db;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import wad.logger.LoggerWrapper;

/**
 *
 * @author Ralph Berendsen <>
 */
public class ReadFromIqcDatabase {
    
       
    //Read all studies from iqcDB (DCM4CHEE) and return arrayList with study_iuid from table STUDY
    public static ArrayList<String> getStudies(Connection dbConnection){
        ArrayList<String> studyList = new ArrayList<String>();
        ResultSet rs_study;        
        Statement stmt_study;
        
        try {
            stmt_study = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_study = stmt_study.executeQuery("SELECT * FROM study");
            while (rs_study.next()) {
                studyList.add(rs_study.getString("study_iuid"));
                //serie_fk = rs_patient.getString("pk");                
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReadFromIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
            LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
        }        
        return studyList;
    }
    
    public static Boolean seriesExistsBySeriesIUID(Connection dbConnection, String seriesIUID){
        ResultSet rs_series;        
        Statement stmt_series;        
        
        try {
            stmt_series = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_series = stmt_series.executeQuery("SELECT * FROM iqc.series WHERE series_iuid='"+seriesIUID+"'");            
            if (rs_series.next()) {
                String pk = rs_series.getString("pk");
                return true;                               
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReadFromIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
            LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
        }
        return false;
    }
    
    public static Boolean rowExistsByPrimaryKey(Connection dbConnection, String tableName, String pk){
        ResultSet rs_series;        
        Statement stmt_series;        
        
        try {
            stmt_series = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_series = stmt_series.executeQuery("SELECT * FROM "+tableName+" WHERE pk='"+pk+"'");            
            if (rs_series.next()) {
                return true;                               
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReadFromIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
            LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
        }
        return false;
    }
    
    public static String getCollectorStudyStatusByStudyFk(Connection dbConnection, String studyFk){
        ResultSet rs_studyStatus;        
        Statement stmt_studyStatus;        
        
        try {
            stmt_studyStatus = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String statement = "SELECT study_status FROM collector_study_status WHERE study_fk='"+studyFk+"'";
            rs_studyStatus = stmt_studyStatus.executeQuery(statement);            
            if (rs_studyStatus.next()) {
                String study_status = rs_studyStatus.getString("study_status");
                return study_status;                               
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReadFromIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
            LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
        }
        return null;
    }
    
    public static String getCollectorSeriesStatusBySeriesFk(Connection dbConnection, String seriesFk){
        ResultSet rs_seriesStatus;        
        Statement stmt_seriesStatus;        
        
        try {
            stmt_seriesStatus = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String statement = "SELECT series_status FROM collector_series_status WHERE series_fk='"+seriesFk+"'";
            rs_seriesStatus = stmt_seriesStatus.executeQuery(statement);            
            if (rs_seriesStatus.next()) {
                String study_status = rs_seriesStatus.getString("series_status");
                return study_status;                               
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReadFromIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
            LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
        }
        return null;
    }
    
    public static ArrayList<String> getColllectorStatusPkByStatus(Connection dbConnection, String tableName, String status){
        ArrayList<String> pkList = new ArrayList<String>();
        ResultSet rs_collectorStatus;        
        Statement stmt_collectorStatus; 
        String statement = "";
        
        try {
            stmt_collectorStatus = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (tableName.equals("collector_series_status")){
                statement = "SELECT pk FROM "+tableName+" WHERE series_status='"+status+"'";
            } else {
                statement = "SELECT pk FROM "+tableName+" WHERE study_status='"+status+"'";
            }
            rs_collectorStatus = stmt_collectorStatus.executeQuery(statement);            
            while (rs_collectorStatus.next()) {
                pkList.add(rs_collectorStatus.getString("pk"));
                //serie_fk = rs_patient.getString("pk");                
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReadFromIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
            LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
        }
        return pkList;
    }
    
    public static String getSeriesFkByInstanceFkFromInstance(Connection dbConnection, String instanceFk){
        ResultSet rs_instance;        
        Statement stmt_instance;        
        
        try {
            stmt_instance = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String statement = "SELECT series_fk FROM instance WHERE pk='"+instanceFk+"'";
            rs_instance = stmt_instance.executeQuery(statement);            
            if (rs_instance.next()) {
                String seriesFk = rs_instance.getString("series_fk");
                return seriesFk;                               
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReadFromIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
            LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
        }
        return null;
    }
    
    public static String getStudyFkBySeriesFkFromSeries(Connection dbConnection, String seriesFk){
        ResultSet rs_series;        
        Statement stmt_series;        
        
        try {
            stmt_series = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String statement = "SELECT study_fk FROM series WHERE pk='"+seriesFk+"'";
            rs_series = stmt_series.executeQuery(statement);            
            if (rs_series.next()) {
                String studyFk = rs_series.getString("study_fk");
                return studyFk;                               
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReadFromIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
            LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
        }
        return null;
    }
    
    public static String getPatientFkByStudyFkFromStudy(Connection dbConnection, String studyFk){
        ResultSet rs_study;        
        Statement stmt_study;        
        
        try {
            stmt_study = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String statement = "SELECT patient_fk FROM study WHERE pk='"+studyFk+"'";
            rs_study = stmt_study.executeQuery(statement);            
            if (rs_study.next()) {
                String patientFk = rs_study.getString("patient_fk");
                return patientFk;                               
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReadFromIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
            LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
        }
        return null;
    }
    
    //Read all series_pk from iqc.series where study_fk is given
    public static ArrayList<String> getSeriesPkListWithStudyFk(Connection dbConnection, String studyFk){
        ArrayList<String> seriesList = new ArrayList<String>();
        ResultSet rs_serie;        
        Statement stmt_serie;
        ResultSet rs_study;        
        Statement stmt_study;
        //String study_fk;
        //String series_IUID;
        
        try {
            stmt_serie = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_serie = stmt_serie.executeQuery("SELECT * FROM series WHERE study_fk='"+studyFk+"'");
            while (rs_serie.next()) {                    
                seriesList.add(rs_serie.getString("pk"));
            }
            rs_serie.close();               
            stmt_serie.close();
        } catch (SQLException ex) {
            Logger.getLogger(ReadFromIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
            LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
        }
        return seriesList;
    }
    
    //Read all series_pk from iqc.series where study_fk is given
    public static ArrayList<String> getInstancePkListWithSeriesFk(Connection dbConnection, String seriesFk){
        ArrayList<String> seriesList = new ArrayList<String>();
        ResultSet rs_instance;        
        Statement stmt_instance;
        ResultSet rs_series;        
        Statement stmt_series;
        //String study_fk;
        //String series_IUID;
        
        try {
            stmt_instance = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_instance = stmt_instance.executeQuery("SELECT * FROM instance WHERE series_fk='"+seriesFk+"'");
            while (rs_instance.next()) {                    
                seriesList.add(rs_instance.getString("pk"));
            }
            rs_instance.close();               
            stmt_instance.close();
        } catch (SQLException ex) {
            Logger.getLogger(ReadFromIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
            LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
        }
        return seriesList;
    }
    
    public static String getFilenameFromTable(Connection dbConnection, String tableName, String pk){
        ResultSet rs_file;        
        Statement stmt_file;        
        
        try {
            stmt_file = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String statement = "SELECT * FROM "+tableName+" WHERE pk='"+pk+"'";
            rs_file = stmt_file.executeQuery(statement);            
            if (rs_file.next()) {
                String fileName = rs_file.getString("filename");
                String filePath = rs_file.getString("filepath");
                return filePath+fileName;                               
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReadFromIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
            LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
        }
        return null;
    }
    
    public static ArrayList<String> getPkListFromTable(Connection dbConnection, String tableName){
        ArrayList<String> pkList = new ArrayList<String>();
        ResultSet rs_tableName;        
        Statement stmt_tableName;
                
        try {
            stmt_tableName = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_tableName = stmt_tableName.executeQuery("SELECT pk FROM "+tableName);
            while (rs_tableName.next()) {                    
                pkList.add(rs_tableName.getString("pk"));
            }
            rs_tableName.close();               
            stmt_tableName.close();
        } catch (SQLException ex) {
            Logger.getLogger(ReadFromIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
            LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
        }
        return pkList;
    }
    
    public static String getStudyStatusByStudyFk(Connection dbConnection, String studyFk){
        ResultSet rs_studyStatus;        
        Statement stmt_studyStatus;        
        
        try {
            stmt_studyStatus = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String statement = "SELECT study_status FROM study WHERE pk='"+studyFk+"'";
            rs_studyStatus = stmt_studyStatus.executeQuery(statement);            
            if (rs_studyStatus.next()) {
                String study_status = rs_studyStatus.getString("study_status");
                return study_status;                               
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReadFromIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
            LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
        }
        return null;
    }
    
    public static String getStudyStatusByStudyIUID(Connection dbConnection, String studyIUID){
        ResultSet rs_studyStatus;        
        Statement stmt_studyStatus;        
        
        try {
            stmt_studyStatus = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String statement = "SELECT study_status FROM study WHERE study_iuid='"+studyIUID+"'";
            rs_studyStatus = stmt_studyStatus.executeQuery(statement);            
            if (rs_studyStatus.next()) {
                String study_status = rs_studyStatus.getString("study_status");
                return study_status;                               
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReadFromIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
            LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
        }
        return null;
    }
    
    //Read filepath with given file_md5
    public static String getFilepathWithFilemd5(Connection dbConnection, String filemd5){
        String filepath = null;        
        ResultSet rs_files;        
        Statement stmt_files;        
        
        try {
            stmt_files = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_files = stmt_files.executeQuery("SELECT filepath FROM files WHERE file_md5='"+filemd5+"'");
            while (rs_files.next()) {                
                filepath = rs_files.getString("filepath");                              
            }
            rs_files.close();
            stmt_files.close();
        } catch (SQLException ex) {
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
            LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
        }
        return filepath;
    }
    
    public static ArrayList<String> getFilemd5ListFromFiles(Connection dbConnection){
        ArrayList<String> filedmd5List = new ArrayList<String>();        
        ResultSet rs_files;        
        Statement stmt_files;        
        
        try {
            stmt_files = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_files = stmt_files.executeQuery("SELECT file_md5 FROM files");
            while (rs_files.next()) {                
                filedmd5List.add(rs_files.getString("file_md5"));                              
            }
            rs_files.close();
            stmt_files.close();
        } catch (SQLException ex) {
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
            LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
        }
        return filedmd5List;
    }
    
    //Get the primary key of a table based on a unique identifier (by example sieres_iuid or study_iuid
    public static String getPkWithUniqueIdentifier(Connection dbConnection, String tableName, String uid_name, String uid_value){        
        ResultSet rs_serie;        
        Statement stmt_serie;        
        
        try {
            stmt_serie = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_serie = stmt_serie.executeQuery("SELECT pk FROM "+tableName+"  WHERE "+uid_name+"='"+uid_value+"'");
            if (rs_serie.next()) {
                String result = rs_serie.getString("pk"); 
                return result;
            }                      
        } catch (SQLException ex) {
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
            LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
        }        
        return "-1";
        
    }
    
}
