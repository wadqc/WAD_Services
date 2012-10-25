/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package selector;

import GewensteProcessen.GPRow;
import GewensteProcessen.GPTable;
import java.util.ArrayList;
import wad.db.CollectorStatusTable;
import wad.db.DatabaseParameters;
import wad.db.ReadFromIqcDatabase;
import wad.db.WriteGewensteProcessen;
import wad.db.WriteToIqcDatabase;

/**
 *
 * @author Ralph Berendsen <>
 * 
 * Deze class moet een verzameling leveren van alle studies met status 1
 * in collector_studies_status en daarbij alle series met een status 0 of 1 in 
 * collector_series_status. Daarnaast heeft elke studie en studyList een arraylist 
 * met matching selector gegevens.
 * Deze class wordt na vulling gebruikt om de gewenste_process te vullen
 */
public class SelectorSummary {
    private SelectorTable selTable;
    private ArrayList<Study> studyList = new ArrayList<Study>();
    private ArrayList<Series> seriesList = new ArrayList<Series>();
    private DatabaseParameters dbParam;
    private ArrayList<String> seriesFkListNoSerieProcessing=new ArrayList<String>();
    private GPTable gpTable = new GPTable();
    
    public void Selector(DatabaseParameters dbParam){
        this.dbParam = dbParam;
        CollectorStatusTable colSeriesStatusTable = new CollectorStatusTable(dbParam, "collector_series_status"); 
        CollectorStatusTable colStudyStatusTable = new CollectorStatusTable(dbParam, "collector_study_status"); 
        
        //Selecteer alleen de seriesLijst en studies met collector_status=1 (voor study ook 0, om selecties op serie niveau en verwerking op study nieveau te blokkeren als de study nog niet compleet is)
        for (int i=0;i<colSeriesStatusTable.size();i++){
            if (!colSeriesStatusTable.getColllectorStatusRow(i).getStatus().equals("1")){
                colSeriesStatusTable.removeColllectorStatusRow(i);
            } else {
                //Voor elke studie uit collector_status een lijst opbouwen
                seriesList.add(new Series(dbParam, colSeriesStatusTable.getColllectorStatusRow(i).getFk()));
                WriteToIqcDatabase.UpdateSeriesStatus(dbParam, colSeriesStatusTable.getColllectorStatusRow(i).getFk(), "2");
            }
        }
        for (int i=0;i<colStudyStatusTable.size();i++){
            if (!colStudyStatusTable.getColllectorStatusRow(i).getStatus().equals("1") &&
                !colStudyStatusTable.getColllectorStatusRow(i).getStatus().equals("0")   ){
                colStudyStatusTable.removeColllectorStatusRow(i);
            } else {
                //Voor elke studie uit collctor_status een lijst opbouwen
                studyList.add(new Study(dbParam, colStudyStatusTable.getColllectorStatusRow(i).getFk()));
                if (colStudyStatusTable.getColllectorStatusRow(i).getStatus().equals("1")){
                    WriteToIqcDatabase.UpdateStudyStatus(dbParam, colStudyStatusTable.getColllectorStatusRow(i).getFk(), "2");
                }
            }
        }
        //Lijst met series en studies en hun matches met selector_serie en selector_study is nu beschikbaar
        
        //nu een overzicht van de tabel selector krijgen
        selTable = new SelectorTable(dbParam);
        
        //Starten met series verwerken
        processSeries();
        //Starten met study verwerken
        processStudy();
        //TODO Na de verwerking alle collector_status waarden bijwerken
        //TODO seriesFkListNoSerieProcessing controleren en deze uit gpTable halen of een andere variabele maken voor het wegschrijven en overal WriteToGewensteProcessen weghalen
        //TODO Collector_series_status en collector_study_status bijwerken Noprocessing dan status naar 1, anders status naar 3
        for (int i=0;i<seriesFkListNoSerieProcessing.size();i++){
            for (int j=0;j<gpTable.size();j++){
                if (gpTable.getSelectorRow(j).getSeriesFk().equals(seriesFkListNoSerieProcessing.get(i))){
                    gpTable.getSelectorRow(j).setSeriesFk("");
                }
            }
            WriteToIqcDatabase.UpdateSeriesStatus(dbParam, seriesFkListNoSerieProcessing.get(i), "1");
            int index = colSeriesStatusTable.getIndexWithFk(seriesFkListNoSerieProcessing.get(i));
            colSeriesStatusTable.removeColllectorStatusRow(index);
        }
        
        for (int i=0;i<gpTable.size();i++){
            if (!gpTable.getSelectorRow(i).getStudyFk().equals("")){
                WriteGewensteProcessen.writeDataStudy(dbParam, gpTable.getSelectorRow(i).getSelectorFk(), gpTable.getSelectorRow(i).getStudyFk());
            } else if(!gpTable.getSelectorRow(i).getSeriesFk().equals("")){
                WriteGewensteProcessen.writeDataSeries(dbParam, gpTable.getSelectorRow(i).getSelectorFk(), gpTable.getSelectorRow(i).getSeriesFk());
            }
        }
        
        //aanpassen collector_status_table en collector_series_table status 3 = Selector klaar
        for (int i=0;i<colSeriesStatusTable.size();i++){
            WriteToIqcDatabase.UpdateSeriesStatus(dbParam, colSeriesStatusTable.getColllectorStatusRow(i).getFk(), "3");
        }
        for (int i=0;i<colStudyStatusTable.size();i++){
            if (colStudyStatusTable.getColllectorStatusRow(i).getStatus().equals("1")){
                WriteToIqcDatabase.UpdateStudyStatus(dbParam, colStudyStatusTable.getColllectorStatusRow(i).getFk(), "3");
            }
        }
        
        String dummy="stop tijdens testen";
    }
    
