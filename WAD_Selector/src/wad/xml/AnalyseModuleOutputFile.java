/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.xml;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import wad.logger.LoggerWrapper;

/**
 *
 * @author Gebruiker
 */
public class AnalyseModuleOutputFile {
    String selectorPk;
    String analyseModule;
    String absoluteFilename;
    String filePath;
    String fileName;
    
    public AnalyseModuleOutputFile(Connection dbConnection, String SelectorPk, String createDateTime){
        this.selectorPk = SelectorPk;
        getModuleName(dbConnection);
        this.fileName = this.analyseModule+"/"+createDateTime+"/result.xml";
        String currentDir = System.getProperty("user.dir");
        File dir = new File(currentDir);
        String mainDir = dir.getParent();
        this.filePath = ReadConfigXML.readFileElement("analysemodule_output");                
                
        this.absoluteFilename = this.filePath.replace("..",mainDir)+ this.fileName;
        this.absoluteFilename = this.absoluteFilename.replace("/","\\");
        File file = new File(this.absoluteFilename);
        boolean created = false;
        try {
            file.getParentFile().mkdirs();
            created = file.createNewFile();
        } catch (IOException ioe){
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1} {2}", new Object[]{AnalyseModuleOutputFile.class.getName(), ioe, "Error while creating new file"});
        }
        
    }
    
    private void getModuleName(Connection dbConnection){
        ResultSet rs_selector;        
        Statement stmt_selector;
        ResultSet rs_analyseModule;        
        Statement stmt_analyseModule;
        
        try {
            stmt_selector = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_selector = stmt_selector.executeQuery("SELECT * FROM selector WHERE pk='"+selectorPk+"'");
            while (rs_selector.next()) {                
                String analyseModule_fk=rs_selector.getString("analysemodule_fk");
                stmt_analyseModule = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rs_analyseModule = stmt_analyseModule.executeQuery("SELECT * FROM analysemodule WHERE pk='"+analyseModule_fk+"'");
                while (rs_analyseModule.next()) {                    
                    this.analyseModule=rs_analyseModule.getString("description");
                    if (this.analyseModule==null){
                        this.analyseModule="naamloos";
                    }
                }
                stmt_analyseModule.close();
                rs_analyseModule.close();
            }
        } catch (SQLException ex) {
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{AnalyseModuleOutputFile.class.getName(), ex});            
        }
    }
    
    public String getFilename(){
        return this.fileName;
    }
    
    public String getFilepath(){
        return this.filePath;
    }
    
    public String getAbsoluteFilename(){
        return this.absoluteFilename;
    }
    
}
