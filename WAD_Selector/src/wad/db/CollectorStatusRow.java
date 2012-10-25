/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.db;

/**
 *
 * @author Ralph Berendsen <>
 */
public class CollectorStatusRow {
    private String pk;
    private String fk;
    private String status;
    
    public CollectorStatusRow(String primaryKey, String foreigKey, String statusValue){
        this.pk = primaryKey;
        this.fk = foreigKey;
        this.status = statusValue;
    }
    
    public CollectorStatusRow(){
        this.pk = "";
        this.fk = "";
        this.status = "";
    }
    
    public String getPk(){
        return this.pk;
    }
    
    public void setPk(String primaryKey){
        this.pk=primaryKey;
    }
    
    public String getFk(){
        return this.fk;
    }
    
    public void setFk(String foreignKey){
        this.fk=foreignKey;
    }
    
    public String getStatus(){
        return this.status;
    }
    
    public void setStatus(String statusValue){
        this.status=statusValue;
    }
}
