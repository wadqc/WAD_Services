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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Ralph Berendsen <>
 */
public class CollectorTimer extends TimerTask{

    private static Log log = LogFactory.getLog(CollectorTimer.class);
    
    @Override
    public void run() {
        //LoggerWrapper loggerWrapper = LoggerWrapper.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
        Date date = new Date();
	String datumtijd = dateFormat.format(date);
                        
        //LoggerWrapper.myLogger.log(Level.INFO, "{0}: Start CollectorTimer", new Object[]{datumtijd});
        log.debug("Start CollectorTimer");
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
	datumtijd = dateFormat.format(date);
                 
        //LoggerWrapper.myLogger.log(Level.INFO, "{0}: Einde CollectorTimer", new Object[]{datumtijd});
        log.debug("Einde CollectorTimer");
        if (ReadConfigXML.readSettingsElement("stop").equals("1")){
            date = new Date();
            datumtijd = dateFormat.format(date);
                 
            //LoggerWrapper.myLogger.log(Level.INFO, "{0}: Afsluiten.", new Object[]{datumtijd});
            log.info("Afsluiten");
            this.cancel();
            System.exit(0);
        } 
    }
    
}
