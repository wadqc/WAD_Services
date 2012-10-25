/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_selector;

import java.sql.Connection;
import java.util.TimerTask;
import selector.Selector;
import wad.xml.ReadConfigXML;
import wad.db.*;

/**
 *
 * @author Ralph Berendsen <>
 */
public class SelectorTimer extends TimerTask{

    @Override
    public void run() {
        System.out.println("Start SelectorTimer");
                
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
        
        System.out.println("Einde SelectorTimer");
        
        if (ReadConfigXML.readSettingsElement("stop").equals("1")){
            System.out.println("Afsluiten");        
            this.cancel();
        }
    }
    
}
