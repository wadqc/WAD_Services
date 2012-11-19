/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_selector;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;
import java.util.logging.Level;
import selector.Selector;
import wad.xml.ReadConfigXML;
import wad.db.*;
import wad.logger.LoggerWrapper;

/**
 *
 * @author Ralph Berendsen <>
 */
public class SelectorTimer extends TimerTask{

    @Override
    public void run() {
        LoggerWrapper loggerWrapper = LoggerWrapper.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date date = new Date();
        String datum = dateFormat.format(date).substring(6, 8)+ "-" +
                        dateFormat.format(date).substring(4, 6)+ "-" +
                        dateFormat.format(date).substring(0, 4);
        String tijd = dateFormat.format(date).substring(8, 10)+ ":" +
                        dateFormat.format(date).substring(10, 12)+ ":" +
                        dateFormat.format(date).substring(12, 14)+ "." +
                        dateFormat.format(date).substring(14);
                        
        System.out.println(datum+" "+ tijd + ":Start SelectorTimer");
        LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}: Start SelectorTimer", new Object[]{datum, tijd});
        
        DatabaseParameters iqcDBParams = new DatabaseParameters();
        iqcDBParams = ReadConfigXML.ReadIqcDBParameters(iqcDBParams);
        
        //Maak een databaseConnection en gebruik deze als variable om door te geven
        //Maken van nieuwe connections voor elke nieuwe query loopt tegen limitaties bij
        //zeer groot aantal queries. Op dat moment worden oude connecties, die nog greserveerd zijn
        //hergebruikt, dit kan niet en eindigd in een JDBC-driver fout.
        
        Connection dbConnection = PacsDatabaseConnection.conDb(iqcDBParams);
        
        //testen nieuwste selector code
        Selector selector = new Selector();
        selector.Selector(dbConnection);
        
        //Sluiten van de databaseConenction nadat de selector één keer doorlopen is.
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        
        //Te gebruiken voor een linebreakpoint in Netbeans
        String dummy = "Stop tijdens testen";        
        
        date = new Date();
        datum = dateFormat.format(date).substring(6, 8)+ "-" +
                dateFormat.format(date).substring(4, 6)+ "-" +
                dateFormat.format(date).substring(0, 4);
        tijd = dateFormat.format(date).substring(8, 10)+ ":" +
               dateFormat.format(date).substring(10, 12)+ ":" +
               dateFormat.format(date).substring(12, 14)+ "." +
               dateFormat.format(date).substring(14);
                 
        System.out.println(datum+" "+ tijd + ":Einde SelectorTimer");
        LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}: Einde SelectorTimer", new Object[]{datum, tijd});
        if (ReadConfigXML.readSettingsElement("stop").equals("1")){
            date = new Date();
            datum = dateFormat.format(date).substring(6, 8)+ "-" +
                    dateFormat.format(date).substring(4, 6)+ "-" +
                    dateFormat.format(date).substring(0, 4);
            tijd = dateFormat.format(date).substring(8, 10)+ ":" +
                   dateFormat.format(date).substring(10, 12)+ ":" +
                   dateFormat.format(date).substring(12, 14)+ "." +
                   dateFormat.format(date).substring(14);
                 
            System.out.println(datum+" "+ tijd + ":Afsluiten");
            LoggerWrapper.myLogger.log(Level.INFO, "{0} {1}: Afsluiten SelectorTimer", new Object[]{datum, tijd});
            this.cancel();
        }
    }
    
}
