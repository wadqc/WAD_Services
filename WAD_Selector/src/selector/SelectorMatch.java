/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package selector;

import java.util.ArrayList;
import wad.db.CompareRows;
import wad.db.DatabaseParameters;

/**
 *
 * @author Ralph Berendsen <>
 */
public class SelectorMatch {
    private ArrayList<String> selectorMatchList;
    
    /*sourceFk is de serie of study fk van het originele object, level is het 
     *niveau "series" of "study" om te bepalen om welke het gaat. 
     */     
    public SelectorMatch(DatabaseParameters dbParam, String sourceFk, String level){
        if (level.equals("study")){
            selectorMatchList = CompareRows.getMatchingSelectorStudyPkList(dbParam, sourceFk);
        } else if (level.equals("series")){
            selectorMatchList = CompareRows.getMatchingSelectorSeriesPkList(dbParam, sourceFk);
        }
    }
    
    public ArrayList<String> getSelectorMatchList(){
        return this.selectorMatchList;
    }
    
    public String getItem(int index){
        return this.selectorMatchList.get(index);
    }
    
    public int size(){
        return this.selectorMatchList.size();
    }
}
