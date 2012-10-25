/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.xml;

import java.util.ArrayList;

/**
 *
 * @author Ralph Berendsen <>
 */
public class Series {
    private String number;
    private String description;
    private ArrayList<Instance> instanceList;
    
    public Series(String number, String description, ArrayList<Instance> instanceList){
        this.number=number;
        this.description=description;
        this.instanceList=instanceList;
    }
    
    public Series(String number, String description){
        this.number=number;
        this.description=description;
        this.instanceList=new ArrayList<Instance>();
    }
    
    public Series(){
        this.number=null;
        this.description=null;
        this.instanceList=new ArrayList<Instance>();
    }
    
    public String getNumber() {
		return number;
	}

    public void setNumber(String number) {
		this.number = number;
	}
    
    public String getDescription() {
		return description;
	}

    public void setDescription(String description) {
		this.description = description;
	}
    
    public ArrayList<Instance> getInstanceList() {
		return instanceList;
	}

    public void setInstanceList(ArrayList<Instance> instanceList) {
		this.instanceList = instanceList;
	}
    
    public Instance getInstance(int index) {
		return instanceList.get(index);
	}

    public void addInstance(Instance instance) {
		this.instanceList.add(instance);
	}
    
    public void addInstance(int index, Instance instance) {
		this.instanceList.add(index, instance);
	}
}
