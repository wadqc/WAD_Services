/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

/**
 *
 * @author Ralph Berendsen 
 */
public class AnalyseModuleResultFile {
    private ArrayList<Object> resultArray;
    private File analayseModuleResultFile;
    Document dom;
    
    private static Log log = LogFactory.getLog(AnalyseModuleResultFile.class);
    
//    public AnalyseModuleInputFile(Patient patient, AnalyseModuleInputValues analyseModuleInputValues){
//        this.patient = patient;        
//        this.version = analyseModuleInputValues.getVersion();
//        this.analyseModuleCfg = analyseModuleInputValues.getModuleConfig();
//        this.analyseModuleOutput = analyseModuleInputValues.getModuleOutput();
//        this.analyselevel = analyseModuleInputValues.getAnalyseLevel();
//        createDocument();
//    }
    
    public AnalyseModuleResultFile(String filename){
        this.analayseModuleResultFile = new File(filename);
        this.resultArray = new ArrayList<Object>(); 
        createDocument();
    }
    
    private void write(String filename){
        this.analayseModuleResultFile = new File(filename);
        createDOMTree();
        printToFile();
    }
    
    public void write(){
        if (this.analayseModuleResultFile!=null){
            createDOMTree();        
            printToFile();
        }        
    }
    
    private void read(String filename){
        this.analayseModuleResultFile = new File(filename);
        parseXmlFile(this.analayseModuleResultFile);        
        parseDocument();
    }
    
