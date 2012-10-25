/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import wad.xml.Patient;
import wad.xml.ReadConfigXML;
import wad.xml.XmlAnalyseFile;
import wad.xml.XmlInputFile;

/**
 *
 * @author Ralph Berendsen <>
 */
public class WriteGewensteProcessen {
    
    public static void writeDataSeries(DatabaseParameters dbParam,String selectorPk, String seriesPk){
        Connection dbConnection;                
        Statement stmt_Write;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        
        //Read data from selector
        ResultSet rs_selector;        
        Statement stmt_selector; 
        String inputFileFk = "";
        String patientFk = "";
        String studyFk = "";
        String seriesFk = seriesPk;
        String instanceFk = "";
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_selector = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_selector = stmt_selector.executeQuery("SELECT * FROM selector WHERE pk='"+selectorPk+"'");            
            while (rs_selector.next()) {
                inputFileFk = rs_selector.getString("analysemodule_fk");
//                patientFk = rs_selector.getString("selector_patient_fk");
//                studyFk = rs_selector.getString("selector_study_fk");
//                serieFk = rs_selector.getString("selector_series_fk");
//                instanceFk = rs_selector.getString("selector_instance_fk");                
            }
            stmt_selector.close();
            rs_selector.close();
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        
        //Controleren of de data al bestaat
        ResultSet rs_gewenste;
        Statement stmt_gewenste;
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        
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
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(WriteToIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (!rowExists){
            //Write analyse_file
            String analyseFilePk = writeAnalyseFile(dbParam, seriesFk, inputFileFk); 
        
            //Write data to gewenste_processen
            String sqlTable = "INSERT INTO gewenste_processen(";
            //String sqlColomn = "selector_fk,study_fk,series_fk,instance_fk,status,analysefile_fk";
            String sqlColomn = "selector_fk,series_fk,analysefile_fk,status";
            String sqlMiddle = ") values (";
            //String sqlValues = "'"+pk+"','"+studyFk+"','"+serieFk+"','"+instanceFk+"','0','"+analyseFilePk+"'";
            String sqlValues = "'"+selectorPk+"','"+seriesFk+"','"+analyseFilePk+"','0'";
            String sqlEnd = ")";
            String sqlStatement = "";
        
            dbConnection = PacsDatabaseConnection.conDb(dbParam);
        
            try {            
                sqlStatement = sqlTable+sqlColomn+sqlMiddle+sqlValues+sqlEnd;
                stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                int count = stmt_Write.executeUpdate(sqlStatement);            
//              ResultSet rs = stmt_Write.getGeneratedKeys();            
//              rs.close();
                stmt_Write.close();            
            } catch (SQLException ex) {
                PacsDatabaseConnection.closeDb(dbConnection, null, null);
                Logger.getLogger(WriteToIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
            }
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
        }
        
    }
    
    public static void writeDataStudy(DatabaseParameters dbParam,String selectorPk, String studyPk){
        Connection dbConnection;                
        Statement stmt_Write;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        
        //Read data from selector
        ResultSet rs_selector;        
        Statement stmt_selector; 
        String inputFileFk = "";
        String patientFk = "";
        String studyFk = studyPk;
        String serieFk = "";
        String instanceFk = "";
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_selector = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_selector = stmt_selector.executeQuery("SELECT * FROM selector WHERE pk='"+selectorPk+"'");            
            while (rs_selector.next()) {
                inputFileFk = rs_selector.getString("analysemodule_fk");
//                patientFk = rs_selector.getString("selector_patient_fk");
//                studyFk = rs_selector.getString("selector_study_fk");
//                serieFk = rs_selector.getString("selector_series_fk");
//                instanceFk = rs_selector.getString("selector_instance_fk");                
            }
            stmt_selector.close();
            rs_selector.close();
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        
        
        //Controleren of de data al bestaat
        ResultSet rs_gewenste;
        Statement stmt_gewenste;
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        
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
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(WriteToIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        
        if (!rowExists){
            //Write analyse_file
            String analyseFilePk = writeAnalyseFile(dbParam, studyFk,inputFileFk); 
        
            //Write data to gewenste_processen
            String sqlTable = "INSERT INTO gewenste_processen(";
            //String sqlColomn = "selector_fk,study_fk,series_fk,instance_fk,status,analysefile_fk";
            String sqlColomn = "selector_fk,study_fk,analysefile_fk,status";
            String sqlMiddle = ") values (";
            //String sqlValues = "'"+pk+"','"+studyFk+"','"+serieFk+"','"+instanceFk+"','0','"+analyseFilePk+"'";
            String sqlValues = "'"+selectorPk+"','"+studyFk+"','"+analyseFilePk+"','0'";
            String sqlEnd = ")";
            String sqlStatement = "";
        
            dbConnection = PacsDatabaseConnection.conDb(dbParam);
        
            try {            
                sqlStatement = sqlTable+sqlColomn+sqlMiddle+sqlValues+sqlEnd;
                stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                int count = stmt_Write.executeUpdate(sqlStatement);            
//              ResultSet rs = stmt_Write.getGeneratedKeys();            
//              rs.close();
                stmt_Write.close();            
            } catch (SQLException ex) {
                PacsDatabaseConnection.closeDb(dbConnection, null, null);
                Logger.getLogger(WriteToIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
            }
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
        }   
    }
    
    //Write analysefile.xml information to analysefile table and return the pk
    private static String writeAnalyseFile(DatabaseParameters dbParam, String studySeriePk, String inputFileFk){
        Connection dbConnection;                
        Statement stmt_Write;
        
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date date = new Date();
                
        String filepath = ReadConfigXML.readFileElement("analysefile");
        
        XmlInputFile xmlinputFile = new XmlInputFile();
        String inputFileStr = ReadFromIqcDatabase.getFilenameFromTable(dbParam, "inputfile", inputFileFk);
        xmlinputFile.read(inputFileStr);        
        GetPatientFromIqcDatabase patient = new GetPatientFromIqcDatabase( studySeriePk, xmlinputFile.getAnalyseLevel());
        XmlAnalyseFile xmlAnaFile = new XmlAnalyseFile(patient.getPatient(), xmlinputFile);
        xmlAnaFile.write(filepath+dateFormat.format(date)+"_analyse.xml");        
        
        String sqlTable = "INSERT INTO analysefile(";
        String sqlColomn = "filename, filepath";
        String sqlMiddle = ") values (";
        String sqlValues = "'"+dateFormat.format(date)+"_analyse.xml','"+filepath+"'";
        String sqlEnd = ")";
        String sqlStatement = "";
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        
        try {            
            sqlStatement = sqlTable+sqlColomn+sqlMiddle+sqlValues+sqlEnd;
            stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            int count = stmt_Write.executeUpdate(sqlStatement, Statement.RETURN_GENERATED_KEYS);
            int autoIncKeyFromApi = -1;
            ResultSet rs = stmt_Write.getGeneratedKeys();
            if (rs.next()){
                autoIncKeyFromApi = rs.getInt(1);
            }
            rs.close();
            stmt_Write.close();
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            return Integer.toString(autoIncKeyFromApi);
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(WriteToIqcDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return "-1";        
    }
    
}
