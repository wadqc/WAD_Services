/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package selector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import wad.db.DatabaseParameters;
import wad.db.PacsDatabaseConnection;
import wad.db.ReadFromIqcDatabase;
import wad.xml.XmlInputFile;

/**
 *
 * @author Ralph Berendsen <>
 */
public class SelectorRow {
    private String inputfileFk;
    private String pk;
    private String selectorPatientFk;
    private String selectorStudyFk;
    private String selectorSeriesFk;
    private String selectorInstanceFk;
    private String analyseLvl;
    
    public SelectorRow(DatabaseParameters dbParam, String pk){
        
        ArrayList<String> selectorPk = new ArrayList<String>();
        Connection dbConnection;
        ResultSet rs_selector;        
        Statement stmt_selector;        
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_selector = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_selector = stmt_selector.executeQuery("SELECT * FROM selector WHERE pk='"+pk+"'");            
            while (rs_selector.next()) {
                this.pk = pk;
                this.inputfileFk = rs_selector.getString("analysemodule_fk");
                this.selectorPatientFk = rs_selector.getString("selector_patient_fk");
                this.selectorStudyFk = rs_selector.getString("selector_study_fk");
                this.selectorSeriesFk = rs_selector.getString("selector_series_fk");
                this.selectorInstanceFk = rs_selector.getString("selector_instance_fk");
                
                XmlInputFile xmlinputFile = new XmlInputFile();
                String inputFileStr = ReadFromIqcDatabase.getFilenameFromTable(dbParam, "inputfile", this.inputfileFk);
                xmlinputFile.read(inputFileStr);  
                
                this.analyseLvl = xmlinputFile.getAnalyseLevel();  
            }
            PacsDatabaseConnection.closeDb(dbConnection, stmt_selector, rs_selector);
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);            
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);        
    }
    
    public String getInputFileFk(){
        return this.inputfileFk;
    }
    
    public String getSelectorPatientFk(){
        return this.selectorPatientFk;
    }
    
    public String getSelectorStudyFk(){
        return this.selectorStudyFk;
    }
    
    public String getSelectorSeriesFk(){
        return this.selectorSeriesFk;
    }
    
    public String getSelectorInstanceFk(){
        return this.selectorInstanceFk;
    }
    
    public String getAnalyseLevel(){
        return this.analyseLvl;
    }
    
    public String getPk(){
        return this.pk;
    }
    
    
}
