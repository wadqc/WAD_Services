/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import wad.xml.AnalyseModuleResultFile;
import wad.xml.ResultsBoolean;
import wad.xml.ResultsChar;
import wad.xml.ResultsFloat;
import wad.xml.ResultsObject;

/**
 *
 * @author Ralph Berendsen <>
 */
public class WriteResultaten {

    AnalyseModuleResultFile resultaten;
    int gewensteProcessenKey;
    //int resultatenKey;
    
    private static Log log = LogFactory.getLog(WriteResultaten.class);

    public WriteResultaten(Connection dbConnection, AnalyseModuleResultFile resultFile, int gpKey) {
        this.resultaten = resultFile;
        this.gewensteProcessenKey = gpKey;
        //Verwijderen van betssande resulaten
        removeResults(dbConnection, "resultaten_char");
        removeResults(dbConnection, "resultaten_boolean");
        removeResults(dbConnection, "resultaten_floating");
        removeResults(dbConnection, "resultaten_object");
        for (int i = 0; i < this.resultaten.getResultSize(); i++) {
            //verwerk in de resultatentabel
            if (getObjectType(this.resultaten.getResult(i)).equals("b")) {
                writeBoolean(dbConnection, (ResultsBoolean) this.resultaten.getResult(i));
            } else if (getObjectType(this.resultaten.getResult(i)).equals("c")) {
                writeChar(dbConnection, (ResultsChar) this.resultaten.getResult(i));
            } else if (getObjectType(this.resultaten.getResult(i)).equals("f")) {
                writeFloat(dbConnection, (ResultsFloat) this.resultaten.getResult(i));
            } else if (getObjectType(this.resultaten.getResult(i)).equals("o")) {
                writeObject(dbConnection, (ResultsObject) this.resultaten.getResult(i));
            }
        }
    }

//    private int write(Connection dbConnection) {
//        Statement stmt_Write;
//        String statement = "INSERT INTO resultaten(gewenste_processen_fk) values ('" + this.gewensteProcessenKey + "')";
//        try {
//            stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
//            int count = stmt_Write.executeUpdate(statement, Statement.RETURN_GENERATED_KEYS);
//            int autoIncKeyFromApi = -1;
//            ResultSet rs = stmt_Write.getGeneratedKeys();
//            if (rs.next()) {
//                autoIncKeyFromApi = rs.getInt(1);
//            }
//            rs.close();
//            stmt_Write.close();
//            return autoIncKeyFromApi;
//        } catch (SQLException ex) {
//            LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
//        }
//        return -1;
//    }

    private void writeBoolean(Connection dbConnection, ResultsBoolean results) {
        Statement stmt_Write;
        String statement = "INSERT INTO resultaten_boolean(niveau, gewenste_processen_fk, omschrijving, volgnummer, waarde) ";
        statement = statement + "values ('" + results.getNiveau() + "','"
                + this.gewensteProcessenKey + "','"
                + results.getOmschrijving() + "',"
                + results.getVolgnummer() + ",'"
                + results.getWaarde()+"'"//+
                //checkEmptyString(results.getDatetime())
                + ")";
        try {
            stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            int count = stmt_Write.executeUpdate(statement, Statement.RETURN_GENERATED_KEYS);
            stmt_Write.close();
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{WriteResultaten.class.getName(), ex});
            log.error(ex);
        }
    }

    private void writeChar(Connection dbConnection, ResultsChar results) {
        Statement stmt_Write;
        String statement = "INSERT INTO resultaten_char(niveau, gewenste_processen_fk, omschrijving, volgnummer, waarde) ";
        statement = statement + "values ('" + results.getNiveau() + "','"
                + this.gewensteProcessenKey + "','"
                + results.getOmschrijving() + "',"
                + results.getVolgnummer() + ",'"
                + results.getWaarde()+"'"//+
                //checkEmptyString(results.getDatetime())
                + ")";

        try {
            stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            int count = stmt_Write.executeUpdate(statement, Statement.RETURN_GENERATED_KEYS);
            stmt_Write.close();
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
            log.error(ex);
        }
    }

    private void writeFloat(Connection dbConnection, ResultsFloat results) {
        Statement stmt_Write;
        String statement = "INSERT INTO resultaten_floating(niveau, "
                + "gewenste_processen_fk, "
                + "omschrijving, "
                + "volgnummer, "
                + "waarde, "
                //+ "datetime, "
                + "grootheid, "
                + "eenheid, "
                + "grens_kritisch_boven, "
                + "grens_kritisch_onder, "
                + "grens_acceptabel_boven, "
                + "grens_acceptabel_onder) ";
        statement = statement + "values ('" + results.getNiveau() + "','"
                + this.gewensteProcessenKey + "','"
                + results.getOmschrijving() + "',"
                + results.getVolgnummer() + ","
                + checkFloating(results.getWaarde()) + ",'"
                + //checkEmptyString(results.getDatetime())+",'"+
                results.getGrootheid() + "','"
                + results.getEenheid() + "',"
                + checkFloating(results.getGrensKritischBoven()) + ","
                + checkFloating(results.getGrensKritischOnder()) + ","
                + checkFloating(results.getGrensAcceptabelBoven()) + ","
                + checkFloating(results.getGrensAcceptabelOnder()) + ")";
        try {
            stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            int count = stmt_Write.executeUpdate(statement, Statement.RETURN_GENERATED_KEYS);
            stmt_Write.close();
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
            log.error(ex);
        }
    }

    private void writeObject(Connection dbConnection, ResultsObject results) {
        Statement stmt_Write;
        String pad = results.getObjectnaampad();
        pad = pad.replace("\\", "/");
        String statement = "INSERT INTO resultaten_object(niveau, "
                + "gewenste_processen_fk, "
                + "omschrijving, "
                + "volgnummer, "
                + "object_naam_pad) ";
                //+ "datetime) ";
        statement = statement + "values ('" + results.getNiveau() + "','"
                + this.gewensteProcessenKey + "','"
                + results.getOmschrijving() + "',"
                + results.getVolgnummer() + ",'"
                + pad+"'"//+
                // checkEmptyString(results.getDatetime())
                + ")";
        try {
            stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            int count = stmt_Write.executeUpdate(statement, Statement.RETURN_GENERATED_KEYS);
            stmt_Write.close();
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
            log.error(ex);
        }
    }

    private String getObjectType(Object obj) {
        Class<?> c = obj.getClass();
        String test = c.getName();
        if (c.getName().contains("Boolean")) {
            return "b";
        } else if (c.getName().contains("Char")) {
            return "c";
        } else if (c.getName().contains("Float")) {
            return "f";
        } else if (c.getName().contains("Object")) {
            return "o";
        }
        return null;
    }

    private String checkEmptyString(String str) {
        if (str.isEmpty()) {
            return null;
        } else {
            return "'" + str + "'";
        }
    }

    private Float checkFloating(String value) {
        if (value.isEmpty()) {
            return null;
        } else if (value.contains(",")) {
            value = value.replace(",", ".");
            return Float.parseFloat(value);
        } else {
            return Float.parseFloat(value);
        }
    }
    
    private Boolean removeResults(Connection dbConnection, String table){
        Statement stmt_Write;
        String statement = "DELETE FROM "+table+ " WHERE gewenste_processen_fk="+this.gewensteProcessenKey;
        try {
            stmt_Write = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            int count = stmt_Write.executeUpdate(statement, Statement.RETURN_GENERATED_KEYS);
            stmt_Write.close();
            return true;
        } catch (SQLException ex) {
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{ReadFromIqcDatabase.class.getName(), ex});
            log.error(ex);
            return false;
        }
    }
}
