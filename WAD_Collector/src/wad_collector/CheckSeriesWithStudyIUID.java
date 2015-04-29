/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_collector;
import java.sql.Connection;
import java.util.ArrayList;
import wad.db.DatabaseParameters;
import wad.db.ReadFromIqcDatabase;
import wad.db.ReadFromPacsDatabase;
import wad.db.WriteToIqcDatabase;
import wad.xml.ReadConfigXML;
/**
 *
 * @author Ralph Berendsen 
 */
public class CheckSeriesWithStudyIUID {
        
    public static void CheckSeries(Connection pacsConnection, Connection iqcConnection, String pacsStudyIUID) {
        //Lezen van studie tabel in PACSDB
        ArrayList<String> pacsSeriesIUID = ReadFromPacsDatabase.getSeriesWithStudyIUID(pacsConnection,pacsStudyIUID);
        
        //Voor elke serie controleren of deze status 0 heeft, als 0 dan is deze gereed in pacsDB en kan verwerkt worden
        Boolean studyComplete = true;
        for (int i=0;i<pacsSeriesIUID.size();i++){
            //System.out.println("Series_iuid : " + pacsSeriesIUID.get(i));
            if (ReadFromPacsDatabase.getSeriesStatusWithSeriesIUID(pacsConnection, pacsSeriesIUID.get(i)).equals("0")){               
               //write seriesinformation to the iqc database if it does not exist there already. 
                if (ReadFromIqcDatabase.seriesExistsBySeriesIUID(iqcConnection, pacsSeriesIUID.get(i))){
                    //serie bestaat al dus is al verwerkt
                    
                } else {                
                    // serie bestaat niet dus verwerken
                    // VUmc - Joost Kuijer - volgorde kopieren gewijzigd om relaties in de database mogelijk te maken,
                    // dan moet eerst de patient gemaakt zijn, voordat zijn studies gekopieerd kunnen worden.
                    // Eerst patient overhalen
                    String patientFk = ReadFromPacsDatabase.getForeignKeyWithUniqueIdentifier(pacsConnection, "study", "patient_fk", "study_iuid", pacsStudyIUID);
                    if (!ReadFromIqcDatabase.rowExistsByPrimaryKey(iqcConnection,"patient", patientFk)){
                        WriteToIqcDatabase.WriteResulsetInTable(iqcConnection, ReadFromPacsDatabase.getTableResultSetWithPrimaryKey(pacsConnection, "patient", patientFk), "patient");
                    }
                    //study overhalen                    
                    String studyFk = ReadFromPacsDatabase.getPkWithUniqueIdentifier(pacsConnection, "study", "study_iuid", pacsStudyIUID);                    
                    if (!ReadFromIqcDatabase.rowExistsByPrimaryKey(iqcConnection,"study", studyFk)){
                        WriteToIqcDatabase.WriteResulsetInTable(iqcConnection, ReadFromPacsDatabase.getStudyResultSetWithStudyIUID(pacsConnection, pacsStudyIUID), "study");
                        //study status 0 vermelden in collector_study_status tabel
                        WriteToIqcDatabase.WriteStudyStatus(iqcConnection, studyFk, "0");
                    }
                    //serie kopieren
                    WriteToIqcDatabase.WriteResulsetInTable(iqcConnection,ReadFromPacsDatabase.getSeriesResultSetWithSeriesIUID(pacsConnection, pacsSeriesIUID.get(i)),"series");
                    
                    //Instances en filesystem kopieren
                    ArrayList<String> instancePk = ReadFromPacsDatabase.getInstancePkWithSeriesIUID(pacsConnection, pacsSeriesIUID.get(i)); 
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
                    //Serie is toegevoegd aan iqcDB nu nog markeren voor de selector middels cllector_series_status = 1
                    String seriesFk = ReadFromPacsDatabase.getPkWithUniqueIdentifier(pacsConnection, "series", "series_iuid", pacsSeriesIUID.get(i));                    
                    WriteToIqcDatabase.WriteSeriesStatus(iqcConnection, seriesFk, "1");
                    //System.out.println("Series status : 1");
                }                
            } else {
                //PACSDB series_status is 1 dus wachten en opnieuw controleren
                //Nieuwe controle start nadat de hoofd timer opnieuw gestart is CheckNewStudy
                studyComplete = false;
                String studyFk = ReadFromPacsDatabase.getPkWithUniqueIdentifier(pacsConnection, "study", "study_iuid", pacsStudyIUID);                    
                //study status 1 vermelden in collector_study_status tabel                
                WriteToIqcDatabase.UpdateStudyStatus(iqcConnection, studyFk, "0");
                //System.out.println("Study status : 0");
            }
        }
        //Controleren of study compleet is, dan collector_study_status bijwerken naar 1
        if (studyComplete){
            String studyFk = ReadFromIqcDatabase.getPkWithUniqueIdentifier(iqcConnection, "study", "study_iuid", pacsStudyIUID);            
            WriteToIqcDatabase.UpdateStudyStatus(iqcConnection, studyFk, "1");
            //System.out.println("Study status : 1");
            
        }
    }
}
