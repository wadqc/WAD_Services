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
import wad.xml.ReadConfigXML;

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
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
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
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
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
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
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
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
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
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
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
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
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
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
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
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
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
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
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
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
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
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
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
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
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
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
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
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
        }
        return null;
    }
    
    public static String getAnalyseModuleBySelectorFk(Connection dbConnection, String selectorFk){
        ResultSet rs_selector;        
        Statement stmt_selector; 
        ResultSet rs_analyseModule;        
        Statement stmt_analyseModule;
        
        try {
            stmt_selector = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String statement = "SELECT analysemodule_fk FROM selector WHERE pk='"+selectorFk+"'";
            rs_selector = stmt_selector.executeQuery(statement);            
            if (rs_selector.next()) {
                String analysemoduleFk = rs_selector.getString("analysemodule_fk");                
                stmt_analyseModule = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                statement = "SELECT * FROM analysemodule WHERE pk='"+analysemoduleFk+"'";
                rs_analyseModule = stmt_selector.executeQuery(statement);            
                if (rs_analyseModule.next()) {
                    String filename = rs_analyseModule.getString("filename");
                    String filepath = rs_analyseModule.getString("filepath");                    
                    String uploadPath = ReadConfigXML.readFileElement("uploads");
                    return (uploadPath+filepath+filename).replace("/","\\");
                }
                stmt_analyseModule.close();
                rs_analyseModule.close();
            }
        } catch (SQLException ex) {
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
        }
        return null;
    }
    
    public static String getAnalyseModuleCfgBySelectorFk(Connection dbConnection, String selectorFk){
        ResultSet rs_selector;        
        Statement stmt_selector;
        ResultSet rs_analyseModuleCfg;        
        Statement stmt_analyseModuleCfg;
        
        try {
            stmt_selector = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_selector = stmt_selector.executeQuery("SELECT * FROM selector WHERE pk='"+selectorFk+"'");
            if (rs_selector.next()) {                
                String analyseModuleCfg_fk=rs_selector.getString("analysemodule_cfg_fk");
                stmt_analyseModuleCfg = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rs_analyseModuleCfg = stmt_analyseModuleCfg.executeQuery("SELECT * FROM analysemodule_cfg WHERE pk='"+analyseModuleCfg_fk+"'");
                if (rs_analyseModuleCfg.next()) {                    
                    String filepath=rs_analyseModuleCfg.getString("filepath");
                    String filename=rs_analyseModuleCfg.getString("filename");                    
                    String uploadPath = ReadConfigXML.readFileElement("uploads");
                    return (uploadPath+filepath+filename).replace("/","\\");
                }
                stmt_analyseModuleCfg.close();
                rs_analyseModuleCfg.close();
            }
        } catch (SQLException ex) {
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
        }
        return null;
    }
    
}
