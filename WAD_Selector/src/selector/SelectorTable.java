/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package selector;

import java.util.ArrayList;
import wad.db.DatabaseParameters;
import wad.db.ReadFromIqcDatabase;

/**
 *
 * @author Ralph Berendsen <>
 */
public class SelectorTable {
    private ArrayList<SelectorRow> selectorRowList = new ArrayList<SelectorRow>();
    
    public SelectorTable(DatabaseParameters dbParam){
        ArrayList<String> pkList = ReadFromIqcDatabase.getPkListFromTable(dbParam, "selector");
        for (int i=0;i<pkList.size();i++){
            selectorRowList.add(new SelectorRow(dbParam,pkList.get(i)));            
        }
    }
    
    public SelectorRow getSelectorRow(int index){
        return this.selectorRowList.get(index);
    }
    
    public int size(){
        return this.selectorRowList.size();
    }
    
    public SelectorRow getSelectorRowWithPk(String pk){
        for (int i=0;i<this.selectorRowList.size();i++){
            if (selectorRowList.get(i).getPk().equals(pk)){
                return this.selectorRowList.get(i);
            }
        }
        return null;
    }
}

