/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package selector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import wad.db.CollectorStatusTable;
import wad.db.WriteGewensteProcessen;
import wad.db.WriteToIqcDatabase;
//import wad.logger.LoggerWrapper;
//import wad_selector.SelectorTimer;

/**
 *
 * @author Ralph Berendsen <>
 */
public class Selector {
    //DatabaseParameters dbParam;
    Connection dbConnection;
    
    private static Log log = LogFactory.getLog(Selector.class);
    
    public void Selector(Connection dbConnection){
        this.dbConnection = dbConnection;
        CollectorStatusTable colSeriesStatusTable = new CollectorStatusTable(this.dbConnection, "collector_series_status"); 
        CollectorStatusTable colStudyStatusTable = new CollectorStatusTable(this.dbConnection, "collector_study_status"); 
        //Lijst met de keys behorende bij de series en study met status = 1
        ArrayList<String> seriesFk = new ArrayList<String>() ;
        ArrayList<String> studyFk = new ArrayList<String>() ;
        //ArrayList<Integer> removeSeries = new ArrayList<Integer> ();
        //ArrayList<Integer> removeStudy = new ArrayList<Integer> ();
        //Selecteer alleen de seriesLijst en studies met collector_status=1 (voor study ook 0, om selecties op serie niveau en verwerking op study nieveau te blokkeren als de study nog niet compleet is)
        //TODO status update en i++ samen met i-- geeft problemen bij de juiste rijen
        for (int i=0;i<colSeriesStatusTable.size();i++){
            if (!colSeriesStatusTable.getColllectorStatusRow(i).getStatus().equals("1")){
                colSeriesStatusTable.removeColllectorStatusRow(i);
                i--;
            } else {
                //Voor elke series met status=1 uit collector_status een lijst opbouwen
                seriesFk.add(colSeriesStatusTable.getColllectorStatusRow(i).getFk());
            }
        }
        for (int i=0;i<colStudyStatusTable.size();i++){
            if (!colStudyStatusTable.getColllectorStatusRow(i).getStatus().equals("1")) {
                colStudyStatusTable.removeColllectorStatusRow(i);
                i--;
            } else {
                //Voor elke studie met status=1 of 0 uit collector_status een lijst opbouwen
                studyFk.add(colStudyStatusTable.getColllectorStatusRow(i).getFk());                
            }
        }
        
        //Per Analyselvl een uitwerking, hierbij is de lijst met series van belang voor de lvl instance en series, en de lijst met study voor lvl study
        selectorInstanceLevel(seriesFk);
        selectorSeriesLevel(seriesFk);
        selectorStudyLevel(studyFk);
    }
    
