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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import wad.xml.*;

/**
 *
 * @author Ralph Berendsen <>
 */
public class WriteGewensteProcessen {
    
    private static Log log = LogFactory.getLog(WriteGewensteProcessen.class);
    
    public static void writeDataSeries(Connection dbConnection,String selectorPk, String seriesPk){
        Statement stmt_Write;
        
        //Read data from selector
//        ResultSet rs_selector;        
//        Statement stmt_selector; 
//        String analyseModuleFk = "";
//        String patientFk = "";
//        String seriesFk = "";
        String seriesFk = seriesPk;
//        String instanceFk = "";
        
//        try {
//            stmt_selector = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
//            rs_selector = stmt_selector.executeQuery("SELECT * FROM selector WHERE pk='"+selectorPk+"'");            
//            while (rs_selector.next()) {
//                analyseModuleFk = rs_selector.getString("analysemodule_fk");
//            }
//            stmt_selector.close();
//            rs_selector.close();
//        } catch (SQLException ex) {
//            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        //Controleren of de data al bestaat
        ResultSet rs_gewenste;
        Statement stmt_gewenste;
        
        String sqlSelect = "SELECT * FROM gewenste_processen WHERE ";
        sqlSelect = sqlSelect + "selector_fk='"+selectorPk+"' AND ";
        sqlSelect = sqlSelect + "series_fk='"+seriesFk+"' AND ";
        sqlSelect = sqlSelect + "status='0'";
        Boolean rowExists = false;
        try {
            stmt_gewenste = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_gewenste = stmt_gewenste.executeQuery(sqlSelect);
            if (rs_gewenste.next()){
                rowExists = true;
            }
            stmt_gewenste.close();
            rs_gewenste.close();
        } catch (SQLException ex) {            
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{WriteGewensteProcessen.class.getName(), ex});
            log.error(ex);
        }
        
