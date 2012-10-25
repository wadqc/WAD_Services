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
import wad.xml.Instance;
import wad.xml.Patient;
import wad.xml.ReadConfigXML;
import wad.xml.Series;

/**
 *
 * @author Ralph Berendsen <>
 */
public class GetPatientFromIqcDatabase {
    private Patient patient;
    
    public GetPatientFromIqcDatabase(String pk, String level){
        patient = new Patient();
        if (level.equals("study")||level.equals("STUDY") || level.equals("studie")||level.equals("STUDIE")){
            getPatientByStudy(pk);
        } else if (level.equals("series")||level.equals("SERIES") || level.equals("serie")||level.equals("SERIE")){
            getPatientBySeries(pk);
        } else if (level.equals("instance")||level.equals("INSTANCE")){
            getPatientByInstance(pk);
        }
    }
    
    public Patient getPatient(){
        return this.patient;
    }
    
    //Patient middels studyPk geeft alle series van deze study
    private void getPatientByStudy(String studyPk){
        DatabaseParameters iqcDBParams = new DatabaseParameters();
        iqcDBParams = ReadConfigXML.ReadIqcDBParameters(iqcDBParams);
        String patientPk = ReadFromIqcDatabase.getPatientFkByStudyFkFromStudy(iqcDBParams, studyPk);
        setPatient(iqcDBParams, patientPk);
        setStudyByStudyFk(iqcDBParams, studyPk);
        //voor alle series in deze study series vullen
        ArrayList<String> seriesPkList = ReadFromIqcDatabase.getSeriesPkListWithStudyFk(iqcDBParams, studyPk);
        for (int i=0;i<seriesPkList.size();i++){
            setSeries(iqcDBParams, seriesPkList.get(i));
            ArrayList<String> instancePkList = ReadFromIqcDatabase.getInstancePkListWithSeriesFk(iqcDBParams, seriesPkList.get(i));
            for (int j=0;j<instancePkList.size();j++){
                setInstance(iqcDBParams, instancePkList.get(j),i);                
            }
        }        
    }
    
    //Patient middels seriesPk geeft maar 1 serie en bijbehorende study
    private void getPatientBySeries(String seriesPk){
        DatabaseParameters iqcDBParams = new DatabaseParameters();
        iqcDBParams = ReadConfigXML.ReadIqcDBParameters(iqcDBParams);
        String studyPk = ReadFromIqcDatabase.getStudyFkBySeriesFkFromSeries(iqcDBParams, seriesPk);
        String patientPk = ReadFromIqcDatabase.getPatientFkByStudyFkFromStudy(iqcDBParams, studyPk);
        setPatient(iqcDBParams, patientPk);
        setStudyByStudyFk(iqcDBParams, studyPk);
        //voor alleen deze series in deze study vullen
        setSeries(iqcDBParams, seriesPk);
        ArrayList<String> instancePkList = ReadFromIqcDatabase.getInstancePkListWithSeriesFk(iqcDBParams, seriesPk);
        for (int j=0;j<instancePkList.size();j++){
            setInstance(iqcDBParams, instancePkList.get(j),0);                
        }
         
    }
    
    //Patient middels instancePk geeft maar 1 serie en bijbehorende study
    private void getPatientByInstance(String instancePk){
        DatabaseParameters iqcDBParams = new DatabaseParameters();
        iqcDBParams = ReadConfigXML.ReadIqcDBParameters(iqcDBParams);
        String seriesPk = ReadFromIqcDatabase.getSeriesFkByInstanceFkFromInstance(iqcDBParams, instancePk);
        String studyPk = ReadFromIqcDatabase.getStudyFkBySeriesFkFromSeries(iqcDBParams, seriesPk);
        String patientPk = ReadFromIqcDatabase.getPatientFkByStudyFkFromStudy(iqcDBParams, studyPk);
        setPatient(iqcDBParams, patientPk);
        setStudyByStudyFk(iqcDBParams, studyPk);
        //voor alleen deze series in deze study vullen
        setSeries(iqcDBParams, seriesPk);
        ArrayList<String> instancePkList = ReadFromIqcDatabase.getInstancePkListWithSeriesFk(iqcDBParams, seriesPk);
        for (int j=0;j<instancePkList.size();j++){
            setInstance(iqcDBParams, instancePkList.get(j),0);                
        }
         
    }
    
    //Get patientinformation from database
    private void setPatient(DatabaseParameters dbParam, String patientFk){        
        Connection dbConnection;
        ResultSet rs_patient;        
        Statement stmt_patient;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_patient = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_patient = stmt_patient.executeQuery("SELECT * FROM patient WHERE pk='"+patientFk+"'");
            while (rs_patient.next()) {
                this.patient.setId(rs_patient.getString("pat_id"));
                this.patient.setName(rs_patient.getString("pat_name"));                            
            }
            stmt_patient.close();
            rs_patient.close();
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }      
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
    }
    
    //Gets study information from db and add to patient
    private void setStudyByStudyFk(DatabaseParameters dbParam, String studyFk){        
        Connection dbConnection;
        ResultSet rs_study;        
        Statement stmt_study; 
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_study = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_study = stmt_study.executeQuery("SELECT * FROM study WHERE pk='"+studyFk+"'");
            while (rs_study.next()) {
                this.patient.getStudy().setUid(rs_study.getString("study_iuid"));
                this.patient.getStudy().setDescription(rs_study.getString("study_desc"));                            
            }
            stmt_study.close();
            rs_study.close();
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }      
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
    }
    
    private void setSeries(DatabaseParameters dbParam, String seriesFk){        
        Connection dbConnection;
        ResultSet rs_series;        
        Statement stmt_series; 
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_series = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_series = stmt_series.executeQuery("SELECT * FROM series WHERE pk='"+seriesFk+"'");
            while (rs_series.next()) {
                Series series = new Series();
                series.setDescription(rs_series.getString("series_desc"));
                series.setNumber(rs_series.getString("series_no"));
                this.patient.getStudy().addSeries(series);
            }
            stmt_series.close();
            rs_series.close();
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }      
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
    }
    
    private void setInstance(DatabaseParameters dbParam, String instanceFk, int serieNo){        
        Connection dbConnection;
        ResultSet rs_instance;        
        Statement stmt_instance; 
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_instance = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_instance = stmt_instance.executeQuery("SELECT * FROM instance WHERE pk='"+instanceFk+"'");
            while (rs_instance.next()) {
                Instance instance = new Instance();                
                String filepath = "";
                String dirpath = "";
                instance.setNumber(rs_instance.getString("inst_no"));
                Statement stmt_files = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs_files = stmt_files.executeQuery("SELECT * FROM files WHERE instance_fk='"+instanceFk+"'");
                while (rs_files.next()){
                    String filessystemFk = rs_files.getString("filesystem_fk");
                    filepath = rs_files.getString("filepath");
                    filepath = filepath.replace('/', '\\');
                    Statement stmt_filesystem = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet rs_filesystem = stmt_filesystem.executeQuery("SELECT * FROM filesystem WHERE pk='"+filessystemFk+"'");
                    while (rs_filesystem.next()){
                        dirpath = rs_filesystem.getString("dirpath");
                    }
                    rs_filesystem.close();
                    stmt_filesystem.close();
                }
                String archivePath = ReadConfigXML.readFileElement("archive");
                archivePath = archivePath.replace('/', '\\');
                instance.setFilename(archivePath+dirpath+"\\"+filepath);
                rs_files.close();
                stmt_files.close();  
                this.patient.getStudy().getSeries(serieNo).addInstance(instance);
            }
            stmt_instance.close();
            rs_instance.close();
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }      
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
    }
}
