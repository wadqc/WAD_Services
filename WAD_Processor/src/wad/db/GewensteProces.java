/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.db;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import wad.logger.LoggerWrapper;

/**
 *
 * @author Ralph Berendsen <>
 */
public class GewensteProces {
    private int key=-1;
    private int selectorKey=-1;
    private int seriesKey=-1;
    private int studyKey=-1;
    private int instanceKey=-1;
    private int status=-1;
    private int inputKey=-1;
    private int outputKey=-1;    
        
    public Boolean getFirstGewensteProcesByStatus(Connection dbConnection, int status){
        ResultSet rs_gp;        
        Statement stmt_gp;
        
        try {
            stmt_gp = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_gp = stmt_gp.executeQuery("SELECT * FROM gewenste_processen WHERE status='"+status+"'");
            if (rs_gp.next()) {
                this.key = replaceNull(rs_gp.getInt("pk"));
                this.selectorKey = replaceNull(rs_gp.getInt("selector_fk"));
                this.seriesKey = replaceNull(rs_gp.getInt("series_fk"));
                this.studyKey = replaceNull(rs_gp.getInt("study_fk"));
                this.instanceKey = replaceNull(rs_gp.getInt("instance_fk"));
                this.status = rs_gp.getInt("status");
                this.inputKey = replaceNull(rs_gp.getInt("analysemodule_input_fk"));
                this.outputKey = replaceNull(rs_gp.getInt("analysemodule_output_fk"));
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            LoggerWrapper.myLogger.log(Level.WARNING, "{0} {1}", new Object[]{GewensteProces.class.getName(), ex});
            return false;
        }
    }
    
    public Boolean getGewensteProcesByKey(Connection dbConnection, String pk){
        ResultSet rs_gp;        
        Statement stmt_gp;
        
        try {
            stmt_gp = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_gp = stmt_gp.executeQuery("SELECT * FROM gewenste_processen WHERE pk='"+pk+"'");
            if (rs_gp.next()) {
                this.key = replaceNull(rs_gp.getInt("pk"));
                this.selectorKey = replaceNull(rs_gp.getInt("selector_fk"));
                this.seriesKey = replaceNull(rs_gp.getInt("series_fk"));
                this.studyKey = replaceNull(rs_gp.getInt("study_fk"));
                this.instanceKey = replaceNull(rs_gp.getInt("instance_fk"));
                this.status = rs_gp.getInt("status");
                this.inputKey = replaceNull(rs_gp.getInt("analysemodule_input_fk"));
                this.outputKey = replaceNull(rs_gp.getInt("analysemodule_output_fk"));
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            LoggerWrapper.myLogger.log(Level.WARNING, "{0} {1}", new Object[]{GewensteProces.class.getName(), ex});
            return false;
        }
    }
    
    private int replaceNull(int value){
        if (value==0){
            return -1;
        }
        return value;
    }
    
    public void updateStatus(Connection dbConnection, int status){
        this.status=status;
        
        Statement stmt_Write;
        
        Timestamp updateTime = new Timestamp(System.currentTimeMillis());
        
        //Update tablename (colomns,..,) values ('','')        
        String sqlStatement = "UPDATE gewenste_processen SET status='"+status+"' WHERE pk='"+this.key+"'";            
        
        try {                        
            stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            int count = stmt_Write.executeUpdate(sqlStatement); 
            stmt_Write.close();
        } catch (SQLException ex) {
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{GewensteProces.class.getName(), ex});
        }
    }
    
    public String getKey(){
        return Integer.toString(this.key);
    }
    
    public String getOutputKey(){
        return Integer.toString(this.outputKey);
    }
    
    public String getInputKey(){
        return Integer.toString(this.inputKey);
    }
    
    public String getSelectorKey(){
        return Integer.toString(this.selectorKey);
    }
    
    
    
    
}