    private void selectorInstanceLevel(ArrayList<String> seriesFkList){        
        ArrayList<String> selectorPk = getSelectorPkByAnalyseLvl("instance");
        //per series beoordelen of voldaan wordt aan de criteria van elke selector
        for (int i=0;i<seriesFkList.size();i++){
            boolean patientMatch = false;
            boolean studyMatch = false;
            boolean seriesMatch = false;
            boolean instanceMatch = false;
            for (int j=0;j<selectorPk.size();j++){
                //study en patient bij deze series zoeken
                String studyFk = getStudyFkFromSeries(seriesFkList.get(i));
                String patientFk = getPatientFkFromStudy(studyFk);                
                //Heeft selector een patient_fk dan vergelijken, anders doorgaan met study_fk                
                String selectorPatientFk = getLevelFkByPk(selectorPk.get(j), "patient");
                if (selectorPatientFk!=null){
                    //controleer of er een match is
                    //Maak een hashmap met <kolomnaam, waarde> van selector_patient met betreffende pk
                    HashMap<String, String> selectorPatientMap = getSelectorLevelHashmap(selectorPatientFk, "patient"); 
                    //Maak een hashmap met <kolomnaam, waarde> van patient met betreffende pk
                    HashMap<String, String> patientMap = getLevelHashmap(patientFk, "patient");
                    patientMatch = isEqualPatientHashmaps(selectorPatientMap, patientMap);
                } else {
                    //Is selector_patient_fk er niet dan is patientMatch true, geen criteria is altijd een match
                    patientMatch = true;
                }
                if (!patientMatch){
                    continue;
                }
                //Heeft selector een study_fk dan vergelijken, anders doorgaan met series_fk
                String selectorStudyFk = getLevelFkByPk(selectorPk.get(j), "study");
                if (selectorStudyFk!=null){
                    //controleer of er een match is
                    //Maak een hashmap met <kolomnaam, waarde> van selector_study met betreffende pk
                    HashMap<String, String> selectorStudyMap = getSelectorLevelHashmap(selectorStudyFk, "study"); 
                    //Maak een hashmap met <kolomnaam, waarde> van study met betreffende pk
                    HashMap<String, String> studyMap = getLevelHashmap(studyFk, "study");
                    studyMatch = isEqualStudyHashmaps(selectorStudyMap, studyMap);
                } else {
                    studyMatch = true;
                }
                if (!studyMatch){
                    continue;
                }
                //Heeft selector een series_fk dan vergelijken, anders doorgaan met instance_fk
                String selectorSeriesFk = getLevelFkByPk(selectorPk.get(j), "series");
                if (selectorSeriesFk!=null){
                    //controleer of er een match is
                    //Maak een hashmap met <kolomnaam, waarde> van selector_series met betreffende pk
                    HashMap<String, String> selectorSeriesMap = getSelectorLevelHashmap(selectorSeriesFk, "series"); 
                    //Maak een hashmap met <kolomnaam, waarde> van series met betreffende pk
                    HashMap<String, String> seriesMap = getLevelHashmap(seriesFkList.get(i), "series");
                    seriesMatch = isEqualSeriesHashmaps(selectorSeriesMap, seriesMap);
                } else {
                    seriesMatch = true;
                }
                if (!seriesMatch){
                    continue;
                }
                //Heeft selector een instance_fk dan vergelijken, anders verwerken in gewensteprocess.
                //Indien de criteria op patient, study of series level niet overeen kwamen was dit stuk code niet bereikt.
                //De break moest dit blokkeren
                String selectorInstanceFk = getLevelFkByPk(selectorPk.get(j), "instance");
                if (selectorInstanceFk!=null){
                    //controleer of er een match is
                    //Maak een hashmap met <kolomnaam, waarde> van selector_instance met betreffende pk
                    HashMap<String, String> selectorInstanceMap = getSelectorLevelHashmap(selectorInstanceFk, "instance"); 
                    //Voor elke instance in deze series moet een controle plaatsvinden.
                    //Als patientMatch, studyMatch, seriesMatch en instanceMatch is true dan moet deze op instanceLevel in GewensteProcessen
                    ArrayList<String> instancePkList = getInstancePkListFromInstance(seriesFkList.get(i));
                    for (int k=0;k<instancePkList.size();k++){
                        //Maak een hashmap met <kolomnaam, waarde> van de instance met betreffende pk
                        HashMap<String, String> instanceMap = getLevelHashmap(instancePkList.get(k), "instance");
                        instanceMatch = isEqualInstanceHashmaps(selectorInstanceMap, instanceMap);
                        if (instanceMatch){
                            //Toevoegen aan tabel gewensteprocessen
                            WriteGewensteProcessen.writeDataInstance(this.dbConnection, selectorPk.get(j), instancePkList.get(k));
                        }
                    }
                } else {
                    instanceMatch = true;
                    //Er zijn geen selectiecriteria op instancelevel, overige selectiecriteria stemmen overeen met deze patient/study/serie, dus alle instances in deze serie verwerken
                    ArrayList<String> instancePkList = getInstancePkListFromInstance(seriesFkList.get(i));
                    for (int k=0;k<instancePkList.size();k++){
                        WriteGewensteProcessen.writeDataInstance(this.dbConnection, selectorPk.get(j), instancePkList.get(k));
                    }
                }                 
            }
        }
    }
    
