/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.xml;
    
//import com.sun.org.apache.xml.internal.serialize.OutputFormat;
//import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;
import wad.logger.LoggerWrapper;
/**
 *
 * @author Ralph Berendsen <>
 */
public class AnalyseModuleInputFile {
    private String version;
    private String analyseModuleCfg;
    private String analyseModuleOutput;
    private String analyselevel;
    private Patient patient;
    private File analayseModuleInputFile;
    Document dom;
    
    public AnalyseModuleInputFile(Patient patient, AnalyseModuleInputValues analyseModuleInputValues){
        this.patient = patient;        
        this.version = analyseModuleInputValues.getVersion();
        this.analyseModuleCfg = analyseModuleInputValues.getModuleConfig();
        this.analyseModuleOutput = analyseModuleInputValues.getModuleOutput();
        this.analyselevel = analyseModuleInputValues.getAnalyseLevel();
        createDocument();
    }
    
    public void write(String filename){
        this.analayseModuleInputFile = new File(filename);
        createDOMTree();
        analayseModuleInputFile.getParentFile().mkdirs();
        printToFile();
    }
    
    public void read(String filename){
        this.analayseModuleInputFile = new File(filename);
        parseXmlFile(this.analayseModuleInputFile);
        this.patient = new Patient();
        parseDocument();
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
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1} {2}", new Object[]{AnalyseModuleInputFile.class.getName(), pce, "Error while trying to instantiate DocumentBuilder "});
            System.exit(1);
	}
    }
    
    private void createDOMTree(){
        //create the root element
        Element rootElement = dom.createElement("WAD");
        dom.appendChild(rootElement);
        
        //create version text node and attach it to rootElement
        Element versionElement = dom.createElement("version");
        Text versionText = dom.createTextNode(this.version);
        versionElement.appendChild(versionText);
        rootElement.appendChild(versionElement);
        
        //create analysemodule_cfg text node and attach it to rootElement
        Element modConfigElement = dom.createElement("analysemodule_cfg");
        Text modConfigText = dom.createTextNode(this.analyseModuleCfg);
        modConfigElement.appendChild(modConfigText);
        rootElement.appendChild(modConfigElement);
        
        //create analysemodule_cfg text node and attach it to rootElement
        Element modOutputElement = dom.createElement("analysemodule_output");
        Text modOutputText = dom.createTextNode(this.analyseModuleOutput);
        modOutputElement.appendChild(modOutputText);
        rootElement.appendChild(modOutputElement);
        
        //create analyselvl text node and attach it to rootElement
        Element analyseLevelElement = dom.createElement("analyselevel");
        Text analyseLevelText = dom.createTextNode(this.analyselevel);
        analyseLevelElement.appendChild(analyseLevelText);
        rootElement.appendChild(analyseLevelElement);
        
        //Loops uitvoeren over patient
        Element patientElement = createPatientElement();
        rootElement.appendChild(patientElement);        
    }
    
    private void printToFile(){
        try {
            DOMImplementation domImpl = dom.getImplementation();
            DOMImplementationLS impl = (DOMImplementationLS) domImpl;            
            
            LSSerializer serializer = impl.createLSSerializer();
            serializer.getDomConfig().setParameter("format-pretty-print", true);
            LSOutput output = impl.createLSOutput();
            output.setEncoding("UTF-8");
            output.setByteStream(new FileOutputStream(analayseModuleInputFile));
            serializer.write(dom, output);
            
        } catch(FileNotFoundException fnf){
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1} {2}", new Object[]{AnalyseModuleInputFile.class.getName(), fnf, "File not found."});            
        }
    }
    
    private Element createPatientElement(){
        Element patientElement = dom.createElement("patient");
        
        //create patientID and name text node and attach it to patientElement
        Element patientIdElement = dom.createElement("id");
        Text patientIdText = dom.createTextNode(this.patient.getId());
        patientIdElement.appendChild(patientIdText);
        patientElement.appendChild(patientIdElement);
        
        Element nameElement = dom.createElement("name");
        Text nameText = dom.createTextNode(this.patient.getName());
        nameElement.appendChild(nameText);
        patientElement.appendChild(nameElement);
        
        //create study element
        Element studyElement = createStudyElement();
        
        patientElement.appendChild(studyElement);
        
        return patientElement;
    }
    
    private Element createStudyElement(){
        Element studyElement = dom.createElement("study");
        Study study = this.patient.getStudy();
        
        //create study uid and description text node and attach it to studyElement
        Element studyUidElement = dom.createElement("uid");
        Text studyUidText = dom.createTextNode(study.getUid());
        studyUidElement.appendChild(studyUidText);
        studyElement.appendChild(studyUidElement);
        
        Element descElement = dom.createElement("description");
        Text descText = dom.createTextNode(study.getDescription());
        descElement.appendChild(descText);
        studyElement.appendChild(descElement);
        
        
        //create series element
        for (int i=0;i<study.getSeriesList().size();i++){
            Element seriesElement = createSeriesElement(study.getSeries(i));
            studyElement.appendChild(seriesElement);
        }
        return studyElement;
    }
    
    private Element createSeriesElement(Series series){
        Element seriesElement = dom.createElement("series");
        
        //create series number and description text node and attach it to seriesElement
        Element seriesNoElement = dom.createElement("number");
        Text seriesNoText = dom.createTextNode(series.getNumber());
        seriesNoElement.appendChild(seriesNoText);
        seriesElement.appendChild(seriesNoElement);
        
        Element descElement = dom.createElement("description");
        Text descText = dom.createTextNode(series.getDescription());
        descElement.appendChild(descText);
        seriesElement.appendChild(descElement);
        
        //create instance element
        for (int i=0;i<series.getInstanceList().size();i++){
            Element instanceElement = createInstanceElement(series.getInstance(i));
            seriesElement.appendChild(instanceElement);
        }
        return seriesElement;
    }
    
    private Element createInstanceElement(Instance instance){
        Element instanceElement = dom.createElement("instance");
        
        //create instance number and filename text node and attach it to instanceElement
        Element instNoElement = dom.createElement("number");
        Text instNoText = dom.createTextNode(instance.getNumber());
        instNoElement.appendChild(instNoText);
        instanceElement.appendChild(instNoElement);
        
        Element filenameElement = dom.createElement("filename");
        Text filenameText = dom.createTextNode(instance.getFilename());
        filenameElement.appendChild(filenameText);
        instanceElement.appendChild(filenameElement);
        
        return instanceElement;
    }
    
    private void parseXmlFile(File inputFile){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        
        try{
            DocumentBuilder db = dbf.newDocumentBuilder();
            
            dom = db.parse(inputFile);
            
        } catch (SAXException ex) {
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{AnalyseModuleInputFile.class.getName(), ex});
        } catch (IOException ex) {
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{AnalyseModuleInputFile.class.getName(), ex});
        } catch (ParserConfigurationException ex) {
            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{AnalyseModuleInputFile.class.getName(), ex});
        }    
    }
    
    private void parseDocument(){
        //get the root element
        Element docElement = dom.getDocumentElement();        
        
        version = getTextValue(docElement, "version");
        analyseModuleCfg = getTextValue(docElement, "analysemodule_cfg");
        analyseModuleOutput = getTextValue(docElement, "analysemodule_output");
        analyselevel = getTextValue(docElement, "analyselevel");
        
        //get a nodelist of <patient>
        NodeList nlPatient = docElement.getElementsByTagName("patient");
        if (nlPatient != null && nlPatient.getLength()>0) {
            for (int i = 0 ; i < nlPatient.getLength();i++) {
                Element elPatient = (Element)nlPatient.item(i);
                this.patient.setId(getTextValue(elPatient, "id"));
                this.patient.setName(getTextValue(elPatient, "name"));
                
                NodeList nlStudy = elPatient.getElementsByTagName("study");
                if (nlStudy != null && nlStudy.getLength()>0) {
                    for (int j = 0 ; j < nlStudy.getLength();j++) {
                        Element elStudy = (Element)nlStudy.item(j);
                        this.patient.getStudy().setUid(getTextValue(elStudy, "uid"));
                        this.patient.getStudy().setDescription(getTextValue(elStudy, "description"));
                        
                        NodeList nlSeries = elStudy.getElementsByTagName("series");
                        if (nlSeries != null && nlSeries.getLength()>0) {
                            for (int k = 0 ; k < nlSeries.getLength();k++) {
                                Element elSeries = (Element)nlSeries.item(k);                                
                                this.patient.getStudy().addSeries(new Series(getTextValue(elSeries, "number"), getTextValue(elSeries, "description")));
                                //this.patient.getStudy().getSeries(k).setNumber(getTextValue(elSeries, "number"));
                                //this.patient.getStudy().getSeries(k).setDescription(getTextValue(elSeries, "description"));
                                
                                NodeList nlInstance = elSeries.getElementsByTagName("instance");
                                if (nlSeries != null && nlInstance.getLength()>0) {
                                    for (int l = 0 ; l < nlInstance.getLength();l++) {
                                        Element elInstance = (Element)nlInstance.item(l);
                                        this.patient.getStudy().getSeries(k).addInstance(new Instance(getTextValue(elInstance, "number"), getTextValue(elInstance, "filename")));
                                        //this.patient.getStudy().getSeries(k).getInstance(l).setNumber((getTextValue(elInstance, "number")));
                                        //this.patient.getStudy().getSeries(k).getInstance(l).setFilename(getTextValue(elInstance, "filename"));                                
                                    }
                                }
                            }
                        }
                    }
                }
//                version = getTextValue(el, "version");
//                moduleconfig = getTextValue(el, "moduleconfig");
//                analyselevel = getTextValue(el, "analyselevel");
            }
        }
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
    
    public void setVersion(String version){
        this.version = version;
    }
    
    public String getModuleConfig(){
        return analyseModuleCfg;
    }
    
    public void setModuleConfig(String moduleconfig){
        this.analyseModuleCfg = moduleconfig;
    }
    
    public String getAnalyseLevel(){
        return analyselevel;
    }
    
    public void setAnalyseLevel(String analyselevel){
        this.analyselevel = analyselevel;
    }
}
