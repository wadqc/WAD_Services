/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_selector;

import java.util.ArrayList;
import java.util.TimerTask;
import selector.SelectorMatch;
import selector.SelectorSummary;
import wad.db.DatabaseParameters;
import wad.db.ReadFromIqcDatabase;
import wad.xml.ReadConfigXML;
import wad.db.*;
import wad.xml.Patient;
import wad.xml.XmlAnalyseFile;
import wad.xml.XmlInputFile;


/**
 *
 * @author Ralph Berendsen <>
 */
public class SelectorTimer_old extends TimerTask{

    @Override
    public void run() {
        //Testen van XmlInputFile
//        XmlInputFile inputFile = new XmlInputFile();
//        inputFile.read("C:/apps/BVT/WAD_Software/XML/XMLinput/MRI_Joost_input.xml");
//        String analyseLevel = inputFile.getAnalyseLevel();
//        
        //Testen patient voor wegschrijven analysefile        
        //GetPatientFromIqcDatabase getPatient = new GetPatientFromIqcDatabase("17", "study") ;
//        GetPatientFromIqcDatabase getPatient = new GetPatientFromIqcDatabase("51", "series") ;
//        Patient patient = getPatient.getPatient();
//        
//        XmlAnalyseFile xmlAnalyseFile = new XmlAnalyseFile(patient, inputFile);
//        xmlAnalyseFile.write("C:/apps/BVT/WAD_Software/XML/XMLanalyse/MRI_Joost_analyse2.xml");
//        
//        XmlAnalyseFile xmlAnalyseFile2 = new XmlAnalyseFile(patient, inputFile);
//        xmlAnalyseFile2.read("C:/apps/BVT/WAD_Software/XML/XMLanalyse/MRI_Joost_analyse2.xml");
        
        
        
        
        
        DatabaseParameters iqcDBParams = new DatabaseParameters();
        iqcDBParams = ReadConfigXML.ReadIqcDBParameters(iqcDBParams);
        
        //testen nieuwe selector code
        SelectorSummary selectorSummary = new SelectorSummary();
        selectorSummary.Selector(iqcDBParams);
        int test=0;
        //Testen van wegschrijven naar gewenste processen
        //WriteGewensteProcessen.writeDataStudy(iqcDBParams, "2", "16");
        //WriteGewensteProcessen.writeDataSeries(iqcDBParams, "7", "1");
        //temp variable for testing
        
        
        CollectorStatusTable colSeriesStatusTable = new CollectorStatusTable(iqcDBParams, "collector_series_status"); 
        CollectorStatusTable colStudyStatusTable = new CollectorStatusTable(iqcDBParams, "collector_study_status"); 
        
        Boolean testBoolean = false;
        
        for (int i=0;i<colSeriesStatusTable.size();i++){
            String colSeriesStatusSeriesFk = colSeriesStatusTable.getColllectorStatusRow(i).getFk();
            String colSeriesStatusPk = colSeriesStatusTable.getColllectorStatusRow(i).getPk();
            String colSeriesStatusSeriesStatus = colSeriesStatusTable.getColllectorStatusRow(i).getStatus();
            if (colSeriesStatusSeriesStatus.equals("1")){
                //collector_series_status op 2 zetten (selector bezig)
                colSeriesStatusTable.getColllectorStatusRow(i).setStatus("2");                
                WriteToIqcDatabase.UpdateSeriesStatus(iqcDBParams, colSeriesStatusSeriesFk, "2");
                //controleren op een match van de rij uit series met selector_series;
                //Geeft alle primary keys van selector_series die gelijk zijn aan de serie (uitzondering pk, availability en series_status)
                ArrayList<String> selectorSeriesPkMatch = CompareRows.getMatchingSelectorSeriesPkList(iqcDBParams, colSeriesStatusSeriesFk);
                if (!selectorSeriesPkMatch.isEmpty()){
                    //testBoolean is true aangezien er een match is
                    //testBoolean = true;
                    //Voor alle series die een match hebben verder gaan
                    for (int j=0;j<selectorSeriesPkMatch.size();j++){
                        String selSeriesPk = selectorSeriesPkMatch.get(j);
                        ArrayList<String> selectorStudyFkList = CollectorReadDatabase.getSelectorStudyFkBySelectorSeriesFk(iqcDBParams, selSeriesPk); 
                        ArrayList<String> selPkList = CollectorReadDatabase.getSelectorPkBySelectorSeriesFkFromSelector(iqcDBParams, selSeriesPk);
                        ArrayList<String> inputFilePkList = CollectorReadDatabase.getInputfileFkBySelectorSeriesFkFromSelector(iqcDBParams, selSeriesPk);
                        if (!selectorStudyFkList.isEmpty()){
                            for (int n=0;n<selectorStudyFkList.size();n++){
                                //read analyselevel from inputfile
                                String inputFileName = ReadFromIqcDatabase.getFilenameFromTable(iqcDBParams, "inputfile", inputFilePkList.get(n));
                                XmlInputFile xmlinputFile = new XmlInputFile();
                                xmlinputFile.read(inputFileName);
                                if (selectorStudyFkList.get(n)== null && (!xmlinputFile.getAnalyseLevel().equalsIgnoreCase("study"))){
                                    //Geen selector_study bij deze selector_serie
                                    //TODO als blijkt uit inputFile dat analyseLevel=STUDY dan nog iets doen. indien analyseLevel=SERIES dan verwerken
                                    WriteGewensteProcessen.writeDataSeries(iqcDBParams, selPkList.get(n),colSeriesStatusSeriesFk);
                                    WriteToIqcDatabase.UpdateSeriesStatus(iqcDBParams, colSeriesStatusSeriesFk, "3");
                                    colSeriesStatusTable.getColllectorStatusRow(i).setStatus("3");
                                    
                                } else {
                                    //study_status controleren van studyFk bij deze colSeriesStatusSeriesFk                            
                                    String studyFk = ReadFromIqcDatabase.getStudyFkBySeriesFkFromSeries(iqcDBParams, colSeriesStatusSeriesFk);
                                    //get studyStatus from colStudyStatusTable with studyFk
                                    String studyStatus = colStudyStatusTable.getStatusWithFk(studyFk);
                                    if (studyStatus.equals("0")){
                                        //studyStatus = 0 dus nog niet verwerkt door collector, reset serieStatus
                                        colSeriesStatusTable.getColllectorStatusRow(i).setStatus("1");                
                                        WriteToIqcDatabase.UpdateSeriesStatus(iqcDBParams, colSeriesStatusSeriesFk, "1");
                                    } else if (studyStatus.equals("1")){
                                        //controleren of de rij van selector_study past (selector_study_fk uit selector)
                                        for (int k=0;k<selectorStudyFkList.size();k++){
                                            String selStudyFk = selectorStudyFkList.get(k);
                                            Boolean match = CompareRows.isMatch(iqcDBParams, studyFk, selStudyFk);
                                            if (match || xmlinputFile.getAnalyseLevel().equalsIgnoreCase("study")){                                        
                                                WriteToIqcDatabase.UpdateStudyStatus(iqcDBParams, studyFk, "2");
                                                colStudyStatusTable.getRowWithFk(studyFk).setStatus("2");
                                                ArrayList<String> seriesPkList = ReadFromIqcDatabase.getSeriesPkListWithStudyFk(iqcDBParams, studyFk);
                                                //TODO Uitvogelen
                                                for (int l=0;l<seriesPkList.size();l++){
                                                    colSeriesStatusTable.getRowWithFk(seriesPkList.get(l)).setStatus("2");                
                                                    WriteToIqcDatabase.UpdateSeriesStatus(iqcDBParams, seriesPkList.get(l), "2");
                                                }
                                                //TODO gewenste_processen vullen voor deze study
                                                WriteGewensteProcessen.writeDataStudy(iqcDBParams, selPkList.get(k),studyFk);
                                                WriteToIqcDatabase.UpdateStudyStatus(iqcDBParams, studyFk, "3");
                                                colStudyStatusTable.getRowWithFk(studyFk).setStatus("3");
                                                for (int l=0;l<seriesPkList.size();l++){
                                                    colSeriesStatusTable.getRowWithFk(seriesPkList.get(l)).setStatus("3");                
                                                    WriteToIqcDatabase.UpdateSeriesStatus(iqcDBParams, seriesPkList.get(l), "3");
                                                }
                                            } else {
                                                //Wordt niet voldaan aan vereist studie voorwaarden dus niet verwerken door processor                                        
                                                //Tenzij er sprake is van een serie selectie waarbij volgens inputfile op study niveau geanalyseerd moet worden.
                                                WriteToIqcDatabase.UpdateSeriesStatus(iqcDBParams, colSeriesStatusSeriesFk, "3");
                                                colSeriesStatusTable.getColllectorStatusRow(i).setStatus("3");
                                            }
                                        }
                                    } else if (studyStatus.equals("3")){
                                        //De serie heeft een studievoorwaarde in selector staan. Maar de studie is al verwerkt door selector, dus collector serie status updaten
                                        //Dit komt voor indien meerdere series in een studie voldoen aan selector_serie en de verwerking al heeft plaatsgevonden.
                                        //anders worden er per serie nieuwe opdrachten voor processor gegeven.
                                        colSeriesStatusTable.getColllectorStatusRow(i).setStatus("3");                
                                        WriteToIqcDatabase.UpdateSeriesStatus(iqcDBParams, colSeriesStatusSeriesFk, "3");
                                    }
                                }
                            }
                            
                        } else {
                            //TODO gewenste_processen vullen voor deze serie
                            //Is er wel een selector ingevuld met deze selector_serie?                           
                            //Deze serie heeft geen enkele match in de selector (dus wel een match in selector_serie maar deze wordt niet gebruikt in selector)
                            //controelren van inputfile analyse level
                            WriteToIqcDatabase.UpdateSeriesStatus(iqcDBParams, colSeriesStatusSeriesFk, "3");
                            //colSeriesStatusTable.getColllectorStatusRow(i).setStatus("3");
                        }
                    }
                    //Tijdens testen moeten de verwerkte series weer op 1 gezet worden zodat ze opnieuw gecontroleerd worden.
                    //TODO verwijderen 'reset' collector_series_status
                    //colSeriesStatusTable.getColllectorStatusRow(i).setStatus("1");                
                    //WriteToIqcDatabase.UpdateSeriesStatus(iqcDBParams, colSeriesStatusSeriesFk, "1");
                } else {
                    //Serie heeft geen match dus status op 3 zetten 
                    //colSeriesStatusTable.getColllectorStatusRow(i).setStatus("3");
                    WriteToIqcDatabase.UpdateSeriesStatus(iqcDBParams, colSeriesStatusSeriesFk, "3");
                }
                //Tijdens programmeren en testen status steeds terug zetten op 1;                                
                //WriteToIqcDatabase.UpdateSeriesStatus(iqcDBParams, colSeriesStatusSeriesFk, "1");
            }
        }
        //Starten met controleren op studie
        for (int i=0;i<colStudyStatusTable.size();i++){
            String colStudyStatusStudyFk = colStudyStatusTable.getColllectorStatusRow(i).getFk();
            String colStudyStatusPk = colStudyStatusTable.getColllectorStatusRow(i).getPk();
            String colStudyStatusStudyStatus = colStudyStatusTable.getColllectorStatusRow(i).getStatus();
            if (colStudyStatusStudyStatus.equals("1")){
                WriteToIqcDatabase.UpdateStudyStatus(iqcDBParams, colStudyStatusStudyFk, "2");
                colStudyStatusTable.getColllectorStatusRow(i).setStatus("2");
                ArrayList<String> selectorStudyPkMatch = CompareRows.getMatchingSelectorStudyPkList(iqcDBParams, colStudyStatusStudyFk);
                if (!selectorStudyPkMatch.isEmpty()){
                    for (int j=0;j<selectorStudyPkMatch.size();j++){
                        //Controleren of er een series vergelijk gekoppeld is aan deze studie vergelijk.
                        String selStudyPk = selectorStudyPkMatch.get(j);
                        ArrayList<String> selSeriesFkList = CollectorReadDatabase.getSelectorSeriesFkBySelectorStudyFkFromSelector(iqcDBParams, selStudyPk);
                        ArrayList<String> selPkList = CollectorReadDatabase.getSelectorPkBySelectorStudyFkFromSelector(iqcDBParams, selStudyPk);
                        //series bij deze studie controleren op collector_status
                        ArrayList<String> seriesFkList = ReadFromIqcDatabase.getSeriesPkListWithStudyFk(iqcDBParams, colStudyStatusStudyFk);
                        Boolean allSeriesReady = true;
                        for (int l=0;l<seriesFkList.size();l++){
                            String colSeriesStatus = colSeriesStatusTable.getStatusWithFk(seriesFkList.get(l));
                            if (colSeriesStatus.equals("1")){
                                allSeriesReady = false;
                            } 
                        }
                        if (allSeriesReady){
                            for (int k=0;k<selSeriesFkList.size();k++){
                                if (selSeriesFkList.get(k)==null){
                                    //TODO gewenste_processen vullen
                                    WriteGewensteProcessen.writeDataStudy(iqcDBParams, selPkList.get(k),colStudyStatusStudyFk);
                                }                                 
                            }
                            WriteToIqcDatabase.UpdateStudyStatus(iqcDBParams, colStudyStatusStudyFk, "3");
                            colStudyStatusTable.getColllectorStatusRow(i).setStatus("3");                            
                        } else {
                            WriteToIqcDatabase.UpdateStudyStatus(iqcDBParams, colStudyStatusStudyFk, "1");
                            colStudyStatusTable.getColllectorStatusRow(i).setStatus("1");
                        }
                    }
                } else {
                    WriteToIqcDatabase.UpdateStudyStatus(iqcDBParams, colStudyStatusStudyFk, "3");
                    //colStudyStatusTable.getColllectorStatusRow(i).setStatus("3");
                }
            }
        }
        String dummy = "Stop tijdens testen";
    }
    
}
