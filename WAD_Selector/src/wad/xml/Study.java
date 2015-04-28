/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.xml;

import java.util.ArrayList;

/**
 *
 * @author Ralph Berendsen 
 */
public class Study {
    private String uid;
    private String description;
    private ArrayList<Series> seriesList;
    
    public Study(String uid, String description, ArrayList<Series> seriesList){
        this.uid=uid;
        this.description=description;
        this.seriesList=seriesList;
    }
    
    public Study(String uid, String description){
        this.uid=uid;
        this.description=description;
        this.seriesList=new ArrayList<Series>();
    }
    
    public Study(){
        this.uid=null;
        this.description=null;
        this.seriesList=new ArrayList<Series>();
    }
    
    public String getUid() {
		return uid;
	}

    public void setUid(String uid) {
		this.uid = uid;
	}
    
    public String getDescription() {
		return description;
	}

    public void setDescription(String description) {
		this.description = description;
	}
    
    public ArrayList<Series> getSeriesList() {
		return seriesList;
	}

    public void setSeriesList(ArrayList<Series> seriesList) {
		this.seriesList = seriesList;
	}
    
    public Series getSeries(int index) {
		return seriesList.get(index);
	}

    public void addSeries(Series series) {
		this.seriesList.add(series);
	}
    
    public void addSeries(int index, Series series) {
		this.seriesList.add(index, series);
	}
}
