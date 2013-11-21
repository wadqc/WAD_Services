/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_collector;

import java.sql.Connection;
import java.util.ArrayList;
import wad.db.ReadFromIqcDatabase;
import wad.db.ReadFromPacsDatabase;
import wad.db.WriteToIqcDatabase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import wad.db.DatabaseParameters;
import wad.db.ReadFromDatabases;
import wad.xml.ReadConfigXML;

/**
 *
 * @author Ralph Berendsen <>
 */
public class CheckNewStudy {
    
    private static Log log = LogFactory.getLog(CheckNewStudy.class);
            
    public void CheckNewStudy(Connection pacsConnection, Connection iqcConnection){
        log.debug("CheckNewStudy");
        
        try {
        
        //LoggerWrapper.myLogger.log(Level.FINEST, "{0}", "CheckNewStudy");
        //Ophalen PACSDB parameters uit config.xml
        //DatabaseParameters pacsDBParams = new DatabaseParameters();
        //pacsDBParams = ReadConfigXML.ReadPacsDBParameters(pacsDBParams);
        //Lezen van studie tabel in PACSDB
        ArrayList<String> pacsStudyIUID = ReadFromPacsDatabase.getStudies(pacsConnection);
        
        //Ophalen iqc parameters uit config.xml
        //DatabaseParameters iqcDBParams = new DatabaseParameters();
        //iqcDBParams = ReadConfigXML.ReadIqcDBParameters(iqcDBParams);
        //Lezen van studie tabel in iqcDB
        ArrayList<String> iqcStudyIUID = ReadFromIqcDatabase.getStudies(iqcConnection);
        
        //Bestaat de study_iuid niet in iqcDB dan timer starten op controleren van de serie   
//        for (int i=0;i<pacsStudyIUID.size();i++){
//            int j=1;
//            Boolean isEqual = false;
//            if (!iqcStudyIUID.isEmpty()){
//                do{
//                    if (pacsStudyIUID.get(i).equals(iqcStudyIUID.get(j-1))){
//                        isEqual = true;
//                    }
//                    j++;
//                }while((!isEqual)&&(j<(iqcStudyIUID.size()+1))); //isEqual true bestaat study, j>iqcStudyIUID.size en laaste study in lijst is gecontroleerd
//            }
//            if (!isEqual){
//                //Study bestaat niet in iqc dus start controle van series
//                //Alle series bij study_iuid zoeken en deze controleren op status = 0 ipv 1. bij status 1 wordt er verwerkt.
//                //System.out.println("Study_iuid : " + pacsStudyIUID.get(i));
//                CheckSeriesWithStudyIUID.CheckSeries(pacsConnection, iqcConnection, pacsStudyIUID.get(i));                
//            }
//        }
        
        //Nieuw deel om controle op ontbrekende studies te versnellen (voorkomen van een lange iteratie bij groot aantal studies)
        DatabaseParameters iqcDBParams = new DatabaseParameters();
        iqcDBParams = ReadConfigXML.ReadIqcDBParameters(iqcDBParams);
        DatabaseParameters pacsDBParams = new DatabaseParameters();
        pacsDBParams = ReadConfigXML.ReadPacsDBParameters(pacsDBParams);        
        
        ArrayList<String> studyIUID = ReadFromDatabases.getDifferenceOfTwoColomns(iqcConnection, pacsDBParams.databasename, "study", "study_iuid", iqcDBParams.databasename, "study", "study_iuid");
        for (int i=0;i<studyIUID.size();i++){
            CheckSeriesWithStudyIUID.CheckSeries(pacsConnection, iqcConnection, studyIUID.get(i));
        }
        
        //Einde nieuw deel ter controle ontbrekende studies
        
        //Indien een study niet compleet is, dus collector_study_status = 0 dan moet deze opnieuw gecontroleerd worden.
//        for (int i=0;i<iqcStudyIUID.size();i++){
//            String studyFk = ReadFromIqcDatabase.getPkWithUniqueIdentifier(iqcConnection, "study", "study_iuid", iqcStudyIUID.get(i));
//            if (ReadFromIqcDatabase.getCollectorStudyStatusByStudyFk(iqcConnection, studyFk).equals("0")){
//                //System.out.println("Collector_study_status : 0");
//                //System.out.println("Study_iuid : " + iqcStudyIUID.get(i));
//                CheckSeriesWithStudyIUID.CheckSeries(pacsConnection, iqcConnection, iqcStudyIUID.get(i));
//            }            
//        }   
        
        //Nieuw deel om controle op studyIUID's met een collector_study_status 0 sneller te vinden
        ArrayList<String> studyIUIDStatus0 = ReadFromIqcDatabase.getStudyIUIDWithCollectorStudyStatus(iqcConnection, "0");
        for (int i=0;i<studyIUIDStatus0.size();i++){
            CheckSeriesWithStudyIUID.CheckSeries(pacsConnection, iqcConnection, studyIUIDStatus0.get(i));
        }    
        
        //Einde nieuw deel controle studyIUID's met status 0
        
        //Indien een serie later wordt toegevoegd aan de studie die al als compleet is gemarkeerd dient de controle voor betreffende studie opnieuw te lopen.
        //Hoe hiermee om te gaan, stel Analyse is al gedaan, moet deze dan opnieuw?
        
        //Lezen serietabel uitpacsdb
        //ArrayList<String> pacsSeriesIUID = ReadFromPacsDatabase.getSeries(pacsConnection);
        //controleren of series_pk bestaat in iqcdb, zo niet dan toevoegen als status=0 en toevoegen aan collector_series_status met status = 1
        
        //Nieuw deel om de controle van ontbrekende series te versnellen (voorkomen van een lange iteratie bij groot aantal series)
        ArrayList<String> seriesIUID = ReadFromDatabases.getDifferenceOfTwoColomns(iqcConnection, pacsDBParams.databasename, "series", "series_iuid", iqcDBParams.databasename, "series", "series_iuid");
        for (int i=0;i<seriesIUID.size();i++){            
                if (ReadFromPacsDatabase.getSeriesStatusWithSeriesIUID(pacsConnection, seriesIUID.get(i)).equals("0")){
                    //System.out.println("Series_iuid : " + pacsSeriesIUID.get(i));
                    WriteToIqcDatabase.WriteResulsetInTable(iqcConnection, ReadFromPacsDatabase.getSeriesResultSetWithSeriesIUID(pacsConnection, seriesIUID.get(i)), "series");
                    String seriesFk = ReadFromPacsDatabase.getPkWithUniqueIdentifier(pacsConnection, "series", "series_iuid", seriesIUID.get(i));
                    //Instances en filesystem kopieren
                    ArrayList<String> instancePk = ReadFromPacsDatabase.getInstancePkWithSeriesIUID(pacsConnection, seriesIUID.get(i)); 
                    for (int j=0;j<instancePk.size();j++){
                        // Eerst filesystem kopieren dan pas de instances ivm restricties/relaties database 
                        //Filesystem kopieren
                        //controleren of filesystem bestaat, zo niet dan kopieren                        
                        String filePk = ReadFromPacsDatabase.getFilesPkWithInstancePk(pacsConnection, instancePk.get(j));
                        String filesystemFk = ReadFromPacsDatabase.getFilesystemPkWithFilesPk(pacsConnection, filePk);
                        if (!ReadFromIqcDatabase.rowExistsByPrimaryKey(iqcConnection, "filesystem", filesystemFk)){
                            WriteToIqcDatabase.WriteResulsetInTable(iqcConnection, ReadFromPacsDatabase.getTableResultSetWithPrimaryKey(pacsConnection, "filesystem", filesystemFk), "filesystem");
                        }
                        // Instance kopieren
                        WriteToIqcDatabase.WriteResulsetInTable(iqcConnection, ReadFromPacsDatabase.getTableResultSetWithPrimaryKey(pacsConnection, "instance", instancePk.get(j)), "instance");
                        //WriteToIqcDatabase.CreateRowInGewensteProcessen(iqcDBParams, "instance", instancePk.get(j), "100");
                        //Files kopieren
                        WriteToIqcDatabase.WriteResulsetInTable(iqcConnection, ReadFromPacsDatabase.getTableResultSetWithForeignKey(pacsConnection, "files","instance_fk" ,instancePk.get(j)), "files");
                    }
                    WriteToIqcDatabase.WriteSeriesStatus(iqcConnection, seriesFk, "1");
                    //System.out.println("Series status : 1");
                }            
        }
        ///Einde nieuwe deel ter controle ontbrekende series
        
//        for (int i=0;i<pacsSeriesIUID.size();i++){
//            if (!ReadFromIqcDatabase.seriesExistsBySeriesIUID(iqcConnection, pacsSeriesIUID.get(i))){
//                if (ReadFromPacsDatabase.getSeriesStatusWithSeriesIUID(pacsConnection, pacsSeriesIUID.get(i)).equals("0")){
//                    //System.out.println("Series_iuid : " + pacsSeriesIUID.get(i));
//                    WriteToIqcDatabase.WriteResulsetInTable(iqcConnection, ReadFromPacsDatabase.getSeriesResultSetWithSeriesIUID(pacsConnection, pacsSeriesIUID.get(i)), "series");
//                    String seriesFk = ReadFromPacsDatabase.getPkWithUniqueIdentifier(pacsConnection, "series", "series_iuid", pacsSeriesIUID.get(i));
//                    //Instances kopieren
//                    ArrayList<String> instancePk = ReadFromPacsDatabase.getInstancePkWithSeriesIUID(pacsConnection, pacsSeriesIUID.get(i)); 
//                    for (int j=0;j<instancePk.size();j++){
//                        WriteToIqcDatabase.WriteResulsetInTable(iqcConnection, ReadFromPacsDatabase.getTableResultSetWithPrimaryKey(pacsConnection, "instance", instancePk.get(j)), "instance");
//                        //WriteToIqcDatabase.CreateRowInGewensteProcessen(iqcDBParams, "instance", instancePk.get(j), "100");
//                        //Files kopieren
//                        WriteToIqcDatabase.WriteResulsetInTable(iqcConnection, ReadFromPacsDatabase.getTableResultSetWithForeignKey(pacsConnection, "files","instance_fk" ,instancePk.get(j)), "files");
//                        //Filesystem kopieren
//                        //controleren of filesystem bestaat, zo niet dan kopieren                        
//                        String filePk = ReadFromPacsDatabase.getFilesPkWithInstancePk(pacsConnection, instancePk.get(j));
//                        String filesystemFk = ReadFromPacsDatabase.getFilesystemPkWithFilesPk(pacsConnection, filePk);
//                        if (!ReadFromIqcDatabase.rowExistsByPrimaryKey(iqcConnection, "filesystem", filesystemFk)){
//                            WriteToIqcDatabase.WriteResulsetInTable(iqcConnection, ReadFromPacsDatabase.getTableResultSetWithPrimaryKey(pacsConnection, "filesystem", filesystemFk), "filesystem");
//                        }
//                    }
//                    WriteToIqcDatabase.WriteSeriesStatus(iqcConnection, seriesFk, "1");
//                    //System.out.println("Series status : 1");
//                }
//            }
//        }
        
        //Controle op verwijderde studies!!!
        //Als een studie verwijderd wordt hoeft alleen de verwijzing van filepath in fiels aangepast te worden
        // ** TODO **
        // VUmc - Joost Kuijer - (tijdelijk) uitgeschakeld ivm performance problemen:
        // - duurt ca 2500 / min bij grote aantallen beelden
        // - meer dan standaard geheugen nodig (ca 1024m bij 10000 beelden)
        //int count = UpdateFiles.start(iqcConnection, pacsConnection);
        //if (count>0){            
        //    LoggerWrapper.myLogger.log(Level.FINEST, "Files updated : {0}", Integer.toString(count));            
        //}
        
        } catch (NullPointerException ex) {
            log.error("database error: "+ex);
        }
    }
}
