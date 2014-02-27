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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Gebruiker
 */
public class AnalyseModuleOutputFile {
    String selectorPk;
	String selectorName;
    //String analyseModule;
    String absoluteFilename;
    String filePath;
    String fileName;
    
    private static Log log = LogFactory.getLog(AnalyseModuleOutputFile.class);
    
    public AnalyseModuleOutputFile(Connection dbConnection, String SelectorPk, String createDateTime){
        this.selectorPk = SelectorPk;
        getModuleName(dbConnection);
        this.fileName = this.selectorName+"/"+createDateTime+"/result.xml";
        //aanpassen bij absoluut filepath voor XML in config.xml
        String currentDir = System.getProperty("user.dir");
        File dir = new File(currentDir);
        String mainDir = dir.getParent();
//        this.filePath = ReadConfigXML.readFileElement("analysemodule_output");                
        this.filePath = "XML/analysemodule_output/";
        
        //this.absoluteFilename = this.filePath.replace("..",mainDir)+ this.fileName;
        this.absoluteFilename = ReadConfigXML.readFileElement("XML")+this.filePath+ this.fileName;
        this.absoluteFilename = this.absoluteFilename.replace("/",File.separator);
        File file = new File(this.absoluteFilename);
        boolean created = false;
        try {
            file.getParentFile().mkdirs();
            created = file.createNewFile();
        } catch (IOException ioe){
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1} {2}", new Object[]{AnalyseModuleOutputFile.class.getName(), ioe, "Error while creating new file"});
            log.error(ioe);
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
				this.selectorName=rs_selector.getString("name");
                /* 
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
				*/
            }
			rs_selector.close();
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{AnalyseModuleOutputFile.class.getName(), ex});            
            log.error(ex);
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