    private void processSeries(){
        //Voor elke match bij elke studyList controleren op analyselvl en of er een studiecriterium
        //Zo ja verwerken bij studies, zo nee studyList individueel verwerken. 
        
        for (int i=0;i<seriesList.size();i++){
            Series serie = this.seriesList.get(i);
            for (int j=0;j<serie.getSelectorSeriesMatch().size();j++){
                String selectorSeriesFk = serie.getSelectorSeriesMatch().getItem(j);
                //Zoek deze selector_series_fk in selTable (meerdere return values mogelijk)
                ArrayList<String> selPkList = getSelectorPkWithSelectorSeriesFk(selectorSeriesFk);
                for (int k=0;k<selPkList.size();k++){
                    //zoek studiecriterium of en analyselvl
                    SelectorRow selRow = selTable.getSelectorRowWithPk(selPkList.get(k));
                    if (selRow.getAnalyseLevel().equalsIgnoreCase("study") ||
                        selRow.getAnalyseLevel().equalsIgnoreCase("studie") ||
                        selRow.getSelectorStudyFk()!=null){
                        //niet verwerken en overslaan, verwerking komt bij studie
                        //als selRow.getSelectorStudyFk()==null wordt niet getriggerd bij serie toch verwerken
                        //is studystatus 0 dan toevoegen aan studieList
                        if (selRow.getSelectorStudyFk()==null){                            
                            if (serie.getCollectorStudyStatus().equals("1")){
                            //verwerken als study
                                //WriteGewensteProcessen.writeDataStudy(dbParam, selPkList.get(k), serie.getStudyFk());
                                GPRow gpRow = new GPRow();
                                gpRow.setSelectorFk(selPkList.get(k));
                                gpRow.setStudyFk(serie.getStudyFk());
                                gpRow.setStatus("0");
                                gpTable.add(gpRow);
                            } else {
                                //Study is nog niet geheel ingeladen dus van deze study mag nog niets verwerkt worden op serie niveau anders ontstaan er dubbele resultaten
                                //Alle series bij deze study in een lijst verwerken om later te uit gewenste_processen te filteren
                                ArrayList<String> seriesFromStudyFk = ReadFromIqcDatabase.getSeriesPkListWithStudyFk(dbParam, serie.getStudyFk());
                                for (int l=0;l<seriesFromStudyFk.size();l++){
                                    seriesFkListNoSerieProcessing.remove(serie.getSeriesFk());
                                    seriesFkListNoSerieProcessing.add(serie.getSeriesFk());
                                }
                            }
                        }
                    } else {
                        //verwerken als serie
                        //WriteGewensteProcessen.writeDataSeries(dbParam, selPkList.get(k), serie.getSeriesFk());
                        GPRow gpRow = new GPRow();
                        gpRow.setSelectorFk(selPkList.get(k));
                        gpRow.setSeriesFk(serie.getSeriesFk());
                        gpRow.setStatus("0");
                        gpTable.add(gpRow);
                    }
                }
            }
        }     
    }
    
