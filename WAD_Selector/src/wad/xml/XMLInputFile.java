/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.xml;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import wad.logger.LoggerWrapper;

/**
 *
 * @author Ralph Berendsen <>
 */
public class XMLInputFile {
    
    private String version;
    private String analyseModuleCfg;
    private String analyselevel;
    Document dom;
    
    public XMLInputFile(){
        version = "";
        analyseModuleCfg = "";
        analyselevel = "";
    }
    
    public void read(String input){
        File inputFile = new File(input);
        parseXmlFile(inputFile);
        parseDocument();
    }
    
    private void parseXmlFile(File inputFile){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        
        try{
            DocumentBuilder db = dbf.newDocumentBuilder();
            
            dom = db.parse(inputFile);
            
        }catch (ParserConfigurationException pce) {
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{XMLInputFile.class.getName(), pce});
        }catch (SAXException se) {
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{XMLInputFile.class.getName(), se});
        }catch (IOException ioe){
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{XMLInputFile.class.getName(), ioe});
        }    
    }
    
    private void parseDocumentNl(){
        //get the root element
        Element docElement = dom.getDocumentElement();
        
        //get a nodelist of <version>
        NodeList nl = docElement.getElementsByTagName("general");
        if (nl != null && nl.getLength()>0) {
            for (int i = 0 ; i < nl.getLength();i++) {
                Element el = (Element)nl.item(i);
                version = getTextValue(el, "version");
                analyseModuleCfg = getTextValue(el, "moduleconfig");
                analyselevel = getTextValue(el, "analyselevel");
            }
        }
    }
    
    private void parseDocument(){
        //get the root element
        Element docElement = dom.getDocumentElement();
        version = getTextValue(docElement, "version");
        analyseModuleCfg = getTextValue(docElement, "moduleconfig");
        analyselevel = getTextValue(docElement, "analyselevel");
        
        //er zitten geen nodelists in dit document
        
    }
    
    private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}
    
    public String getVersion(){
        return version;
    }
    
    public String getModuleConfig(){
        return analyseModuleCfg;
    }
    
    public String getAnalyseLevel(){
        return analyselevel;
    }

}
