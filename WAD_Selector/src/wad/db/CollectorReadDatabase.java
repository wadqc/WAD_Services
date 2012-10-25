/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ralph Berendsen <>
 */
public class CollectorReadDatabase {
    
    public static ArrayList<String> getSelectorStudyFkBySelectorSeriesFk(DatabaseParameters dbParam, String selectorSeriesFk){
        ArrayList<String> selectorPk = new ArrayList<String>();
        Connection dbConnection;
        ResultSet rs_selector;        
        Statement stmt_selector;        
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_selector = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_selector = stmt_selector.executeQuery("SELECT * FROM selector WHERE selector_series_fk='"+selectorSeriesFk+"'");            
            while (rs_selector.next()) {
                selectorPk.add(rs_selector.getString("selector_study_fk"));                                              
            }
            PacsDatabaseConnection.closeDb(dbConnection, stmt_selector, rs_selector);
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return selectorPk;
    }
    
    public static ArrayList<String> getSelectorSeriesFkBySelectorStudyFkFromSelector(DatabaseParameters dbParam, String selectorStudyFk){
        ArrayList<String> selectorPk = new ArrayList<String>();
        Connection dbConnection;
        ResultSet rs_selector;        
        Statement stmt_selector;        
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_selector = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_selector = stmt_selector.executeQuery("SELECT * FROM selector WHERE selector_study_fk='"+selectorStudyFk+"'");            
            while (rs_selector.next()) {
                selectorPk.add(rs_selector.getString("selector_series_fk"));                                              
            }
            PacsDatabaseConnection.closeDb(dbConnection, stmt_selector, rs_selector);
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return selectorPk;
    }
    
    public static ArrayList<String> getSelectorPkBySelectorStudyFkFromSelector(DatabaseParameters dbParam, String selectorStudyFk){
        ArrayList<String> selectorPk = new ArrayList<String>();
        Connection dbConnection;
        ResultSet rs_selector;        
        Statement stmt_selector;        
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_selector = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_selector = stmt_selector.executeQuery("SELECT * FROM selector WHERE selector_study_fk='"+selectorStudyFk+"'");            
            while (rs_selector.next()) {
                selectorPk.add(rs_selector.getString("pk"));                                              
            }
            PacsDatabaseConnection.closeDb(dbConnection, stmt_selector, rs_selector);
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return selectorPk;
    }
    
    public static ArrayList<String> getSelectorPkBySelectorSeriesFkFromSelector(DatabaseParameters dbParam, String selectorSeriesFk){
        ArrayList<String> selectorPk = new ArrayList<String>();
        Connection dbConnection;
        ResultSet rs_selector;        
        Statement stmt_selector;        
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_selector = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_selector = stmt_selector.executeQuery("SELECT * FROM selector WHERE selector_series_fk='"+selectorSeriesFk+"'");            
            while (rs_selector.next()) {
                selectorPk.add(rs_selector.getString("pk"));                                              
            }
            PacsDatabaseConnection.closeDb(dbConnection, stmt_selector, rs_selector);
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return selectorPk;
    }
    
    public static ArrayList<String> getInputfileFkBySelectorSeriesFkFromSelector(DatabaseParameters dbParam, String selectorSeriesFk){
        ArrayList<String> selectorPk = new ArrayList<String>();
        Connection dbConnection;
        ResultSet rs_selector;        
        Statement stmt_selector;        
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_selector = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_selector = stmt_selector.executeQuery("SELECT * FROM selector WHERE selector_series_fk='"+selectorSeriesFk+"'");            
            while (rs_selector.next()) {
                selectorPk.add(rs_selector.getString("analysemodule_fk"));                                              
            }
            PacsDatabaseConnection.closeDb(dbConnection, stmt_selector, rs_selector);
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return selectorPk;
    }
    
    public static ArrayList<String> getInputfileFkBySelectorStudyFkFromSelector(DatabaseParameters dbParam, String selectorStudyFk){
        ArrayList<String> selectorPk = new ArrayList<String>();
        Connection dbConnection;
        ResultSet rs_selector;        
        Statement stmt_selector;        
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_selector = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_selector = stmt_selector.executeQuery("SELECT * FROM selector WHERE selector_study_fk='"+selectorStudyFk+"'");            
            while (rs_selector.next()) {
                selectorPk.add(rs_selector.getString("analysemodule_fk"));                                              
            }
            PacsDatabaseConnection.closeDb(dbConnection, stmt_selector, rs_selector);
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return selectorPk;
    }
}