    private void processStudy(){
        //Voor elke match bij elke studyList controleren op analyselvl en of er een studiecriterium
        //Zo ja verwerken bij studies, zo nee studyList individueel verwerken.
        for (int i=0;i<studyList.size();i++){
            Study studie = this.studyList.get(i);
            if (!studie.getCollectorStudyStatus().equals("0")){
            for (int j=0;j<studie.getSelectorStudyMatch().size();j++){
                String selectorStudyFk = studie.getSelectorStudyMatch().getItem(j);
                //Zoek deze selector_series_fk in selTable (meerdere return values mogelijk)
                ArrayList<String> selPkList = getSelectorPkWithSelectorStudyFk(selectorStudyFk);
                for (int k=0;k<selPkList.size();k++){
                    //zoek studiecriterium of en analyselvl
                    SelectorRow selRow = selTable.getSelectorRowWithPk(selPkList.get(k));
                    if ((selRow.getAnalyseLevel().equalsIgnoreCase("study") ||
                        selRow.getAnalyseLevel().equalsIgnoreCase("studie")) &&
                        selRow.getSelectorSeriesFk()==null){
                        //verwerken als study
                        //WriteGewensteProcessen.writeDataStudy(dbParam, selPkList.get(k), studie.getStudyFk());
                        GPRow gpRow = new GPRow();
                        gpRow.setSelectorFk(selPkList.get(k));
                        gpRow.setStudyFk(studie.getStudyFk());
                        gpRow.setStatus("0");
                        gpTable.add(gpRow);
                    } else if (selRow.getSelectorSeriesFk()!=null){
                        //controleren of één serie in deze studyList overeenkomt met selector_series
                        //Ja dan analyselvl uitlezen
                        //  analyselvl series dan alleen die seriesLijst verwerken
                        //  analyselvl studie dan de hele studyList verwerken
                        //Nee dan studie/series niet verwerken
                        ArrayList<String> seriesLijst = ReadFromIqcDatabase.getSeriesPkListWithStudyFk(dbParam, studie.getStudyFk());
                        for (int si=0;si<seriesLijst.size();si++){
                            //Zoek de seriesFk op in serie
                            for (int sj=0;sj<this.seriesList.size();sj++){
                                if (this.seriesList.get(sj).getSeriesFk().equals(seriesLijst.get(si))){
                                    //serie gevonden, nu de lijst met matchen pakken en controleren of deze in dezelfde selectorrij staan als de selector_studie_fk
                                    for (int sk=0;sk<this.seriesList.get(sj).getSelectorSeriesMatch().size();sk++){
                                        String selSeriesFk = this.seriesList.get(sj).getSelectorSeriesMatch().getItem(sk);
                                        //selectorTabel afzoeken naar het voorkomen van deze selector_series_fk
                                        for (int sl=0;sl<selTable.size();sl++){
                                            if (selTable.getSelectorRow(sl).getSelectorSeriesFk()!=null){
                                            if (selTable.getSelectorRow(sl).getSelectorSeriesFk().equals(selSeriesFk)){
                                                //controleer of de selectorStudyFk past bij deze selectorRow
                                                if (selTable.getSelectorRow(sl).getSelectorStudyFk()!=null){
                                                if (selTable.getSelectorRow(sl).getSelectorStudyFk().equals(selectorStudyFk)){
                                                    //controleren van analyselevel
                                                    if (selRow.getAnalyseLevel().equalsIgnoreCase("series") ||
                                                        selRow.getAnalyseLevel().equalsIgnoreCase("serie")){
                                                        //verwerk deze serie
                                                        //WriteGewensteProcessen.writeDataSeries(dbParam, selPkList.get(k), this.seriesList.get(sj).getSeriesFk());
                                                        GPRow gpRow = new GPRow();
                                                        gpRow.setSelectorFk(selPkList.get(k));
                                                        gpRow.setSeriesFk(this.seriesList.get(sj).getSeriesFk());
                                                        gpRow.setStatus("0");
                                                        gpTable.add(gpRow);
                                                    } else {
                                                        //verwerk deze study
                                                        //TODO meerdere series kunnen voldoen aan de selector_serie, dus voor verwerken controleren of deze study al in gewenste processen staat
                                                        
                                                        //WriteGewensteProcessen.writeDataStudy(dbParam, selPkList.get(k), studie.getStudyFk());
                                                        GPRow gpRow = new GPRow();
                                                        gpRow.setSelectorFk(selPkList.get(k));
                                                        gpRow.setStudyFk(studie.getStudyFk());
                                                        gpRow.setStatus("0");
                                                        gpTable.add(gpRow);
                                                    }
                                                }
                                                }
                                            }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (selRow.getAnalyseLevel().equalsIgnoreCase("series") ||
                        selRow.getAnalyseLevel().equalsIgnoreCase("serie")){
                        //alle series bij deze studie verwerken als serie
                        ArrayList<String> seriesFkList = ReadFromIqcDatabase.getSeriesPkListWithStudyFk(dbParam, studie.getStudyFk());
                        for (int l=0;l<seriesFkList.size();l++){
                            //WriteGewensteProcessen.writeDataSeries(dbParam, selPkList.get(k), seriesFkList.get(l));
                            GPRow gpRow = new GPRow();
                            gpRow.setSelectorFk(selPkList.get(k));
                            gpRow.setSeriesFk(seriesFkList.get(l));
                            gpRow.setStatus("0");
                            gpTable.add(gpRow);
                        }
                    }
                }
            }
        }
        }
        
                
    }
    
    //VErkrijg all selectorPks waarbij selectorseriesFk voorkomt
    private ArrayList<String> getSelectorPkWithSelectorSeriesFk(String selectorSeriesFk){
        ArrayList<String> selTablePk = new ArrayList<String>();
        int dummy = selTable.size();
        for (int i=0;i<selTable.size();i++){
            if (this.selTable.getSelectorRow(i).getSelectorSeriesFk()!=null){
                if (this.selTable.getSelectorRow(i).getSelectorSeriesFk().equals(selectorSeriesFk)){
                    selTablePk.add(this.selTable.getSelectorRow(i).getPk());
                }
            }
        }
        return selTablePk;
    }
    
    private ArrayList<String> getSelectorPkWithSelectorStudyFk(String selectorStudyFk){
        ArrayList<String> selTablePk = new ArrayList<String>();
        for (int i=0;i<selTable.size();i++){
            if (this.selTable.getSelectorRow(i).getSelectorStudyFk()!=null){
                if (this.selTable.getSelectorRow(i).getSelectorStudyFk().equals(selectorStudyFk)){
                    selTablePk.add(this.selTable.getSelectorRow(i).getPk());
                }
            }
        }
        return selTablePk;
    }
    
    
    
}
