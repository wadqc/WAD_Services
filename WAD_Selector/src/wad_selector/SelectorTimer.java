/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_selector;

import java.util.ArrayList;
import java.util.TimerTask;
import selector.SelectorSummary;
import wad.xml.ReadConfigXML;
import wad.db.*;
import wad.xml.XmlInputFile;


/**
 *
 * @author Ralph Berendsen <>
 */
public class SelectorTimer extends TimerTask{

    @Override
    public void run() {
        //Testen van XmlInputFile
//        XmlInputFile inputFile = new XmlInputFile();
//        inputFile.read("C:/apps/BVT/WAD_Software/XML/XMLinput/MRI_Joost_input.xml");
//        String analyseLevel = inputFile.getAnalyseLevel();
//        
        //Testen patient voor wegschrijven analysefile        
        //GetPatientFromIqcDatabase getPatient = new GetPatientFromIqcDatabase("17", "study") ;
//        GetPatientFromIqcDatabase getPatient = new GetPatientFromIqcDatabase("51", "series") ;
//        Patient patient = getPatient.getPatient();
//        
//        XmlAnalyseFile xmlAnalyseFile = new XmlAnalyseFile(patient, inputFile);
//        xmlAnalyseFile.write("C:/apps/BVT/WAD_Software/XML/XMLanalyse/MRI_Joost_analyse2.xml");
//        
//        XmlAnalyseFile xmlAnalyseFile2 = new XmlAnalyseFile(patient, inputFile);
//        xmlAnalyseFile2.read("C:/apps/BVT/WAD_Software/XML/XMLanalyse/MRI_Joost_analyse2.xml");
                
        DatabaseParameters iqcDBParams = new DatabaseParameters();
        iqcDBParams = ReadConfigXML.ReadIqcDBParameters(iqcDBParams);
        
        //testen nieuwe selector code
        SelectorSummary selectorSummary = new SelectorSummary();
        selectorSummary.Selector(iqcDBParams);
        
        String dummy = "Stop tijdens testen";
    }
    
}
