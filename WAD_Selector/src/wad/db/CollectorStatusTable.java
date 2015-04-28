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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 *
 * @author Ralph Berendsen 
 */
public class CollectorStatusTable {
    private ArrayList<CollectorStatusRow> colStatusTable = new ArrayList<CollectorStatusRow>();
    
    private static Log log = LogFactory.getLog(CollectorStatusTable.class);
    
    public CollectorStatusTable(Connection dbConnection, String tableName){        
        ResultSet rs_collectorStatus;        
        Statement stmt_collectorStatus; 
        String statement = "";
        
        //dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_collectorStatus = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statement = "SELECT * FROM "+tableName;            
            rs_collectorStatus = stmt_collectorStatus.executeQuery(statement);            
            while (rs_collectorStatus.next()) {
                this.colStatusTable.add(new CollectorStatusRow(rs_collectorStatus.getString(1), rs_collectorStatus.getString(2), rs_collectorStatus.getString(3)));
                //serie_fk = rs_study.getString("pk");                
            }
            //PacsDatabaseConnection.closeDb(dbConnection, stmt_collectorStatus, rs_collectorStatus);
        } catch (SQLException ex) {
            //PacsDatabaseConnection.closeDb(dbConnection, null, null);            
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{CollectorStatusTable.class.getName(), ex});
            log.error(ex);
        }
        //PacsDatabaseConnection.closeDb(dbConnection, null, null);
        
    }
    
    public int size(){
        return this.colStatusTable.size();
    }
    
    public CollectorStatusRow getColllectorStatusRow(int i){
        return this.colStatusTable.get(i);
    }
    
    public CollectorStatusRow removeColllectorStatusRow(int i){
        return this.colStatusTable.remove(i);
    }
    
    public String getStatusWithFk(String foreignKey){
        for (int i=0;i<this.colStatusTable.size();i++){
            if (this.colStatusTable.get(i).getFk().equals(foreignKey)){
                return this.colStatusTable.get(i).getStatus();
            } 
        }
        return "-1";
    }
    
    public CollectorStatusRow getRowWithFk(String foreignKey){
        CollectorStatusRow colStatRow = new CollectorStatusRow();
        for (int i=0;i<this.colStatusTable.size();i++){
            if (this.colStatusTable.get(i).getFk().equals(foreignKey)){
                return this.colStatusTable.get(i);
            } 
        }
        return colStatRow;
    }
    
    public int getIndexWithFk(String foreignKey){
        for (int i=0;i<this.colStatusTable.size();i++){
            if (this.colStatusTable.get(i).getFk().equals(foreignKey)){
                return i;
            } 
        }
        return -1;
    }
    
    
}
