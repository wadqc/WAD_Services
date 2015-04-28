/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.xml;

/**
 *
 * @author Ralph Berendsen 
 */
public class ResultsFloat {
    private String type;
    private String omschrijving;
    private String volgnummer;
    private String niveau;
//    private String datetime;
    private String grootheid;
    private String eenheid;
    private String waarde;
    private String grenskritischonder;
    private String grenskritischboven;
    private String grensacceptabelonder;
    private String grensacceptabelboven;
    
    public ResultsFloat(){
        this.type ="float";
        this.omschrijving="";
        this.volgnummer="";
        this.niveau="";
//        this.datetime="";
        this.grootheid="";
        this.eenheid="";
        this.waarde="";
        this.grenskritischonder="";
        this.grenskritischboven="";
        this.grensacceptabelonder="";
        this.grensacceptabelboven="";
    }
    
    public String getNiveau(){
        return this.niveau;
    }
    
//    public String getDatetime(){
//        return this.datetime;
//    }
    
    public String getGrootheid(){
        return this.grootheid;
    }
    
    public String getEenheid(){
        return this.eenheid;
    }
    
    public String getWaarde(){
        return this.waarde;
    }
    
    public String getGrensKritischOnder(){
        return this.grenskritischonder;
    }
    
    public String getGrensKritischBoven(){
        return this.grenskritischboven;
    }
    
    public String getGrensAcceptabelOnder(){
        return this.grensacceptabelonder;
    }
    
    public String getGrensAcceptabelBoven(){
        return this.grensacceptabelboven;
    }
    
    public void setNiveau(String niveau){
        this.niveau = niveau;
    }
    
//    public void setDatetime(String datetime){
//        this.datetime = datetime;
//    }
    
    public void setGrootheid(String grootheid){
        this.grootheid = grootheid;
    }
    
    public void setEenheid(String eenheid){
        this.eenheid = eenheid;
    }
    
    public void setWaarde(String waarde){
        this.waarde = waarde;
    }
    
    public void setGrensKritischOnder(String grenskritischonder){
        this.grenskritischonder = grenskritischonder;
    }
    
    public void setGrensKritischBoven(String waarde){
        this.grenskritischboven = waarde;
    }
    
    public void setGrensAcceptabelOnder(String grensacceptabelonder){
        this.grensacceptabelonder = grensacceptabelonder;
    }
    
    public void setGrensAcceptabelBoven(String grensacceptabelboven){
        this.grensacceptabelboven = grensacceptabelboven;
    }
    
    public void setType(String type){
        this.type = type;
    }
    
    public String getType(){
        return this.type;
    }
    
    public void setVolgnummer(String volgnummer){
        this.volgnummer = volgnummer;
    }
    
    public String getVolgnummer(){
        return this.volgnummer;
    }
    
    public String getOmschrijving(){
        return this.omschrijving;
    }
    
    public void setOmschrijving(String omschrijving){
        this.omschrijving = omschrijving;
    }
}
