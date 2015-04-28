/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.xml;

/**
 *
 * @author Ralph Berendsen 
 */
public class Patient {
    private String id;
    private String name;
    private Study study;
    
    public Patient(String id, String name, Study study){
        this.id=id;
        this.name=name;
        this.study=study;
    }
    
    public Patient(){
        this.id=null;
        this.name=null;
        this.study=new Study();
    }
    
    public String getId() {
		return id;
	}

    public void setId(String id) {
		this.id = id;
	}
    
    public String getName() {
		return name;
	}

    public void setName(String name) {
		this.name = name;
	}
    
    public Study getStudy() {
		return study;
	}

    public void setStudy(Study study) {
		this.study = study;
	}
}
