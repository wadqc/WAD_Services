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

/**
 *
 * @author Ralph Berendsen <>
 */
public class ReadFromIqcDatabase {
    
       
    //Read all studies from iqcDB (DCM4CHEE) and return arrayList with study_iuid from table STUDY
    public static ArrayList<String> getStudies(DatabaseParameters dbParam){
        ArrayList<String> studyList = new ArrayList<String>();
        Connection dbConnection;
        ResultSet rs_study;        
        Statement stmt_study;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_study = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_study = stmt_study.executeQuery("SELECT * FROM study");
            while (rs_study.next()) {
                studyList.add(rs_study.getString("study_iuid"));
                //serie_fk = rs_patient.getString("pk");                
            }
            PacsDatabaseConnection.closeDb(dbConnection, stmt_study, rs_study);
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return studyList;
    }
    
    public static Boolean seriesExistsBySeriesIUID(DatabaseParameters dbParam, String seriesIUID){
        Connection dbConnection;
        ResultSet rs_series;        
        Statement stmt_series;        
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_series = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_series = stmt_series.executeQuery("SELECT * FROM iqc.series WHERE series_iuid='"+seriesIUID+"'");            
            if (rs_series.next()) {
                String pk = rs_series.getString("pk");
                PacsDatabaseConnection.closeDb(dbConnection, stmt_series, rs_series);
                return true;                               
            } else {
                PacsDatabaseConnection.closeDb(dbConnection, stmt_series, rs_series);
                return false;
            }
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        
        return false;
    }
    
    public static Boolean rowExistsByPrimaryKey(DatabaseParameters dbParam, String tableName, String pk){
        Connection dbConnection;
        ResultSet rs_series;        
        Statement stmt_series;        
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_series = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_series = stmt_series.executeQuery("SELECT * FROM "+tableName+" WHERE pk='"+pk+"'");            
            if (rs_series.next()) {
                PacsDatabaseConnection.closeDb(dbConnection, stmt_series, rs_series);
                return true;                               
            } else {
                PacsDatabaseConnection.closeDb(dbConnection, stmt_series, rs_series);
                return false;
            }
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return false;
    }
    
    public static String getCollectorStudyStatusByStudyFk(DatabaseParameters dbParam, String studyFk){
        Connection dbConnection;
        ResultSet rs_studyStatus;        
        Statement stmt_studyStatus;        
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_studyStatus = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String statement = "SELECT study_status FROM collector_study_status WHERE study_fk='"+studyFk+"'";
            rs_studyStatus = stmt_studyStatus.executeQuery(statement);            
            if (rs_studyStatus.next()) {
                String study_status = rs_studyStatus.getString("study_status");
                PacsDatabaseConnection.closeDb(dbConnection, stmt_studyStatus, rs_studyStatus);
                return study_status;                               
            } else {
                PacsDatabaseConnection.closeDb(dbConnection, stmt_studyStatus, rs_studyStatus);
                return null;
            }
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return null;
    }
    
    public static String getCollectorSeriesStatusBySeriesFk(DatabaseParameters dbParam, String seriesFk){
        Connection dbConnection;
        ResultSet rs_seriesStatus;        
        Statement stmt_seriesStatus;        
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_seriesStatus = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String statement = "SELECT series_status FROM collector_series_status WHERE series_fk='"+seriesFk+"'";
            rs_seriesStatus = stmt_seriesStatus.executeQuery(statement);            
            if (rs_seriesStatus.next()) {
                String study_status = rs_seriesStatus.getString("series_status");
                PacsDatabaseConnection.closeDb(dbConnection, stmt_seriesStatus, rs_seriesStatus);
                return study_status;                               
            } else {
                PacsDatabaseConnection.closeDb(dbConnection, stmt_seriesStatus, rs_seriesStatus);
                return null;
            }
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return null;
    }
    
    public static ArrayList<String> getColllectorStatusPkByStatus(DatabaseParameters dbParam, String tableName, String status){
        ArrayList<String> pkList = new ArrayList<String>();
        Connection dbConnection;
        ResultSet rs_collectorStatus;        
        Statement stmt_collectorStatus; 
        String statement = "";
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
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
            PacsDatabaseConnection.closeDb(dbConnection, stmt_collectorStatus, rs_collectorStatus);
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return pkList;
    }
    
    public static String getStudyFkBySeriesFkFromSeries(DatabaseParameters dbParam, String seriesFk){
        Connection dbConnection;
        ResultSet rs_series;        
        Statement stmt_series;        
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_series = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String statement = "SELECT study_fk FROM series WHERE pk='"+seriesFk+"'";
            rs_series = stmt_series.executeQuery(statement);            
            if (rs_series.next()) {
                String studyFk = rs_series.getString("study_fk");
                PacsDatabaseConnection.closeDb(dbConnection, stmt_series, rs_series);
                return studyFk;                               
            } else {
                PacsDatabaseConnection.closeDb(dbConnection, stmt_series, rs_series);
                return null;
            }
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return null;
    }
    