    private void selectorSeriesLevel(ArrayList<String> seriesFkList){
        ArrayList<String> selectorPk = getSelectorPkByAnalyseLvl("series");
        //per series beoordelen of voldaan wordt aan de criteria van elke selector
        for (int i=0;i<seriesFkList.size();i++){
            boolean patientMatch = false;
            boolean studyMatch = false;
            boolean seriesMatch = false;
            boolean instanceMatch = false;
            for (int j=0;j<selectorPk.size();j++){
                //study en patient bij deze series zoeken
                String studyFk = getStudyFkFromSeries(seriesFkList.get(i));
                String patientFk = getPatientFkFromStudy(studyFk);                
                //Heeft selector een patient_fk dan vergelijken, anders doorgaan met study_fk                
                String selectorPatientFk = getLevelFkByPk(selectorPk.get(j), "patient");
                if (selectorPatientFk!=null){
                    //controleer of er een match is
                    //Maak een hashmap met <kolomnaam, waarde> van selector_patient met betreffende pk
                    HashMap<String, String> selectorPatientMap = getSelectorLevelHashmap(selectorPatientFk, "patient"); 
                    //Maak een hashmap met <kolomnaam, waarde> van patient met betreffende pk
                    HashMap<String, String> patientMap = getLevelHashmap(patientFk, "patient");
                    patientMatch = isEqualPatientHashmaps(selectorPatientMap, patientMap);
                } else {
                    //Is selector_patient_fk er niet dan is patientMatch true, geen criteria is altijd een match
                    patientMatch = true;
                }
                if (!patientMatch){
                    continue;
                }
                //Heeft selector een study_fk dan vergelijken, anders doorgaan met series_fk
                String selectorStudyFk = getLevelFkByPk(selectorPk.get(j), "study");
                if (selectorStudyFk!=null){
                    //controleer of er een match is
                    //Maak een hashmap met <kolomnaam, waarde> van selector_study met betreffende pk
                    HashMap<String, String> selectorStudyMap = getSelectorLevelHashmap(selectorStudyFk, "study"); 
                    //Maak een hashmap met <kolomnaam, waarde> van study met betreffende pk
                    HashMap<String, String> studyMap = getLevelHashmap(studyFk, "study");
                    studyMatch = isEqualStudyHashmaps(selectorStudyMap, studyMap);
                } else {
                    studyMatch = true;
                }
                if (!studyMatch){
                    continue;
                }
                //Heeft selector een series_fk dan vergelijken, anders doorgaan met instance_fk
                String selectorSeriesFk = getLevelFkByPk(selectorPk.get(j), "series");
                if (selectorSeriesFk!=null){
                    //controleer of er een match is
                    //Maak een hashmap met <kolomnaam, waarde> van selector_series met betreffende pk
                    HashMap<String, String> selectorSeriesMap = getSelectorLevelHashmap(selectorSeriesFk, "series"); 
                    //Maak een hashmap met <kolomnaam, waarde> van series met betreffende pk
                    HashMap<String, String> seriesMap = getLevelHashmap(seriesFkList.get(i), "series");
                    seriesMatch = isEqualSeriesHashmaps(selectorSeriesMap, seriesMap);
                } else {
                    seriesMatch = true;
                }
                if (!seriesMatch){
                    continue;
                }
                //Heeft selector een instance_fk dan vergelijken, anders verwerken in gewensteprocess.
                //Indien de criteria op patient, study of series level niet overeen kwamen was dit stuk code niet bereikt.
                //De continue moest dit blokkeren
                String selectorInstanceFk = getLevelFkByPk(selectorPk.get(j), "instance");
                if (selectorInstanceFk!=null){
                    //controleer of er een match is
                    //Maak een hashmap met <kolomnaam, waarde> van selector_instance met betreffende pk
                    HashMap<String, String> selectorInstanceMap = getSelectorLevelHashmap(selectorInstanceFk, "instance"); 
                    //Voor elke instance in deze series moet een controle plaatsvinden.
                    //Als instanceMatch is true voor één van de instances in deze series dan moet deze series op seriesLevel in GewensteProcessen
                    ArrayList<String> instancePkList = getInstancePkListFromInstance(seriesFkList.get(i));
                    for (int k=0;k<instancePkList.size();k++){
                        //Maak een hashmap met <kolomnaam, waarde> van de instance met betreffende pk
                        HashMap<String, String> instanceMap = getLevelHashmap(instancePkList.get(k), "instance");
                        instanceMatch = isEqualInstanceHashmaps(selectorInstanceMap, instanceMap);
                        if (instanceMatch){
                            //Toevoegen aan tabel gewensteprocessen
                            WriteGewensteProcessen.writeDataSeries(this.dbConnection, selectorPk.get(j), seriesFkList.get(i));
                            //Na één match hoeft er verder geen controle te zijn dus break
                            break;
                        }
                    }
                } else {
                    instanceMatch = true;
                    //Er zijn geen selectiecriteria op instancelevel, overige selectiecriteria stemmen overeen met deze patient/study/serie, dus alle instances in deze serie verwerken
                    WriteGewensteProcessen.writeDataSeries(this.dbConnection, selectorPk.get(j), seriesFkList.get(i));                    
                }                 
            }
            WriteToIqcDatabase.UpdateSeriesStatus(this.dbConnection, seriesFkList.get(i), "3");
        }
    }
    
