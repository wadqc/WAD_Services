/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_collector;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;
import wad.db.DatabaseParameters;
import wad.db.PacsDatabaseConnection;
import wad.xml.ReadConfigXML;

/**
 *
 * @author Ralph Berendsen <>
 */
public class CollectorTimer extends TimerTask{

    @Override
    public void run() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date date = new Date();
        String datum = dateFormat.format(date).substring(6, 8)+ "-" +
                        dateFormat.format(date).substring(4, 6)+ "-" +
                        dateFormat.format(date).substring(0, 4);
        String tijd = dateFormat.format(date).substring(8, 10)+ ":" +
                        dateFormat.format(date).substring(10, 12)+ ":" +
                        dateFormat.format(date).substring(12, 14)+ "." +
                        dateFormat.format(date).substring(14);
                        
        System.out.println(datum+" "+ tijd + ": Start CollectorTimer");
        
        DatabaseParameters iqcDBParams = new DatabaseParameters();
        iqcDBParams = ReadConfigXML.ReadIqcDBParameters(iqcDBParams);
        DatabaseParameters pacsDBParams = new DatabaseParameters();
        pacsDBParams = ReadConfigXML.ReadPacsDBParameters(pacsDBParams); 
        //Maak een databaseConnection en gebruik deze als variable om door te geven
        //Maken van nieuwe connections voor elke nieuwe query loopt tegen limitaties bij
        //zeer groot aantal queries. Op dat moment worden oude connecties, die nog greserveerd zijn
        //hergebruikt, dit kan niet en eindigd in een JDBC-driver fout.
        
        Connection iqcConnection = PacsDatabaseConnection.conDb(iqcDBParams);
        Connection pacsConnection = PacsDatabaseConnection.conDb(pacsDBParams);
        
        
        CheckNewStudy checkNewStudy = new CheckNewStudy();
        checkNewStudy.CheckNewStudy(pacsConnection, iqcConnection);
        
        PacsDatabaseConnection.closeDb(iqcConnection, null, null);
        PacsDatabaseConnection.closeDb(pacsConnection, null, null);
        date = new Date();
        datum = dateFormat.format(date).substring(6, 8)+ "-" +
                dateFormat.format(date).substring(4, 6)+ "-" +
                dateFormat.format(date).substring(0, 4);
        tijd = dateFormat.format(date).substring(8, 10)+ ":" +
               dateFormat.format(date).substring(10, 12)+ ":" +
               dateFormat.format(date).substring(12, 14)+ "." +
               dateFormat.format(date).substring(14);
                 
        System.out.println(datum+" "+ tijd + ": Einde CollectorTimer");
        if (ReadConfigXML.readSettingsElement("stop").equals("1")){
            date = new Date();
            datum = dateFormat.format(date).substring(6, 8)+ "-" +
                    dateFormat.format(date).substring(4, 6)+ "-" +
                    dateFormat.format(date).substring(0, 4);
            tijd = dateFormat.format(date).substring(8, 10)+ ":" +
                   dateFormat.format(date).substring(10, 12)+ ":" +
                   dateFormat.format(date).substring(12, 14)+ "." +
                   dateFormat.format(date).substring(14);
                 
        System.out.println(datum+" "+ tijd + ":Afsluiten.");
            this.cancel();
            System.exit(0);
        } 
    }
    
}
