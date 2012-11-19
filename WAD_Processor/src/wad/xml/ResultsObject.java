/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.xml;

/**
 *
 * @author Ralph Berendsen <>
 */
public class ResultsObject {
    private String type;
    private String omschrijving;
    private String volgnummer;
    private String niveau;
//    private String datetime;
    private String objectnaampad;
    
    
    
    public ResultsObject(){
        this.type ="object";
        this.niveau="";
//        this.datetime="";
        this.objectnaampad="";  
        
    }
    
    public String getNiveau(){
        return this.niveau;
    }
    
//    public String getDatetime(){
//        return this.datetime;
//    }
    
    public String getObjectnaampad(){
        return this.objectnaampad;
    }    
       
    public void setNiveau(String niveau){
        this.niveau = niveau;
    }
    
//    public void setDatetime(String datetime){
//        this.datetime = datetime;
//    }
    
    public void setObjectnaampad(String objectnaampad){
        this.objectnaampad = objectnaampad;
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
