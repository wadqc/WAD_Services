/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.xml;

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
public class AnalyseModuleInputValues {
    private String version;
    private String analyseModuleCfg;
    private String analyseModuleOutput;
    private String analyselevel;
    private String analyseModuleOutputFilename;
    private String analyseModuleOutputFilepath;
    
    private static Log log = LogFactory.getLog(AnalyseModuleInputValues.class);
    
    public AnalyseModuleInputValues(Connection dbConnection, String selectorPk, String createDateTime){
        version = "";
        analyseModuleCfg = "";
        analyseModuleOutput = "";
        analyselevel = "";
        setCfgFile(dbConnection, selectorPk);
        this.analyseModuleCfg = ReadConfigXML.readFileElement("uploads")+this.analyseModuleCfg; 
        this.analyseModuleCfg = this.analyseModuleCfg.replace("/","\\");
    }
    
    private void setCfgFile(Connection dbConnection, String selectorPk){
        ResultSet rs_selector;        
        Statement stmt_selector;
        ResultSet rs_analyseModuleCfg;        
        Statement stmt_analyseModuleCfg;
        
        try {
            stmt_selector = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_selector = stmt_selector.executeQuery("SELECT * FROM selector WHERE pk='"+selectorPk+"'");
            while (rs_selector.next()) {
                this.analyselevel=rs_selector.getString("analyselevel");
                String analyseModuleCfg_fk=rs_selector.getString("analysemodule_cfg_fk");
                stmt_analyseModuleCfg = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rs_analyseModuleCfg = stmt_analyseModuleCfg.executeQuery("SELECT * FROM analysemodule_cfg WHERE pk='"+analyseModuleCfg_fk+"'");
                while (rs_analyseModuleCfg.next()) {                    
                    String filepath=rs_analyseModuleCfg.getString("filepath");
                    String filename=rs_analyseModuleCfg.getString("filename");
                    this.analyseModuleCfg=filepath+filename;
                }
                stmt_analyseModuleCfg.close();
                rs_analyseModuleCfg.close();
            }
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{AnalyseModuleInputValues.class.getName(), ex});
            log.error(ex);
        }
        
    }   
    
    
    public String getVersion(){
        return version;
    }
    
    public String getModuleConfig(){
        return analyseModuleCfg;
    }
    
    public String getModuleOutput(){
        return analyseModuleOutput;
    }
    
    public String getAnalyseModuleOutputFilename(){
        return analyseModuleOutputFilename;
    }
    
    public String getAnalyseModuleOutputFilepath(){
        return analyseModuleOutputFilepath;
    }
    
    public void setModuleOutput(String analyseModuleOutput){
        this.analyseModuleOutput=analyseModuleOutput;
    }
    
    public void setAnalyseModuleOutputFilename(String analyseModuleOutputFilename){
        this.analyseModuleOutputFilename=analyseModuleOutputFilename;
    }
    
    public void setAnalyseModuleOutputFilepath(String analyseModuleOutputFilepath){
        this.analyseModuleOutputFilepath=analyseModuleOutputFilepath;
    }
    
    public String getAnalyseLevel(){
        return analyselevel;
    }
}