    public static String getPatientFkByStudyFkFromStudy(DatabaseParameters dbParam, String studyFk){
        Connection dbConnection;
        ResultSet rs_study;        
        Statement stmt_study;        
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_study = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String statement = "SELECT patient_fk FROM study WHERE pk='"+studyFk+"'";
            rs_study = stmt_study.executeQuery(statement);            
            if (rs_study.next()) {
                String patientFk = rs_study.getString("patient_fk");
                PacsDatabaseConnection.closeDb(dbConnection, stmt_study, rs_study);
                return patientFk;                               
            } else {
                PacsDatabaseConnection.closeDb(dbConnection, stmt_study, rs_study);
                return null;
            }
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return null;
    }
    
    //Read all series_pk from iqc.series where study_fk is given
    public static ArrayList<String> getSeriesPkListWithStudyFk(DatabaseParameters dbParam, String studyFk){
        ArrayList<String> seriesList = new ArrayList<String>();
        Connection dbConnection;
        ResultSet rs_serie;        
        Statement stmt_serie;
        ResultSet rs_study;        
        Statement stmt_study;
        //String study_fk;
        //String series_IUID;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        
        try {
            stmt_serie = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_serie = stmt_serie.executeQuery("SELECT * FROM series WHERE study_fk='"+studyFk+"'");
            while (rs_serie.next()) {                    
                seriesList.add(rs_serie.getString("pk"));
            }
            rs_serie.close();               
            stmt_serie.close();
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return seriesList;
    }
    
    //Read all series_pk from iqc.series where study_fk is given
    public static ArrayList<String> getInstancePkListWithSeriesFk(DatabaseParameters dbParam, String seriesFk){
        ArrayList<String> seriesList = new ArrayList<String>();
        Connection dbConnection;
        ResultSet rs_instance;        
        Statement stmt_instance;
        ResultSet rs_series;        
        Statement stmt_series;
        //String study_fk;
        //String series_IUID;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        
        try {
            stmt_instance = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_instance = stmt_instance.executeQuery("SELECT * FROM instance WHERE series_fk='"+seriesFk+"'");
            while (rs_instance.next()) {                    
                seriesList.add(rs_instance.getString("pk"));
            }
            rs_instance.close();               
            stmt_instance.close();
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return seriesList;
    }
    
    public static String getFilenameFromTable(DatabaseParameters dbParam, String tableName, String pk){
        Connection dbConnection;
        ResultSet rs_file;        
        Statement stmt_file;        
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_file = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String statement = "SELECT * FROM "+tableName+" WHERE pk='"+pk+"'";
            rs_file = stmt_file.executeQuery(statement);            
            if (rs_file.next()) {
                String fileName = rs_file.getString("filename");
                String filePath = rs_file.getString("filepath");
                PacsDatabaseConnection.closeDb(dbConnection, stmt_file, rs_file);
                return filePath+fileName;                               
            } else {
                PacsDatabaseConnection.closeDb(dbConnection, stmt_file, rs_file);
                return null;
            }
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return null;
    }
    
    public static ArrayList<String> getPkListFromTable(DatabaseParameters dbParam, String tableName){
        ArrayList<String> pkList = new ArrayList<String>();
        Connection dbConnection;
        ResultSet rs_tableName;        
        Statement stmt_tableName;
                
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        
        try {
            stmt_tableName = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_tableName = stmt_tableName.executeQuery("SELECT pk FROM "+tableName);
            while (rs_tableName.next()) {                    
                pkList.add(rs_tableName.getString("pk"));
            }
            rs_tableName.close();               
            stmt_tableName.close();
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return pkList;
    }
    
    public static String getStudyStatusByStudyFk(DatabaseParameters dbParam, String studyFk){
        Connection dbConnection;
        ResultSet rs_studyStatus;        
        Statement stmt_studyStatus;        
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_studyStatus = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String statement = "SELECT study_status FROM study WHERE pk='"+studyFk+"'";
            rs_studyStatus = stmt_studyStatus.executeQuery(statement);            
            if (rs_studyStatus.next()) {
                String study_status = rs_studyStatus.getString("study_status");
                PacsDatabaseConnection.closeDb(dbConnection, stmt_studyStatus, rs_studyStatus);
                return study_status;                               
            } else {
                PacsDatabaseConnection.closeDb(dbConnection, stmt_studyStatus, rs_studyStatus);
                return null;
            }
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return null;
    }
    
}
