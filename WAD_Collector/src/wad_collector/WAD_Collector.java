/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_collector;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Timer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import wad.xml.ReadConfigXML;

/**
 *
 * @author Ralph Berendsen <>
 */
public class WAD_Collector {
    Timer studyCheckTimer;    
    private static Log log = LogFactory.getLog(WAD_Collector.class);
    private static Properties logProperties = new Properties();
    
    private WAD_Collector(){
        studyCheckTimer = new Timer();
        Integer interval = Integer.parseInt(ReadConfigXML.readSettingsElement("timer"));
        studyCheckTimer.schedule(new CollectorTimer(),0, interval);
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // gebruik config/log4j.properties indien aanwezig
        // wanneer niet gevonden wordt de log4j.properties gebruikt uit de src folder (komt in jar-file terecht)
        try {
            logProperties.load(new FileInputStream("config/log4j.properties"));
            PropertyConfigurator.configure(logProperties);
        } catch (IOException ex) {
            log.error(ex);
        }
        
        WAD_Collector client = new WAD_Collector();        
    }
}
