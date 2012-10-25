/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_selector;

import java.util.Timer;
import wad.xml.ReadConfigXML;

/**
 *
 * @author Ralph Berendsen <>
 */
public class WAD_Selector {
    
    Timer studyCheckTimer;    
    
    private WAD_Selector(){ 
        studyCheckTimer = new Timer();
        Integer interval = Integer.parseInt(ReadConfigXML.readSettingsElement("timer"));
        studyCheckTimer.schedule(new SelectorTimer(),0, interval);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        WAD_Selector client = new WAD_Selector();
    }
}