    private void selectorStudyLevel(ArrayList<String> studyFkList){
        ArrayList<String> selectorPk = getSelectorPkByAnalyseLvl("study");
        //per study beoordelen of voldaan wordt aan de criteria van elke selector
        for (int i=0;i<studyFkList.size();i++){
            boolean patientMatch = false;
            boolean studyMatch = false;
            boolean seriesMatch = false;
            boolean instanceMatch = false;
            for (int j=0;j<selectorPk.size();j++){
                //patient bij deze study zoeken                
                String patientFk = getPatientFkFromStudy(studyFkList.get(i));                
                //Heeft selector een patient_fk dan vergelijken, anders doorgaan met study_fk                
                String selectorPatientFk = getLevelFkByPk(selectorPk.get(j), "patient");
                if (selectorPatientFk!=null){
                    //controleer of er een match is
                    //Maak een hashmap met <kolomnaam, waarde> van selector_patient met betreffende pk
                    HashMap<String, String> selectorPatientMap = getSelectorLevelHashmap(selectorPatientFk, "patient"); 
                    //Maak een hashmap met <kolomnaam, waarde> van patient met betreffende pk
                    HashMap<String, String> patientMap = getLevelHashmap(patientFk, "patient");
                    patientMatch = isEqualPatientHashmaps(selectorPatientMap, patientMap);
                } else {
                    //Is selector_patient_fk er niet dan is patientMatch true, geen criteria is altijd een match
                    patientMatch = true;
                }
                if (!patientMatch){
                    continue;
                }
                //Heeft selector een study_fk dan vergelijken, anders doorgaan met series_fk
                String selectorStudyFk = getLevelFkByPk(selectorPk.get(j), "study");
                if (selectorStudyFk!=null){
                    //controleer of er een match is
                    //Maak een hashmap met <kolomnaam, waarde> van selector_study met betreffende pk
                    HashMap<String, String> selectorStudyMap = getSelectorLevelHashmap(selectorStudyFk, "study"); 
                    //Maak een hashmap met <kolomnaam, waarde> van study met betreffende pk
                    HashMap<String, String> studyMap = getLevelHashmap(studyFkList.get(i), "study");
                    studyMatch = isEqualStudyHashmaps(selectorStudyMap, studyMap);
                } else {
                    studyMatch = true;
                }
                if (!studyMatch){
                    continue;
                }
                //Heeft selector een series_fk dan vergelijken, anders doorgaan met instance_fk
                String selectorSeriesFk = getLevelFkByPk(selectorPk.get(j), "series");
                if (selectorSeriesFk!=null){
                    //controleer of er een match is
                    //Maak een hashmap met <kolomnaam, waarde> van selector_series met betreffende pk
                    HashMap<String, String> selectorSeriesMap = getSelectorLevelHashmap(selectorSeriesFk, "series"); 
                    //Voor elke series in deze study moet een controle plaatsvinden.                    
                    ArrayList<String> seriesPkList = getSeriesPkListFromSeries(studyFkList.get(i));
                    for (int k=0;k<seriesPkList.size();k++){
                        //Maak een hashmap met <kolomnaam, waarde> van de series met betreffende pk
                        HashMap<String, String> seriesMap = getLevelHashmap(seriesPkList.get(k), "series");
                        seriesMatch = isEqualSeriesHashmaps(selectorSeriesMap, seriesMap);
                        if (seriesMatch){
                            //Als er een seriesMatch is moet er gecontroleerd worden of er binnen deze series een instanceMatch is
                            //Is er geen instanceMatch dan verdergaan met controleren op series anders verwerken als study in gewensteprocessen
                            
                            //Heeft selector een instance_fk dan vergelijken, anders verwerken in gewensteprocess.
                            //Indien de criteria op patient, study of series level niet overeen kwamen was dit stuk code niet bereikt.
                            //De break moest dit blokkeren
                            String selectorInstanceFk = getLevelFkByPk(selectorPk.get(j), "instance");
                            if (selectorInstanceFk!=null){
                                //controleer of er een match is
                                //Maak een hashmap met <kolomnaam, waarde> van selector_instance met betreffende pk
                                HashMap<String, String> selectorInstanceMap = getSelectorLevelHashmap(selectorInstanceFk, "instance"); 
                                //Voor elke instance in deze series moet een controle plaatsvinden.
                                //Als instanceMatch is true dan moet deze op instanceLevel in GewensteProcessen
                                ArrayList<String> instancePkList = getInstancePkListFromInstance(seriesPkList.get(k));
                                for (int m=0;m<instancePkList.size();m++){
                                    //Maak een hashmap met <kolomnaam, waarde> van de instance met betreffende pk
                                    HashMap<String, String> instanceMap = getLevelHashmap(instancePkList.get(m), "instance");
                                    instanceMatch = isEqualInstanceHashmaps(selectorInstanceMap, instanceMap);
                                    if (instanceMatch){
                                        //Toevoegen aan tabel gewensteprocessen
                                        WriteGewensteProcessen.writeDataStudy(this.dbConnection, selectorPk.get(j), studyFkList.get(i));
                                        //Er is een match, verwerking is klaar stoppen met verder controleren
                                        break;
                                    }
                                }
                                if (instanceMatch){
                                    //Als er een instanceMatch is hoeven andere series niet gecontroleerd te worden, verwerking is al gedaan
                                    break;
                                }
                            } else {
                                instanceMatch = true;
                                //Er zijn geen selectiecriteria op instancelevel, overige selectiecriteria stemmen overeen met deze patient/study/serie, dus verwerken op studie level
                                WriteGewensteProcessen.writeDataStudy(this.dbConnection, selectorPk.get(j), studyFkList.get(i));                                
                            }
                            if (instanceMatch){
                                //Als er een instanceMatch is hoeven andere series niet gecontroleerd te worden, verwerking is al gedaan
                                break;
                            }
                        }
                    }
                } else {
                    seriesMatch = true;
                    //Er is een seriesMatch omdat er geen criteria op series level zijn
                    //Voor elke series in deze study moet een controle op instancelevel plaatsvinden.                    
                    ArrayList<String> seriesPkList = getSeriesPkListFromSeries(studyFkList.get(i));
                    for (int k=0;k<seriesPkList.size();k++){
                        //Heeft selector een instance_fk dan vergelijken, anders verwerken in gewensteprocess.
                        //Indien de criteria op patient, study of series level niet overeen kwamen was dit stuk code niet bereikt.
                        //De break moest dit blokkeren
                        String selectorInstanceFk = getLevelFkByPk(selectorPk.get(j), "instance");
                        if (selectorInstanceFk!=null){
                            //controleer of er een match is
                            //Maak een hashmap met <kolomnaam, waarde> van selector_instance met betreffende pk
                            HashMap<String, String> selectorInstanceMap = getSelectorLevelHashmap(selectorInstanceFk, "instance"); 
                            //Voor elke instance in deze series moet een controle plaatsvinden.
                            //Als instanceMatch is true dan moet deze op instanceLevel in GewensteProcessen
                            ArrayList<String> instancePkList = getInstancePkListFromInstance(seriesPkList.get(k));
                            for (int m=0;m<instancePkList.size();m++){
                                //Maak een hashmap met <kolomnaam, waarde> van de instance met betreffende pk
                                HashMap<String, String> instanceMap = getLevelHashmap(instancePkList.get(m), "instance");
                                instanceMatch = isEqualInstanceHashmaps(selectorInstanceMap, instanceMap);
                                if (instanceMatch){
                                    //Toevoegen aan tabel gewensteprocessen
                                    WriteGewensteProcessen.writeDataStudy(this.dbConnection, selectorPk.get(j), studyFkList.get(i));
                                    //Er is een match, verwerking is klaar stoppen met verder controleren
                                    break;
                                }
                            }
                            if (instanceMatch){
                                //Als er een instanceMatch is hoeven andere series niet gecontroleerd te worden, verwerking is al gedaan
                                break;
                            }
                        } else {
                            instanceMatch = true;
                            //Er zijn geen selectiecriteria op instancelevel, overige selectiecriteria stemmen overeen met deze patient/study/serie, dus verwerken op studie level
                            WriteGewensteProcessen.writeDataStudy(this.dbConnection, selectorPk.get(j), studyFkList.get(i));                                
                            //
                            break;
                        }                                           
                    }
                }                                                 
            }
            WriteToIqcDatabase.UpdateStudyStatus(this.dbConnection, studyFkList.get(i), "3");
        }
    }
    
