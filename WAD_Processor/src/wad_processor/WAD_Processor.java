/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_processor;

import java.util.Timer; 
import wad.xml.ReadConfigXML;

/**
 *
 * @author titulaer
 */
public class WAD_Processor {
Timer processListTimer;

    
    private WAD_Processor(){
        processListTimer = new Timer();
        CheckNewJobs c = new CheckNewJobs();
        //c.aantalConcurrentJobs=2;
        //Lezen van de inetrval timer setting uit config.xml
        Integer interval = Integer.parseInt(ReadConfigXML.readSettingsElement("timer"));
        //lezen van aantal jobs uit de config.xml
        c.aantalConcurrentJobs = Integer.parseInt(ReadConfigXML.readSettingsElement("jobs"));
        processListTimer.schedule(c,0, interval);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        WAD_Processor client =  new WAD_Processor(); 
    }
    
}