        if (!rowExists){
            //Write analyse_file
            ArrayList<String> analyseFilePk = writeAnalyseFile(dbConnection, seriesFk, selectorPk); 
        
            //Write data to gewenste_processen
            String sqlTable = "INSERT INTO gewenste_processen(";
            String sqlColomn = "selector_fk,series_fk,analysemodule_input_fk,analysemodule_output_fk,status";
            String sqlMiddle = ") values (";
            String sqlValues = "'"+selectorPk+"','"+seriesFk+"','"+analyseFilePk.get(0)+"','"+analyseFilePk.get(1)+"','0'";
            String sqlEnd = ")";
            String sqlStatement = "";
        
            try {            
                sqlStatement = sqlTable+sqlColomn+sqlMiddle+sqlValues+sqlEnd;
                stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                int count = stmt_Write.executeUpdate(sqlStatement);
                stmt_Write.close();
                //WriteToIqcDatabase.UpdateSeriesStatus(dbConnection, seriesFk, "3");
            } catch (SQLException ex) {
                //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{WriteGewensteProcessen.class.getName(), ex});
                log.error(ex);
            }
        }
        
    }
    
    public static void writeDataStudy(Connection dbConnection,String selectorPk, String studyPk){
        Statement stmt_Write;
                
        //Read data from selector
//        ResultSet rs_selector;        
//        Statement stmt_selector; 
//        String analyseModuleFk = "";
//        String patientFk = "";
        String studyFk = studyPk;
//        String serieFk = "";
//        String instanceFk = "";
        
//        try {
//            stmt_selector = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
//            rs_selector = stmt_selector.executeQuery("SELECT * FROM selector WHERE pk='"+selectorPk+"'");            
//            while (rs_selector.next()) {
//                analyseModuleFk = rs_selector.getString("analysemodule_fk");
//            }
//            stmt_selector.close();
//            rs_selector.close();
//        } catch (SQLException ex) {
//            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        //Controleren of de data al bestaat
        ResultSet rs_gewenste;
        Statement stmt_gewenste;
        
        String sqlSelect = "SELECT * FROM gewenste_processen WHERE ";
        sqlSelect = sqlSelect + "selector_fk='"+selectorPk+"' AND ";
        sqlSelect = sqlSelect + "study_fk='"+studyFk+"' AND ";
        sqlSelect = sqlSelect + "status='0'";
        Boolean rowExists = false;
        try {
            stmt_gewenste = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_gewenste = stmt_gewenste.executeQuery(sqlSelect);
            if (rs_gewenste.next()){
                rowExists = true;
            }
            stmt_gewenste.close();
            rs_gewenste.close();
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{WriteGewensteProcessen.class.getName(), ex});
            log.error(ex);
        }
        
        if (!rowExists){
            //Write analyse_file
            ArrayList<String> analyseFilePk = writeAnalyseFile(dbConnection, studyFk, selectorPk); 
        
            //Write data to gewenste_processen
            String sqlTable = "INSERT INTO gewenste_processen(";
            String sqlColomn = "selector_fk,study_fk,analysemodule_input_fk,analysemodule_output_fk,status";
            String sqlMiddle = ") values (";
            String sqlValues = "'"+selectorPk+"','"+studyFk+"','"+analyseFilePk.get(0)+"','"+analyseFilePk.get(1)+"','0'";
            String sqlEnd = ")";
            String sqlStatement = "";
                    
            try {            
                sqlStatement = sqlTable+sqlColomn+sqlMiddle+sqlValues+sqlEnd;
                stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                int count = stmt_Write.executeUpdate(sqlStatement);
                stmt_Write.close();
            } catch (SQLException ex) {
                //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{WriteGewensteProcessen.class.getName(), ex});
                log.error(ex);
            }
        }   
    }
    
    public static void writeDataInstance(Connection dbConnection,String selectorPk, String instancePk){
        Statement stmt_Write;        
        
        //Read data from selector
//        ResultSet rs_selector;        
//        Statement stmt_selector; 
//        String analyseModuleFk = "";
//        String patientFk = "";
//        String seriesFk = "";
//        String serieFk = "";
        String instanceFk = instancePk;
        
//        try {
//            stmt_selector = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
//            rs_selector = stmt_selector.executeQuery("SELECT * FROM selector WHERE pk='"+selectorPk+"'");            
//            while (rs_selector.next()) {
//                analyseModuleFk = rs_selector.getString("analysemodule_fk");
//            }
//            stmt_selector.close();
//            rs_selector.close();
//        } catch (SQLException ex) {
//            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        //Controleren of de data al bestaat
        ResultSet rs_gewenste;
        Statement stmt_gewenste;
        
        String sqlSelect = "SELECT * FROM gewenste_processen WHERE ";
        sqlSelect = sqlSelect + "selector_fk='"+selectorPk+"' AND ";
        sqlSelect = sqlSelect + "instance_fk='"+instanceFk+"' AND ";
        sqlSelect = sqlSelect + "status='0'";
        Boolean rowExists = false;
        try {
            stmt_gewenste = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_gewenste = stmt_gewenste.executeQuery(sqlSelect);
            if (rs_gewenste.next()){
                rowExists = true;
            }
            stmt_gewenste.close();
            rs_gewenste.close();
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{WriteGewensteProcessen.class.getName(), ex});
            log.error(ex);
        }
                
        if (!rowExists){
            //Write analyse_file
            ArrayList<String> analyseFilePk = writeAnalyseFile(dbConnection, instanceFk, selectorPk); 
        
            //Write data to gewenste_processen
            String sqlTable = "INSERT INTO gewenste_processen(";
            String sqlColomn = "selector_fk,instance_fk,analysemodule_input_fk,analysemodule_output_fk,status";
            String sqlMiddle = ") values (";
            String sqlValues = "'"+selectorPk+"','"+instanceFk+"','"+analyseFilePk.get(0)+"','"+analyseFilePk.get(1)+"','0'";
            String sqlEnd = ")";
            String sqlStatement = "";
                    
            try {            
                sqlStatement = sqlTable+sqlColomn+sqlMiddle+sqlValues+sqlEnd;
                stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                int count = stmt_Write.executeUpdate(sqlStatement); 
                stmt_Write.close();
            } catch (SQLException ex) {
                //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{WriteGewensteProcessen.class.getName(), ex});
                log.error(ex);
            }            
        }   
    }
    
    //Write analysefile.xml information to analysefile table and return the pk
    private static ArrayList<String> writeAnalyseFile(Connection dbConnection, String levelPk, String selectorFk){
        Statement stmt_Write;
        ArrayList<String> fks = new ArrayList<String>();
        
        //Karakters zoals "-",":" en " " zijn niet wenselijk in een bestandsnaam, vandaar een eenvoudig formaat
        //DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date date = new Date();
        
        String anaModInputFilepath = "XML/analysemodule_input/";
        String absAnaModInputFilepath = ReadConfigXML.readFileElement("analysemodule_input")+anaModInputFilepath;                

        AnalyseModuleInputValues anaModInputVal = writeFiles(dbConnection, levelPk, selectorFk, dateFormat.format(date));
        String selectorName = anaModInputVal.getSelectorName();
                
        String sqlTableInput = "INSERT INTO analysemodule_input(";
        String sqlTableOutput = "INSERT INTO analysemodule_output(";
        String sqlColomn = "filename, filepath";
        String sqlMiddle = ") values (";
        String sqlValuesInput = "'"+dateFormat.format(date)+".xml','"+anaModInputFilepath+selectorName+File.separator+"'";
        String sqlValuesOutput = "'"+anaModInputVal.getAnalyseModuleOutputFilename()+"','"+anaModInputVal.getAnalyseModuleOutputFilepath()+"'";
        String sqlEnd = ")";
        String sqlStatementInput = sqlTableInput+sqlColomn+sqlMiddle+sqlValuesInput+sqlEnd;
        String sqlStatementOutput = sqlTableOutput+sqlColomn+sqlMiddle+sqlValuesOutput+sqlEnd;
        
        try {            
            
            stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            int count = stmt_Write.executeUpdate(sqlStatementInput, Statement.RETURN_GENERATED_KEYS);
            int autoIncKeyFromApi = -1;
            ResultSet rs = stmt_Write.getGeneratedKeys();
            if (rs.next()){
                autoIncKeyFromApi = rs.getInt(1);
            }
            rs.close();
            stmt_Write.close();
            fks.add(Integer.toString(autoIncKeyFromApi));
            stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            count = stmt_Write.executeUpdate(sqlStatementOutput, Statement.RETURN_GENERATED_KEYS);
            autoIncKeyFromApi = -1;
            rs = stmt_Write.getGeneratedKeys();
            if (rs.next()){
                autoIncKeyFromApi = rs.getInt(1);
            }
            rs.close();
            stmt_Write.close();
            fks.add(Integer.toString(autoIncKeyFromApi));                   
            // \XML\analysemodule_output\selectorname\datetime\result.xml
            
            return fks;
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{WriteGewensteProcessen.class.getName(), ex});
            log.error(ex);
        }
        fks.add("-1");
        fks.add("-1");
        return fks;        
    }
    
    private static AnalyseModuleInputValues writeFiles(Connection dbConnection, String levelPk, String selectorFk, String createDateTime){
        AnalyseModuleInputValues analyseModuleInputValues = new AnalyseModuleInputValues(dbConnection, selectorFk, createDateTime);
        AnalyseModuleOutputFile anaModOutFile = new AnalyseModuleOutputFile(dbConnection, selectorFk, createDateTime);
        analyseModuleInputValues.setModuleOutput(anaModOutFile.getAbsoluteFilename());
        analyseModuleInputValues.setAnalyseModuleOutputFilename(anaModOutFile.getFilename());
        analyseModuleInputValues.setAnalyseModuleOutputFilepath(anaModOutFile.getFilepath());
        // aanpassen bij absoluut filepath voor XML in config.xml
//        String currentDir = System.getProperty("user.dir");
//        File dir = new File(currentDir);
//        String mainDir = dir.getParent();
//        String anaModInputFilepath = ReadConfigXML.readFileElement("analysemodule_input");
        String absAnaModInputFilepath = ReadConfigXML.readFileElement("XML")+"XML/analysemodule_input/";
        
        //String anaModAbsFilename = anaModInputFilepath.replace("..", mainDir);
        //String anaModAbsFilename = absAnaModInputFilepath.replace("/","\\");
        GetPatientFromIqcDatabase patient = new GetPatientFromIqcDatabase( dbConnection, levelPk, analyseModuleInputValues.getAnalyseLevel());
        AnalyseModuleInputFile anaModInputFile = new AnalyseModuleInputFile(patient.getPatient(), analyseModuleInputValues);        
        String selectorName = anaModOutFile.getSelectorName();
        anaModInputFile.write(absAnaModInputFilepath+selectorName+File.separator+createDateTime+".xml");        
        return analyseModuleInputValues;    
    }
}
