/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GewensteProcessen;

/**
 *
 * @author Ralph Berendsen <>
 */
public class GPRow {
    private String selectorFk;
    //private String pk;
    private String studyFk;
    private String seriesFk;
    private String instanceFk;
    private String status;
    private String analyseFileFk;
    
    public GPRow(){
        this.selectorFk = "";        
        this.studyFk = "";
        this.seriesFk = "";
        this.instanceFk = "";
        this.status = "";
        this.analyseFileFk = "";
    }
    
    public String getSelectorFk(){
        return this.selectorFk;
    }
    
    public void setSelectorFk(String selectorFk){
        this.selectorFk = selectorFk;
    }
    
    public String getSeriesFk(){
        return this.seriesFk;
    }
    
    public void setSeriesFk(String seriesFk){
        this.seriesFk = seriesFk;
    }
    
    public String getInstanceFk(){
        return this.instanceFk;
    }
    
    public void setInstanceFk(String instanceFk){
        this.instanceFk = instanceFk;
    }
    
    public String getStatus(){
        return this.status;
    }
    
    public void setStatus(String status){
        this.status = status;
    }
    
    public String getStudyFk(){
        return this.studyFk;
    }
    
    public void setStudyFk(String studyFk){
        this.studyFk = studyFk;
    }
    
    public String getAnalyseFileFk(){
        return this.analyseFileFk;
    }
    
    public void setAnalyseFileFk(String analyseFileFk){
        this.analyseFileFk = analyseFileFk;
    }
    
//    public String getPk(){
//        return this.pk;
//    }
    
//    public void setPk(String pk){
//        this.pk = pk;
//    }
    
    
}