    //Deze functie geeft een lijst met selectorPk terug behorende bij een analyseLvl
    //Input voor analyselevel is:"study", "series" of "instance", anders is de ArrayList van lengte 0;
    private ArrayList<String> getSelectorPkByAnalyseLvl(String analyseLvl){
        ArrayList<String> selectorPk = new  ArrayList<String>();           
        ResultSet rs_selector;        
        Statement stmt_selector;        
        
        try {
            stmt_selector = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_selector = stmt_selector.executeQuery("SELECT * FROM selector WHERE analyselevel='"+analyseLvl+"'");            
            while (rs_selector.next()) {
                selectorPk.add(rs_selector.getString("pk"));                                              
            }            
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{Selector.class.getName(),ex});
            log.error(ex);
        }
        return selectorPk;
    }
    
    //Deze functie geeft de key behorende bij het level waarop gezocht wordt of null
    //Input voor level is:"patient", "study", "series" of "instance", anders is de ArrayList van lengte 0;
    private String getLevelFkByPk(String pk, String level){
        String levelFk = null;               
        ResultSet rs_selector;        
        Statement stmt_selector;        
        
        try {
            stmt_selector = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_selector = stmt_selector.executeQuery("SELECT * FROM selector WHERE pk='"+pk+"'");            
            while (rs_selector.next()) {
                levelFk = rs_selector.getString("selector_"+level+"_fk");                                              
            }            
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{Selector.class.getName(),ex});
            log.error(ex);
        }
        return levelFk;
    }
    
    private HashMap<String, String> getSelectorLevelHashmap(String pk, String level){
        HashMap<String, String> selectorLevelMap = new HashMap<String, String>();        
        ResultSet rs_series;        
        Statement stmt_series;
        
        try {
            stmt_series = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_series = stmt_series.executeQuery("SELECT * FROM selector_"+level+" WHERE pk='"+pk+"'");
            ResultSetMetaData md = rs_series.getMetaData();
            int columnCount = md.getColumnCount();
            while (rs_series.next()) {
                //Let op rs_series.getString(i) eerste kolom heeft index 1 en niet 0
                for (int i=1;i<=columnCount;i++){
                    selectorLevelMap.put(md.getColumnLabel(i), rs_series.getString(i));
                }             
            }
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{Selector.class.getName(),ex});
            log.error(ex);
        }        
        return selectorLevelMap;
    }
    
    private ArrayList<String> getColomnNamesFromTable(String tableName){
        ArrayList<String> columnList = new ArrayList<String>();
        ResultSet rs_series;        
        Statement stmt_series;
        
        try {
            stmt_series = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_series = stmt_series.executeQuery("SELECT * FROM "+tableName);
            ResultSetMetaData md = rs_series.getMetaData();
            int columnCount = md.getColumnCount();
            for (int i=1;i<=columnCount;i++){
                //Let op md.getColumnLabel(i) eerste kolom heeft index 1 en niet 0    
                columnList.add(md.getColumnLabel(i));
            }
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{Selector.class.getName(),ex});
            log.error(ex);
        }        
        return columnList;
    }
    
    private HashMap<String, String> getLevelHashmap(String pk, String level){
        HashMap<String, String> levelMap = new HashMap<String, String>();
        ResultSet rs_series;        
        Statement stmt_series;
        
        try {
            stmt_series = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_series = stmt_series.executeQuery("SELECT * FROM "+level+" WHERE pk='"+pk+"'");
            ResultSetMetaData md = rs_series.getMetaData();
            int columnCount = md.getColumnCount();
            while (rs_series.next()) {
                //Let op md.getColumnLabel(i) eerste kolom heeft index 1 en niet 0
                for (int i=1;i<=columnCount;i++){
                    levelMap.put(md.getColumnLabel(i), rs_series.getString(i));
                }                                               
            }
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{Selector.class.getName(),ex});
            log.error(ex);
        }
        return levelMap;
    }
    
    private boolean isEqualSeriesHashmaps(HashMap<String, String> selectorSeriesMap,HashMap<String, String> seriesMap){
        //Maak een lijst van de kolommen uit selector_series om te doorlopen een vergelijken
        ArrayList<String> columnList = getColomnNamesFromTable("selector_series");
        Boolean match = true;
        for (int j=0;j<columnList.size();j++){
            String columnLabel = columnList.get(j);
            String selectorSeriesValue = selectorSeriesMap.get(columnLabel);
            String seriesValue = seriesMap.get(columnLabel);
            if (!(selectorSeriesValue==null || seriesValue==null)){
                if (!selectorSeriesValue.equalsIgnoreCase("null") && !selectorSeriesValue.equalsIgnoreCase("")){
                    if (!columnLabel.equalsIgnoreCase("availability") && 
                        !columnLabel.equalsIgnoreCase("series_status") && 
                        !columnLabel.equalsIgnoreCase("pk") &&
                        !columnLabel.equalsIgnoreCase("pps_start") &&    
                        !columnLabel.equalsIgnoreCase("created_time") &&
                        !columnLabel.equalsIgnoreCase("updated_time")     
                            ){
                        //if (!selectorSeriesValue.equalsIgnoreCase(seriesValue)){
                        //    match=false;
                        //}
			seriesValue = Pattern.quote(seriesValue);
                        if (!selectorSeriesValue.matches("(?i:(^|(.*;))\\s*"+seriesValue+"\\s*((;.*|$)))")){
                            match=false;
                        }
                    }
                }
            }
        }
        return match; 
    }
    
    private boolean isEqualStudyHashmaps(HashMap<String, String> selectorStudyMap, HashMap<String, String> studyMap){
        //Maak een lijst van de kolommen uit selector_series om te doorlopen een vergelijken
        ArrayList<String> columnList = getColomnNamesFromTable("selector_study");
        Boolean match = true;            
        for (int j=0;j<columnList.size();j++){
            String columnLabel = columnList.get(j);
            String selectorStudyValue = selectorStudyMap.get(columnLabel);
            String studyValue = studyMap.get(columnLabel);
            if (!(selectorStudyValue==null || studyValue==null)){
                if (!selectorStudyValue.equalsIgnoreCase("null") && !selectorStudyValue.equalsIgnoreCase("")){
                    if (!columnLabel.equalsIgnoreCase("availability") && 
                        !columnLabel.equalsIgnoreCase("study_status") && 
                        !columnLabel.equalsIgnoreCase("study_datetime") && 
                        !columnLabel.equalsIgnoreCase("pk") && 
                        !columnLabel.equalsIgnoreCase("checked_time") && 
                        !columnLabel.equalsIgnoreCase("updated_time") && 
                        !columnLabel.equalsIgnoreCase("created_time")
                            ){
                        //if (!selectorStudyValue.equalsIgnoreCase(studyValue)){
                        //    match=false;
                        //}
			studyValue = Pattern.quote(studyValue);
                        if (!selectorStudyValue.matches("(?i:(^|(.*;))\\s*"+studyValue+"\\s*((;.*|$)))")){
                            match=false;
                        }
                    }
                }
            }
        }
        return match; 
    }
    
    private boolean isEqualPatientHashmaps(HashMap<String, String> selectorPatientMap,HashMap<String, String> patientMap){
        //Maak een lijst van de kolommen uit selector_series om te doorlopen een vergelijken
        ArrayList<String> columnList = getColomnNamesFromTable("selector_patient");
        Boolean match = true;
        for (int j=0;j<columnList.size();j++){
            String columnLabel = columnList.get(j);
            String selectorSeriesValue = selectorPatientMap.get(columnLabel);
            String seriesValue = patientMap.get(columnLabel);
            if (!(selectorSeriesValue==null || seriesValue==null)){
                if (!selectorSeriesValue.equalsIgnoreCase("null") && !selectorSeriesValue.equalsIgnoreCase("")){
                    if (!columnLabel.equalsIgnoreCase("pk") && 
                        !columnLabel.equalsIgnoreCase("merge_fk") &&                          
                        !columnLabel.equalsIgnoreCase("created_time") &&
                        !columnLabel.equalsIgnoreCase("updated_time") &&
                        !columnLabel.equalsIgnoreCase("pat_attrs")    
                            ){
                        //if (!selectorSeriesValue.equalsIgnoreCase(seriesValue)){
                        //    match=false;
                        //}
			seriesValue = Pattern.quote(seriesValue);
                        if (!selectorSeriesValue.matches("(?i:(^|(.*;))\\s*"+seriesValue+"\\s*((;.*|$)))")){
                            match=false;
                        }
                    }
                }
            }
        }
        return match; 
    }
    
    private boolean isEqualInstanceHashmaps(HashMap<String, String> selectorInstanceMap,HashMap<String, String> instanceMap){
        //Maak een lijst van de kolommen uit selector_series om te doorlopen een vergelijken
        ArrayList<String> columnList = getColomnNamesFromTable("selector_instance");
        Boolean match = true;
        for (int j=0;j<columnList.size();j++){
            String columnLabel = columnList.get(j);
            String selectorSeriesValue = selectorInstanceMap.get(columnLabel);
            String seriesValue = instanceMap.get(columnLabel);
            if (!(selectorSeriesValue==null || seriesValue==null)){
                if (!selectorSeriesValue.equalsIgnoreCase("null") && !selectorSeriesValue.equalsIgnoreCase("")){
                    if (!columnLabel.equalsIgnoreCase("pk") && 
                        !columnLabel.equalsIgnoreCase("content_datetime") &&  
                        !columnLabel.equalsIgnoreCase("availability") &&
                        !columnLabel.equalsIgnoreCase("inst_status") &&
                        !columnLabel.equalsIgnoreCase("all_attrs") &&
                        !columnLabel.equalsIgnoreCase("commitment") &&
                        !columnLabel.equalsIgnoreCase("archived") &&
                        !columnLabel.equalsIgnoreCase("created_time") &&
                        !columnLabel.equalsIgnoreCase("updated_time") &&
                        !columnLabel.equalsIgnoreCase("inst_attrs")     
                            ){
                        //if (!selectorSeriesValue.equals(seriesValue)){
                        //    match=false;
                        //}
			seriesValue = Pattern.quote(seriesValue);
                        if (!selectorSeriesValue.matches("(?i:(^|(.*;))\\s*"+seriesValue+"\\s*((;.*|$)))")){
                            match=false;
                        }
                    }
                }
            }
        }
        return match; 
    }
    
    private String getStudyFkFromSeries(String seriesFk){
        ResultSet rs_series;        
        Statement stmt_series;        
        
        try {
            stmt_series = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String statement = "SELECT study_fk FROM series WHERE pk='"+seriesFk+"'";
            rs_series = stmt_series.executeQuery(statement);            
            if (rs_series.next()) {
                String studyFk = rs_series.getString("study_fk");
                return studyFk;                               
            } else {
                return null;
            }
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{Selector.class.getName(),ex});
            log.error(ex);
        }
        return null;
    }
    
    private String getPatientFkFromStudy(String studyFk){
        ResultSet rs_study;        
        Statement stmt_study;        
        
        try {
            stmt_study = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String statement = "SELECT patient_fk FROM study WHERE pk='"+studyFk+"'";
            rs_study = stmt_study.executeQuery(statement);            
            if (rs_study.next()) {
                String patientFk = rs_study.getString("patient_fk");
                return patientFk;                               
            } else {
                return null;
            }
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{Selector.class.getName(),ex});
            log.error(ex);
        }
        return null;
    }
    
    private ArrayList<String> getInstancePkListFromInstance(String seriesFk){
        ArrayList<String> instancePkList = new  ArrayList<String>();
        ResultSet rs_instance;        
        Statement stmt_instance;        
        
        try {
            stmt_instance = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String statement = "SELECT pk FROM instance WHERE series_fk='"+seriesFk+"'";
            rs_instance = stmt_instance.executeQuery(statement);            
            while (rs_instance.next()) {
                instancePkList.add(rs_instance.getString("pk"));                                               
            }
            return instancePkList;
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{Selector.class.getName(),ex});
            log.error(ex);
        }
        return null;
    }
    
    private ArrayList<String> getSeriesPkListFromSeries(String studyFk){
        ArrayList<String> instancePkList = new  ArrayList<String>();
        ResultSet rs_series;        
        Statement stmt_series;        
        
        try {
            stmt_series = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String statement = "SELECT pk FROM series WHERE study_fk='"+studyFk+"'";
            rs_series = stmt_series.executeQuery(statement);            
            while (rs_series.next()) {
                instancePkList.add(rs_series.getString("pk"));                                               
            }
            return instancePkList;
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{Selector.class.getName(),ex});
            log.error(ex);
        }
        return null;
    }
}
