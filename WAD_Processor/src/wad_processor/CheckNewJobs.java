/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_processor;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.logging.Level;
import wad.db.DatabaseParameters;
import wad.db.GewensteProces;
import wad.db.PacsDatabaseConnection;
import wad.db.ReadFromIqcDatabase;
import wad.db.WriteResultaten;
import wad.logger.LoggerWrapper;
import wad.xml.AnalyseModuleResultFile;
import wad.xml.ReadConfigXML;

/**
 *
 * @author titulaer
 */
public class CheckNewJobs extends TimerTask {

    public int aantalConcurrentJobs = 4;
    ArrayList<WAD_ProcessThread> processList = new ArrayList<WAD_ProcessThread>();
    int processID = 0;
    //Teller om een aantal loops te laten doorlopen voordat afgesloten wordt.
    //Dit bouw ik in om te voorkomen dat bij het starten de applicatie niet oneindig blijft lopen, maar egstopt kan worden via een setting in de config.xml
    //De teller wordt op nul gezet indien er processen lopen, bij 5 keer doorlopen zonder active processen gaat de aplpicatie uit.
    public int teller = 0;

    public void run() {
        LoggerWrapper loggerWrapper = LoggerWrapper.getInstance();
        LoggerWrapper.myLogger.log(Level.INFO, "Begin cyclus processor.");
        teller++;
        //RB inlezen db parameters
        DatabaseParameters iqcDBParams = new DatabaseParameters();
        iqcDBParams = ReadConfigXML.ReadIqcDBParameters(iqcDBParams);
        Connection dbConnection = PacsDatabaseConnection.conDb(iqcDBParams);
        //Rb Einde inlezen db parameters
        Iterator<WAD_ProcessThread> i = null;
        WAD_ProcessThread rp = null;
        for (i = processList.iterator(); i.hasNext();) {
            teller = 0;
            rp = i.next();
            if (!rp.isRunning()) {
                LoggerWrapper.myLogger.log(Level.INFO, "{0}", "thread " + rp.ID() + " stopped, removing from currentJobList");
                // RB BT update process met rp.ID() stopped
                GewensteProces gp = new GewensteProces();
                gp.getGewensteProcesByKey(dbConnection, Integer.toString(rp.ID()));
                if (!rp.stoppedWithError()) {
                    // aanpassen bij absoluut filepath voor XML in config.xml
                    String output = ReadConfigXML.readFileElement("XML")+ReadFromIqcDatabase.getFilenameFromTable(dbConnection, "analysemodule_output", gp.getOutputKey());
//                    String currentDir = System.getProperty("user.dir");
//                    File dir = new File(currentDir);
//                    String mainDir = dir.getParent();
//                    output = output.replace("..", mainDir);
                    output = output.replace("/", "\\");
                    gp.updateStatus(dbConnection, 3);
                    AnalyseModuleResultFile resultFile = new AnalyseModuleResultFile(output);
                    resultFile.read();
                    gp.updateStatus(dbConnection, 4);
                    WriteResultaten writeResultaten = new WriteResultaten(dbConnection, resultFile, Integer.parseInt(gp.getKey()));
                    gp.updateStatus(dbConnection, 5);
                } else {
                    gp.updateStatus(dbConnection, 10);
                }
                
                i.remove();
            }
        }
        if (processList.size() < aantalConcurrentJobs) {
            //RB check op nieuw te starten processen met processID, zo ja dan verder
            //Pak het eertse item uit de tabel gewenste_processen
            GewensteProces gp = new GewensteProces();
            if (gp.getFirstGewensteProcesByStatus(dbConnection, 0)) {
                //Zet het gewenste process op status 1 (Gestart)
                gp.updateStatus(dbConnection, 1);
                //Einde code RB
                WAD_ProcessThread p = new WAD_ProcessThread();
                try {
                    teller = 0;
                    //RB update process met id op start zetten 

                    String[] command;
                    //command = new String[2];
//                command[0] = "C:\\WINDOWS\\notepad.exe";
//                command[1] = "C:\\temp\\test" + counter + ".txt";

                    String anaModuleFile = ReadFromIqcDatabase.getAnalyseModuleBySelectorFk(dbConnection, gp.getSelectorKey());
                    //String anaModuleCfgFile = ReadFromIqcDatabase.getAnalyseModuleCfgBySelectorFk(dbConnection, gp.getSelectorKey());

                    //aanpassen bij absoluut filepath voor XML in config.xml
                    String input = ReadConfigXML.readFileElement("XML")+ReadFromIqcDatabase.getFilenameFromTable(dbConnection, "analysemodule_input", gp.getInputKey());

//                    String currentDir = System.getProperty("user.dir");
//                    File dir = new File(currentDir);
//                    String mainDir = dir.getParent();
//                    input = input.replace("..", mainDir);
                    input = input.replace("/", "\\");

                    //controle op type extensie, zodat m-files, java-files etc de juiste commandline meekrijgen
                    //command[0] = "java -jar "+anaModuleFile + " \"" + input +"\"";
                    //command[1] = anaModuleFile + " " + input;
                    if (matchExtension(anaModuleFile, "jar")) {
                        command = new String[4];
                        command[0] = "java";
                        command[1] = "-jar";
                        command[2] = anaModuleFile;
                        command[3] = "\"" + input + "\"";
                    } else {
                        command = new String[2];
                        command[0] = anaModuleFile;
                        command[1] = "\"" + input + "\"";
                    }


                    //Gegevens zijn bekend, nu starten van de analysemodule met input als argument
                    gp.updateStatus(dbConnection, 2);

                    this.processID = Integer.parseInt(gp.getKey());
                    //
                    p.newProcessThread(command, processID);
                    processList.add(processList.size(), p);
                    //zorgen dat service kan herstarten zonder afsluiten dochterprocessen
                    p.setDaemon(false);
                    p.start();
                    //counter += 1;
                    LoggerWrapper.myLogger.log(Level.INFO, "{0}", "Process id: " + processID);
                } catch (Exception e) {
                    processList.remove(p);
                    LoggerWrapper.myLogger.log(Level.INFO, "{0}", "Problem starting task: " + e.getMessage());
                    //Zet het gewenste process op status 10 (Error)
                    gp.updateStatus(dbConnection, 10);
                }
            }
        } else {
            LoggerWrapper.myLogger.log(Level.INFO, "{0}", "Cannot add process, current number of processes: " + processList.size());
        }
        //RB sluiten van de DBconnectie
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        LoggerWrapper.myLogger.log(Level.INFO, "Einde cyclus processor.");
        //RB
        //RB Sluiten applicatie voor testdoeleinden, gebruik setting in config.xml
        if (ReadConfigXML.readSettingsElement("stop").equals("1")) {
            if (teller == 5) {
                //System.out.println(datum+" "+ tijd + ":Afsluiten.");
                LoggerWrapper.myLogger.log(Level.INFO, "Afsluiten processor.");
                this.cancel();
                System.exit(0);
            }
        }
        //RB Einde afsluiten.
    }

    private boolean matchExtension(String filename, String ext) {
        int dot = filename.lastIndexOf(".");
        if (filename.substring(dot + 1).equals(ext)) {
            return true;
        } else {
            return false;
        }

    }
}
