/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_collector;

import java.util.Timer;
import wad.xml.ReadConfigXML;

/**
 *
 * @author Ralph Berendsen <>
 */
public class WAD_Collector {
    Timer studyCheckTimer;    
    
    private WAD_Collector(){
        studyCheckTimer = new Timer();
        Integer interval = Integer.parseInt(ReadConfigXML.readSettingsElement("timer"));
        studyCheckTimer.schedule(new CollectorTimer(),0, interval);
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        WAD_Collector client = new WAD_Collector();        
    }
}
