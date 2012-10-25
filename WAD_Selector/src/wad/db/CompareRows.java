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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ralph Berendsen <>
 */
public class CompareRows {
    
    //returns the pk of selector_series
    public static ArrayList<String> getMatchingSelectorSeriesPkList(DatabaseParameters dbParam, String seriesPk){
        ArrayList<String> seriesPkList = new ArrayList<String>();
        ArrayList<HashMap<String, String>> selectorSeriesList = getSelectorSeries(dbParam);
        HashMap<String, String> seriesMap = getSeriesByPk(dbParam, seriesPk);
        ArrayList<String> columnList = getColomnNamesFromTable(dbParam, "selector_series");
        for (int i=0;i<selectorSeriesList.size();i++){
            HashMap<String, String> selectorSeriesMap = selectorSeriesList.get(i);
            Boolean match = true;
            String selectorSeriesPk = selectorSeriesMap.get("pk");
            for (int j=0;j<columnList.size();j++){
                String columnLabel = columnList.get(j);
                String selectorSeriesValue = selectorSeriesMap.get(columnLabel);
                String seriesValue = seriesMap.get(columnLabel);
                if (!(selectorSeriesValue==null)){
                    if (!selectorSeriesValue.equals("null") && !selectorSeriesValue.equals("")){
                        if (!columnLabel.equals("availability") && !columnLabel.equals("series_status") && !columnLabel.equals("pk")){
                            if (!selectorSeriesValue.equals(seriesValue)){
                                match=false;
                            }
                        }
                    }
                }
            }
            if (match){
                seriesPkList.add(selectorSeriesPk);
            }
        }
        return seriesPkList;
    }
    