    public boolean read(){
        if (this.analayseModuleResultFile!=null){
            parseXmlFile(this.analayseModuleResultFile);        
            try{
                parseDocument();
                return true;
            } catch(Exception ex){                
                log.error(ex);
                return false;
            }
        }
        return false;
    }
    
    
    private void createDocument(){
        //get an instance of factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	try {
            //get an instance of builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //create an instance of DOM
            dom = db.newDocument();
	}catch(ParserConfigurationException pce) {
            //dump it
            System.out.println("Error while trying to instantiate DocumentBuilder " + pce);
            System.exit(1);
	}
    }
    
    private void createDOMTree(){
        //create the root element
        Element rootElement = dom.createElement("WAD");
        dom.appendChild(rootElement);
        
                
        //Loops uitvoeren over patient
        for (int i=0;i<this.resultArray.size();i++){
            Element resultElement = createResultElement(i);
            if (resultElement!=null){
                rootElement.appendChild(resultElement);
            }
        }             
    }
    
    private void printToFile(){
        try {
            DOMImplementation domImpl = dom.getImplementation();
            DOMImplementationLS impl = (DOMImplementationLS) domImpl;            
            
            LSSerializer serializer = impl.createLSSerializer();
            serializer.getDomConfig().setParameter("format-pretty-print", true);
            LSOutput output = impl.createLSOutput();
            output.setEncoding("UTF-8");
            output.setByteStream(new FileOutputStream(analayseModuleResultFile));
            serializer.write(dom, output);
            
        } catch(FileNotFoundException fnf){
          System.out.println(fnf);  
        }
    }
    
    private Element createResultElement(int index){        
        
        Object result = resultArray.get(index);
        Class<?> c = result.getClass();
        String test = c.getName();
        
            if (c.getName().contains("Boolean")){
                ResultsBoolean r = (ResultsBoolean) result;    
                Element e = createBooleanElement(r);
                return e;
            } else if (c.getName().contains("Char")){
                ResultsChar r = (ResultsChar) result;    
                Element e = createCharElement(r);
                return e;
            } else if (c.getName().contains("Float")){
                ResultsFloat r = (ResultsFloat) result;    
                Element e = createFloatElement(r);
                return e;
            } else if (c.getName().contains("Object")){
                ResultsObject r = (ResultsObject) result;    
                Element e = createObjectElement(r);
                return e;
            }
            
        
        
        return null;
    }
    
    private Element createCharElement(ResultsChar result){
        Element resultElement = dom.createElement("results");       
        
        //create study uid and description text node and attach it to resultElement
        Element typeElement = dom.createElement("type");
        Text typeText = dom.createTextNode(result.getType());
        typeElement.appendChild(typeText);
        resultElement.appendChild(typeElement);
        
        Element niveauElement = dom.createElement("niveau");
        Text niveauText = dom.createTextNode(result.getNiveau());
        niveauElement.appendChild(niveauText);
        resultElement.appendChild(niveauElement);
        
//        Element datetimeElement = dom.createElement("datetime");
//        Text datetimeText = dom.createTextNode(result.getDatetime());
//        datetimeElement.appendChild(datetimeText);
//        resultElement.appendChild(datetimeElement);
        
        Element omschrijvingElement = dom.createElement("omschrijving");
        Text omschrijvingText = dom.createTextNode(result.getOmschrijving());
        omschrijvingElement.appendChild(omschrijvingText);
        resultElement.appendChild(omschrijvingElement);
        
        Element volgnummerElement = dom.createElement("volgnummer");
        Text volgnummerText = dom.createTextNode(result.getVolgnummer());
        volgnummerElement.appendChild(volgnummerText);
        resultElement.appendChild(volgnummerElement);
        
        Element waardeElement = dom.createElement("waarde");
        Text waardeText = dom.createTextNode(result.getWaarde());
        waardeElement.appendChild(waardeText);
        resultElement.appendChild(waardeElement);        
        
        Element criteriumElement = dom.createElement("criterium");
        Text criteriumText = dom.createTextNode(result.getCriterium());
        criteriumElement.appendChild(criteriumText);
        resultElement.appendChild(criteriumElement);        
        return resultElement;
    }
    
    private Element createBooleanElement(ResultsBoolean result){
        Element resultElement = dom.createElement("results");       
        
        //create study uid and description text node and attach it to resultElement
        Element typeElement = dom.createElement("type");
        Text typeText = dom.createTextNode(result.getType());
        typeElement.appendChild(typeText);
        resultElement.appendChild(typeElement);
        
        Element niveauElement = dom.createElement("niveau");
        Text niveauText = dom.createTextNode(result.getNiveau());
        niveauElement.appendChild(niveauText);
        resultElement.appendChild(niveauElement);
        
//        Element datetimeElement = dom.createElement("datetime");
//        Text datetimeText = dom.createTextNode(result.getDatetime());
//        datetimeElement.appendChild(datetimeText);
//        resultElement.appendChild(datetimeElement);
        
        Element omschrijvingElement = dom.createElement("omschrijving");
        Text omschrijvingText = dom.createTextNode(result.getOmschrijving());
        omschrijvingElement.appendChild(omschrijvingText);
        resultElement.appendChild(omschrijvingElement);
        
        Element volgnummerElement = dom.createElement("volgnummer");
        Text volgnummerText = dom.createTextNode(result.getVolgnummer());
        volgnummerElement.appendChild(volgnummerText);
        resultElement.appendChild(volgnummerElement);
        
        Element waardeElement = dom.createElement("waarde");
        Text waardeText = dom.createTextNode(result.getWaarde());
        waardeElement.appendChild(waardeText);
        resultElement.appendChild(waardeElement);        
        
        return resultElement;
    }
    
    private Element createFloatElement(ResultsFloat result){
        Element resultElement = dom.createElement("results");       
        
        //create study uid and description text node and attach it to resultElement
        Element typeElement = dom.createElement("type");
        Text typeText = dom.createTextNode(result.getType());
        typeElement.appendChild(typeText);
        resultElement.appendChild(typeElement);
        
        Element omschrijvingElement = dom.createElement("omschrijving");
        Text omschrijvingText = dom.createTextNode(result.getOmschrijving());
        omschrijvingElement.appendChild(omschrijvingText);
        resultElement.appendChild(omschrijvingElement);
        
        Element volgnummerElement = dom.createElement("volgnummer");
        Text volgnummerText = dom.createTextNode(result.getVolgnummer());
        volgnummerElement.appendChild(volgnummerText);
        resultElement.appendChild(volgnummerElement);
        
        Element niveauElement = dom.createElement("niveau");
        Text niveauText = dom.createTextNode(result.getNiveau());
        niveauElement.appendChild(niveauText);
        resultElement.appendChild(niveauElement);
        
//        Element datetimeElement = dom.createElement("datetime");
//        Text datetimeText = dom.createTextNode(result.getDatetime());
//        datetimeElement.appendChild(datetimeText);
//        resultElement.appendChild(datetimeElement);
        
        Element grootheidElement = dom.createElement("grootheid");
        Text grootheidText = dom.createTextNode(result.getGrootheid());
        grootheidElement.appendChild(grootheidText);
        resultElement.appendChild(grootheidElement);
        
        Element eenheidElement = dom.createElement("eenheid");
        Text eenheidText = dom.createTextNode(result.getEenheid());
        eenheidElement.appendChild(eenheidText);
        resultElement.appendChild(eenheidElement);
        
        Element waardeElement = dom.createElement("waarde");
        Text waardeText = dom.createTextNode(result.getWaarde());
        waardeElement.appendChild(waardeText);
        resultElement.appendChild(waardeElement); 
        
        Element grensKritischOnderElement = dom.createElement("grens_kritisch_onder");
        Text grensKritischOnderText = dom.createTextNode(result.getGrensKritischOnder());
        grensKritischOnderElement.appendChild(grensKritischOnderText);
        resultElement.appendChild(grensKritischOnderElement);
        
        Element grensKritischBovenElement = dom.createElement("grens_kritisch_boven");
        Text grensKritischBovenText = dom.createTextNode(result.getGrensKritischBoven());
        grensKritischBovenElement.appendChild(grensKritischBovenText);
        resultElement.appendChild(grensKritischBovenElement);
        
        Element grensAcceptabelOnderElement = dom.createElement("grens_acceptabel_onder");
        Text grensAcceptabelOnderText = dom.createTextNode(result.getGrensAcceptabelOnder());
        grensAcceptabelOnderElement.appendChild(grensAcceptabelOnderText);
        resultElement.appendChild(grensAcceptabelOnderElement);
        
        Element grensAcceptabelBovenElement = dom.createElement("grens_acceptabel_boven");
        Text grensAcceptabelBovenText = dom.createTextNode(result.getGrensAcceptabelBoven());
        grensAcceptabelBovenElement.appendChild(grensAcceptabelBovenText);
        resultElement.appendChild(grensAcceptabelBovenElement);
        
        return resultElement;
    }
    
    private Element createObjectElement(ResultsObject result){
        Element resultElement = dom.createElement("results");       
        
        //create study uid and description text node and attach it to resultElement
        Element typeElement = dom.createElement("type");
        Text typeText = dom.createTextNode(result.getType());
        typeElement.appendChild(typeText);
        resultElement.appendChild(typeElement);
        
        Element omschrijvingElement = dom.createElement("omschrijving");
        Text omschrijvingText = dom.createTextNode(result.getOmschrijving());
        omschrijvingElement.appendChild(omschrijvingText);
        resultElement.appendChild(omschrijvingElement);
        
        Element volgnummerElement = dom.createElement("volgnummer");
        Text volgnummerText = dom.createTextNode(result.getVolgnummer());
        volgnummerElement.appendChild(volgnummerText);
        resultElement.appendChild(volgnummerElement);
        
        Element niveauElement = dom.createElement("niveau");
        Text niveauText = dom.createTextNode(result.getNiveau());
        niveauElement.appendChild(niveauText);
        resultElement.appendChild(niveauElement);
        
//        Element datetimeElement = dom.createElement("datetime");
//        Text datetimeText = dom.createTextNode(result.getDatetime());
//        datetimeElement.appendChild(datetimeText);
//        resultElement.appendChild(datetimeElement);
        
        Element objectNaamPadElement = dom.createElement("object_naam_pad");
        Text objectNaamPadText = dom.createTextNode(result.getObjectnaampad());
        objectNaamPadElement.appendChild(objectNaamPadText);
        resultElement.appendChild(objectNaamPadElement);
        
        return resultElement;
    }
    
    private void parseXmlFile(File inputFile){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        
        try{
            DocumentBuilder db = dbf.newDocumentBuilder();
            
            dom = db.parse(inputFile);
            
        } catch (SAXException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{AnalyseModuleResultFile.class.getName(), ex});
            log.error(ex);
        } catch (IOException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{AnalyseModuleResultFile.class.getName(), ex});
            log.error(ex);
        } catch (ParserConfigurationException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{AnalyseModuleResultFile.class.getName(), ex});
            log.error(ex);
        }    
    }
    
    private void parseDocument(){
        //get the root element
        Element docElement = dom.getDocumentElement();        
        resultArray.clear();                
        //get a nodelist of <patient>

        NodeList nlResults = docElement.getElementsByTagName("results");
        if (nlResults != null && nlResults.getLength()>0) {
            for (int i = 0 ; i < nlResults.getLength();i++) {
                Element elResults = (Element)nlResults.item(i);
                String type = getTextValue(elResults, "type");
                if (type.equals("char")){
                    ResultsChar result = new ResultsChar();
                    result.setType(type);
                    //result.setDatetime(getTextValue(elResults, "datetime"));
                    result.setNiveau(getTextValue(elResults, "niveau"));
                    result.setOmschrijving(getTextValue(elResults, "omschrijving"));
                    result.setVolgnummer(getTextValue(elResults, "volgnummer"));
                    result.setWaarde(getTextValue(elResults, "waarde"));                    
                    result.setCriterium(getTextValue(elResults, "criterium"));                    
                    resultArray.add(result);
                } else if (type.equals("boolean")){
                    ResultsBoolean result = new ResultsBoolean();
                    result.setType(type);
                    //result.setDatetime(getTextValue(elResults, "datetime"));
                    result.setNiveau(getTextValue(elResults, "niveau"));
                    result.setOmschrijving(getTextValue(elResults, "omschrijving"));
                    result.setVolgnummer(getTextValue(elResults, "volgnummer"));
                    result.setWaarde(getTextValue(elResults, "waarde"));                    
                    resultArray.add(result);
                } else if (type.equals("float")){
                    ResultsFloat result = new ResultsFloat();
                    result.setType(type);
                    result.setOmschrijving(getTextValue(elResults, "omschrijving"));
                    result.setVolgnummer(getTextValue(elResults, "volgnummer"));
                    //result.setDatetime(getTextValue(elResults, "datetime"));
                    result.setNiveau(getTextValue(elResults, "niveau"));
                    result.setGrootheid(getTextValue(elResults, "grootheid"));
                    result.setEenheid(getTextValue(elResults, "eenheid"));
                    result.setGrensKritischOnder(getTextValue(elResults, "grens_kritisch_onder"));
                    result.setGrensKritischBoven(getTextValue(elResults, "grens_kritisch_boven"));
                    result.setGrensAcceptabelOnder(getTextValue(elResults, "grens_acceptabel_onder"));
                    result.setGrensAcceptabelBoven(getTextValue(elResults, "grens_acceptabel_boven"));
                    result.setWaarde(getTextValue(elResults, "waarde"));                    
                    resultArray.add(result);
                } else if (type.equals("object")){
                    ResultsObject result = new ResultsObject();
                    result.setType(type);
                    result.setOmschrijving(getTextValue(elResults, "omschrijving"));
                    result.setVolgnummer(getTextValue(elResults, "volgnummer"));
                    //result.setDatetime(getTextValue(elResults, "datetime"));
                    result.setNiveau(getTextValue(elResults, "niveau"));
                    result.setObjectnaampad(getTextValue(elResults, "object_naam_pad"));                   
                    resultArray.add(result);
                }                 
            }
        }
    }
    
    private String getTextValue(Element ele, String tagName) {
		String textVal = "";
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			try {
                            textVal = el.getFirstChild().getNodeValue();
                        } catch (NullPointerException e){
                            return "";
                        }
		}

		return textVal;
	}
    
        
    public void add(Object obj){
        this.resultArray.add(obj);
    }
    
    public void addAll(ArrayList<Object> list){
        this.resultArray.addAll(list);
    }
    
    public Object getResult(int index){
        return this.resultArray.get(index);
    }
    
    public int getResultSize(){
        return this.resultArray.size();
    }
}
