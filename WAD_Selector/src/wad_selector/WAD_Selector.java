/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_selector;

import java.util.Timer;

/**
 *
 * @author Ralph Berendsen <>
 */
public class WAD_Selector {
    
    Timer studyCheckTimer;    
    
    private WAD_Selector(){ 
        studyCheckTimer = new Timer();
        studyCheckTimer.schedule(new SelectorTimer(),0, 30000);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        WAD_Selector client = new WAD_Selector();
    }
}
