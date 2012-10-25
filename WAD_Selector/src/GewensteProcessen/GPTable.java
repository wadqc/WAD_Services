/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GewensteProcessen;

import java.util.ArrayList;

/**
 *
 * @author Ralph Berendsen <>
 */
public class GPTable {
    ArrayList<GPRow> gewProcessenTable = new ArrayList<GPRow>();
    
    public GPTable(){
        
    }
    
    public GPRow getSelectorRow(int index){
        return this.gewProcessenTable.get(index);
    }
    
    public int size(){
        return this.gewProcessenTable.size();
    }
    
    public void add(GPRow gpRow){
        if (!rowExists(gpRow)){
            this.gewProcessenTable.add(gpRow);
        }
    }
    
    private boolean rowExists(GPRow gpRow){        
        for (int i=0;i<this.gewProcessenTable.size();i++){
            if (this.gewProcessenTable.get(i).getSelectorFk().equals(gpRow.getSelectorFk()) &&
                this.gewProcessenTable.get(i).getStudyFk().equals(gpRow.getStudyFk()) &&
                this.gewProcessenTable.get(i).getSeriesFk().equals(gpRow.getSeriesFk()) &&
                this.gewProcessenTable.get(i).getInstanceFk().equals(gpRow.getInstanceFk()) &&
                this.gewProcessenTable.get(i).getStatus().equals(gpRow.getStatus()) ){
                return true;
            }            
        }
        return false;
    }
}
