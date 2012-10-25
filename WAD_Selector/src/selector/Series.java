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
public class Series {
    private String seriesFk;
    private String studyFk;
    private String collectorStudyStatus;
    private String collectorSeriesStatus;
    private SelectorMatch selectorSeriesMatch;
    
    public Series(DatabaseParameters dbParam, String seriesFk ){
        this.seriesFk = seriesFk;
        this.studyFk = ReadFromIqcDatabase.getStudyFkBySeriesFkFromSeries(dbParam, seriesFk);
        this.collectorStudyStatus = ReadFromIqcDatabase.getCollectorStudyStatusByStudyFk(dbParam, this.studyFk);
        this.collectorSeriesStatus = ReadFromIqcDatabase.getCollectorSeriesStatusBySeriesFk(dbParam, this.seriesFk);
        this.selectorSeriesMatch = new SelectorMatch(dbParam, seriesFk, "series");
    }
    
    public SelectorMatch getSelectorSeriesMatch(){
        return this.selectorSeriesMatch;
    }
    
    public String getSeriesFk(){
        return this.seriesFk;
    }
    
    public String getStudyFk(){
        return this.studyFk;
    }
    
    public String getCollectorStudyStatus(){
        return this.collectorStudyStatus;
    }
    
    public String getCollectorSeriesStatus(){
        return this.collectorSeriesStatus;
    }
}
