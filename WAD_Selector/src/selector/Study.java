/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package selector;

import wad.db.DatabaseParameters;
import wad.db.ReadFromIqcDatabase;

/**
 *
 * @author Ralph Berendsen <>
 */
public class Study {
    private String studyFk;
    private SelectorMatch selectorStudyMatch;
    private String collectorStudyStatus;
    //private ArrayList<Series> series;
    
    public Study(DatabaseParameters dbParam, String studyFk ){
        this.studyFk = studyFk;
        this.collectorStudyStatus = ReadFromIqcDatabase.getCollectorStudyStatusByStudyFk(dbParam, studyFk);
        selectorStudyMatch = new SelectorMatch(dbParam, studyFk, "study");
        
    }
    
    public SelectorMatch getSelectorStudyMatch(){
        return this.selectorStudyMatch;
    }
    
    public String getStudyFk(){
        return studyFk;
    }
    
    public String getCollectorStudyStatus(){
        return collectorStudyStatus;
    }
    
//    public ArrayList<Series> getSeriesList(){
//        return this.series;
//    }
    
//    public Series getSeries(int index){
//        return this.series.get(index);
//    }
}