    public static ArrayList<HashMap<String, String>> getSelectorSeries(DatabaseParameters dbParam){
        ArrayList<HashMap<String, String>> seriesList = new ArrayList<HashMap<String, String>>();
        Connection dbConnection;
        ResultSet rs_series;        
        Statement stmt_series;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_series = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_series = stmt_series.executeQuery("SELECT * FROM selector_series");
            ResultSetMetaData md = rs_series.getMetaData();
            int columnCount = md.getColumnCount();
            while (rs_series.next()) {
                HashMap<String, String> seriesMap = new HashMap<String, String>();
                //start with 2 because first colom pk will not match
                for (int i=1;i<=columnCount;i++){                    
                    
                        seriesMap.put(md.getColumnLabel(i), rs_series.getString(i));
                    
                }
                seriesList.add(seriesMap);
                
                //studyList.add(rs_study.getString("study_iuid"));
                //serie_fk = rs_study.getString("pk");                
            }
            PacsDatabaseConnection.closeDb(dbConnection, stmt_series, rs_series);
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }        
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return seriesList;
    }
    
    public static ArrayList<String> getColomnNamesFromTable(DatabaseParameters dbParam, String tableName){
        ArrayList<String> columnList = new ArrayList<String>();
        Connection dbConnection;
        ResultSet rs_series;        
        Statement stmt_series;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_series = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_series = stmt_series.executeQuery("SELECT * FROM "+tableName);
            ResultSetMetaData md = rs_series.getMetaData();
            int columnCount = md.getColumnCount();
            for (int i=1;i<=columnCount;i++){
                    columnList.add(md.getColumnLabel(i));
            }
            PacsDatabaseConnection.closeDb(dbConnection, stmt_series, rs_series);
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }        
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return columnList;
    }
    
    public static HashMap<String, String> getSeriesByPk(DatabaseParameters dbParam, String pk){
        HashMap<String, String> seriesMap = new HashMap<String, String>();
        Connection dbConnection;
        ResultSet rs_series;        
        Statement stmt_series;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        
        try {
            stmt_series = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_series = stmt_series.executeQuery("SELECT * FROM series WHERE pk='"+pk+"'");
            ResultSetMetaData md = rs_series.getMetaData();
            int columnCount = md.getColumnCount();
            while (rs_series.next()) {
                for (int i=1;i<=columnCount;i++){
                    seriesMap.put(md.getColumnLabel(i), rs_series.getString(i));
                }                                               
            }            
            PacsDatabaseConnection.closeDb(dbConnection, stmt_series, rs_series);
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null); 
        return seriesMap;
    }
    
    //returns the pk of selector_study
    public static ArrayList<String> getMatchingSelectorStudyPkList(DatabaseParameters dbParam, String studyPk){
        ArrayList<String> studyPkList = new ArrayList<String>();
        ArrayList<HashMap<String, String>> selectorStudyList = getSelectorStudy(dbParam);
        HashMap<String, String> studyMap = getStudyByPk(dbParam, studyPk);
        ArrayList<String> columnList = getColomnNamesFromTable(dbParam, "selector_study");
        for (int i=0;i<selectorStudyList.size();i++){
            HashMap<String, String> selectorStudyMap = selectorStudyList.get(i);
            Boolean match = true;
            String selectorSeriesPk = selectorStudyMap.get("pk");
            for (int j=0;j<columnList.size();j++){
                String columnLabel = columnList.get(j);
                String selectorStudyValue = selectorStudyMap.get(columnLabel);
                String studyValue = studyMap.get(columnLabel);
                if (!(selectorStudyValue==null)){
                    if (!selectorStudyValue.equals("null") && !selectorStudyValue.equals("")){
                        if (!columnLabel.equals("availability") && !columnLabel.equals("study_status") && !columnLabel.equals("pk")){
                            if (!selectorStudyValue.equals(studyValue)){
                                match=false;
                            }
                        }
                    }
                }
            }
            if (match){
                studyPkList.add(selectorSeriesPk);
            }
        }
        return studyPkList;
    }
    
    public static ArrayList<HashMap<String, String>> getSelectorStudy(DatabaseParameters dbParam){
        ArrayList<HashMap<String, String>> studyList = new ArrayList<HashMap<String, String>>();
        Connection dbConnection;
        ResultSet rs_study;        
        Statement stmt_study;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_study = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_study = stmt_study.executeQuery("SELECT * FROM selector_study");
            ResultSetMetaData md = rs_study.getMetaData();
            int columnCount = md.getColumnCount();
            while (rs_study.next()) {
                HashMap<String, String> seriesMap = new HashMap<String, String>();
                //start with 2 because first colom pk will not match
                for (int i=1;i<=columnCount;i++){                    
                    
                        seriesMap.put(md.getColumnLabel(i), rs_study.getString(i));
                    
                }
                studyList.add(seriesMap);
                
                //studyList.add(rs_study.getString("study_iuid"));
                //serie_fk = rs_study.getString("pk");                
            }
            PacsDatabaseConnection.closeDb(dbConnection, stmt_study, rs_study);
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }        
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return studyList;
    }
    
    public static HashMap<String, String> getStudyByPk(DatabaseParameters dbParam, String pk){
        HashMap<String, String> studyMap = new HashMap<String, String>();
        Connection dbConnection;
        ResultSet rs_study;        
        Statement stmt_study;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        
        try {
            stmt_study = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_study = stmt_study.executeQuery("SELECT * FROM study WHERE pk='"+pk+"'");
            ResultSetMetaData md = rs_study.getMetaData();
            int columnCount = md.getColumnCount();
            while (rs_study.next()) {
                for (int i=1;i<=columnCount;i++){
                    studyMap.put(md.getColumnLabel(i), rs_study.getString(i));
                }                                               
            }            
            PacsDatabaseConnection.closeDb(dbConnection, stmt_study, rs_study);
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null); 
        return studyMap;
    }
    
    public static HashMap<String, String> getSelectorStudyByPk(DatabaseParameters dbParam, String pk){
        HashMap<String, String> studyMap = new HashMap<String, String>();
        Connection dbConnection;
        ResultSet rs_study;        
        Statement stmt_study;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        
        try {
            stmt_study = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_study = stmt_study.executeQuery("SELECT * FROM selector_study WHERE pk='"+pk+"'");
            ResultSetMetaData md = rs_study.getMetaData();
            int columnCount = md.getColumnCount();
            while (rs_study.next()) {
                for (int i=1;i<=columnCount;i++){
                    studyMap.put(md.getColumnLabel(i), rs_study.getString(i));
                }                                               
            }            
            PacsDatabaseConnection.closeDb(dbConnection, stmt_study, rs_study);
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null); 
        return studyMap;
    }
    
    //returns the pk of selector_study
    public static Boolean isMatch(DatabaseParameters dbParam, String studyPk, String selStudyPk){
        //ArrayList<String> studyPkList = new ArrayList<String>();
        if (studyPk==null || selStudyPk==null){
            return false;
        }
        HashMap<String, String> selectorStudyMap = getSelectorStudyByPk(dbParam, selStudyPk);
        HashMap<String, String> studyMap = getStudyByPk(dbParam, studyPk);
        ArrayList<String> columnList = getColomnNamesFromTable(dbParam, "selector_study");
        Boolean match = true;
        //String selectorSeriesPk = selectorStudyMap.get("pk");
        for (int j=0;j<columnList.size();j++){
            String columnLabel = columnList.get(j);
            String selectorStudyValue = selectorStudyMap.get(columnLabel);
            String studyValue = studyMap.get(columnLabel);
            if (!(selectorStudyValue==null)){
                if (!selectorStudyValue.equals("null") && !selectorStudyValue.equals("")){
                    if (!columnLabel.equals("availability") && !columnLabel.equals("study_status") && !columnLabel.equals("pk")){
                        if (!selectorStudyValue.equals(studyValue)){
                            match=false;
                        }
                    }
                }
            }
        }
        return match;
    }
}
