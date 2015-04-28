/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.xml;

/**
 *
 * @author Ralph Berendsen 
 */
public class Instance {
    private String number;
    private String filename;
    
    public Instance(String number, String filename){
        this.number=number;
        this.filename=filename;
    }
    
    public Instance(){
        this.number=null;
        this.filename=null;
    }
    
    public String getNumber() {
		return number;
	}

    public void setNumber(String number) {
		this.number = number;
	}
    
    public String getFilename() {
		return filename;
	}

    public void setFilename(String filename) {
		this.filename = filename;
	}    
    
}
