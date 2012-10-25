/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.db;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ralph Berendsen <>
 */
public class ReadFromPacsDatabase {
    
    public static ArrayList<String> getFileListFromSerie(DatabaseParameters dbParam, String serieUID) {
        Connection dbConnection;
        ResultSet rs_serie;
        ResultSet rs_instance;
        ResultSet rs_files;
        ResultSet rs_filesystem;
        Statement stmt_serie;
        Statement stmt_instance;
        Statement stmt_files;
        Statement stmt_filesystem;
        ArrayList<String> profileList = new ArrayList<String>();
        String serie_fk = "";
        String instance_fk = "";
        String filesystem_fk = "";
        String filepath = "";
        String dirpath = "";

        dbConnection = PacsDatabaseConnection.conDb(dbParam);

        try {
            stmt_serie = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_serie = stmt_serie.executeQuery("SELECT pk FROM series WHERE series_iuid='"+serieUID+"'");
            while (rs_serie.next()) {
                //profileList.add(rs.getString("pk"));
                serie_fk = rs_serie.getString("pk");
                
                //serie identifier bekend nu instances zoeken
                stmt_instance = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rs_instance = stmt_instance.executeQuery("SELECT pk FROM instance WHERE series_fk='"+serie_fk+"'");
                while (rs_instance.next()){
                    instance_fk = rs_instance.getString("pk");
                    
                    //instance identifier bekend nu files zoeken
                    stmt_files = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    rs_files = stmt_files.executeQuery("SELECT * FROM files WHERE instance_fk='"+instance_fk+"'");
                    while (rs_files.next()){
                        filesystem_fk = rs_files.getString("filesystem_fk");
                        
                        //files identiefier bekend, nu filepath uitlezen en uit filesystem dirpath uitlezen
                        filepath = rs_files.getString("filepath");
                        filepath = filepath.replace('/', '\\');
                        stmt_filesystem = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        rs_filesystem = stmt_filesystem.executeQuery("SELECT dirpath FROM filesystem WHERE pk='"+filesystem_fk+"'");
                        while (rs_filesystem.next()){
                            dirpath = rs_filesystem.getString("dirpath"); 
                            profileList.add(dirpath+"\\"+filepath);
                        }
                    }
                }
            }
            PacsDatabaseConnection.closeDb(dbConnection, stmt_serie, rs_serie);
            return profileList;
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            System.out.println(ex.toString());
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return profileList;
    }
    
    //Read all studies from pacsDB (DCM4CHEE) and return arrayList with study_iuid from table STUDY
    public static ArrayList<String> getAllStudies(DatabaseParameters dbParam){
        ArrayList<String> studyList = new ArrayList<String>();
        Connection dbConnection;
        ResultSet rs_study;        
        Statement stmt_study;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        
        try {
            stmt_study = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_study = stmt_study.executeQuery("SELECT * FROM study");
            while (rs_study.next()) {
                studyList.add(rs_study.getString("study_iuid"));
                //serie_fk = rs_study.getString("pk");                
            }            
            rs_study.close();
            stmt_study.close();
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return studyList;
    }
    
    //Read all series from pacsDB (DCM4CHEE) and return arrayList with series_iuid from table series
    public static ArrayList<String> getAllSeries(DatabaseParameters dbParam){
        ArrayList<String> seriesList = new ArrayList<String>();
        Connection dbConnection;
        ResultSet rs_series;        
        Statement stmt_series;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        
        try {
            stmt_series = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_series = stmt_series.executeQuery("SELECT * FROM series");
            while (rs_series.next()) {
                seriesList.add(rs_series.getString("series_iuid"));                               
            }
            rs_series.close();
            stmt_series.close();
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null); 
        return seriesList;
    }
    
    //Read all series_iuid from pacsDB.series (DCM4CHEE) where studyIUID is given
    public static ArrayList<String> getSeriesWithStudyIUID(DatabaseParameters dbParam, String studyIUID){
        ArrayList<String> seriesList = new ArrayList<String>();
        Connection dbConnection;
        ResultSet rs_serie;        
        Statement stmt_serie;
        ResultSet rs_study;        
        Statement stmt_study;
        String study_fk;
        String series_IUID;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        
        try {
            stmt_study = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_study = stmt_study.executeQuery("SELECT pk FROM study WHERE study_iuid='"+studyIUID+"'");
            while (rs_study.next()) {
                //studyList.add(rs_study.getString("study_iuid"));
                study_fk = rs_study.getString("pk"); 
                stmt_serie = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rs_serie = stmt_serie.executeQuery("SELECT * FROM series WHERE study_fk='"+study_fk+"'");
                while (rs_serie.next()) {                    
                    seriesList.add(rs_serie.getString("series_iuid"));
                }
                rs_serie.close();               
                stmt_serie.close();                
            }
            rs_study.close();
            stmt_study.close();
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return seriesList;
    }
    
    //Read all pk from pacsDB.instance (DCM4CHEE) where seriesIUID is given
    public static ArrayList<String> getInstancePkWithSeriesIUID(DatabaseParameters dbParam, String seriesIUID){
        ArrayList<String> instanceList = new ArrayList<String>();
        Connection dbConnection;
        ResultSet rs_instance;        
        Statement stmt_instance;
        ResultSet rs_series;        
        Statement stmt_series;
        String series_fk;
        String series_IUID;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        
        try {
            stmt_series = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_series = stmt_series.executeQuery("SELECT pk FROM series WHERE series_iuid='"+seriesIUID+"'");
            while (rs_series.next()) {                
                series_fk = rs_series.getString("pk"); 
                stmt_instance = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rs_instance = stmt_instance.executeQuery("SELECT * FROM instance WHERE series_fk='"+series_fk+"'");
                while (rs_instance.next()) {                    
                    instanceList.add(rs_instance.getString("pk"));
                }
                rs_instance.close();
                stmt_instance.close();
            }
            rs_series.close();
            stmt_series.close();
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return instanceList;
    }
    
    //Read all pk from pacsDB.files (DCM4CHEE) where instance_fk is given
    public static String getFilesPkWithInstancePk(DatabaseParameters dbParam, String instance_fk){
        String files_pk = new String();
        Connection dbConnection;
        ResultSet rs_files;        
        Statement stmt_files;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        
        try {
            stmt_files = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_files = stmt_files.executeQuery("SELECT pk FROM files WHERE instance_fk='"+instance_fk+"'");
            while (rs_files.next()) {                
                    files_pk = rs_files.getString("pk");
            }
            PacsDatabaseConnection.closeDb(dbConnection, stmt_files, rs_files);
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return files_pk;
    }
    
    //Read filesystem_pk from pacsDB.files (DCM4CHEE) where pk is given
    public static String getFilesystemPkWithFilesPk(DatabaseParameters dbParam, String files_pk){
        String filesystem_pk = new String();
        Connection dbConnection;
        ResultSet rs_files;        
        Statement stmt_files;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        
        try {
            stmt_files = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_files = stmt_files.executeQuery("SELECT filesystem_fk FROM files WHERE pk='"+files_pk+"'");
            while (rs_files.next()) {                
                    filesystem_pk = rs_files.getString("filesystem_fk");
            }
            PacsDatabaseConnection.closeDb(dbConnection, stmt_files, rs_files);
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return filesystem_pk;
    }
    
    //Get the status (0 or 1) of the serie based on its serieIUID (status 1 is busy with adding to pacsDB)
    public static String getSeriesStatusWithSeriesIUID(DatabaseParameters dbParam, String seriesIUID){        
        Connection dbConnection;
        ResultSet rs_serie;        
        Statement stmt_serie;        
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        
        try {
            stmt_serie = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_serie = stmt_serie.executeQuery("SELECT series_status FROM series WHERE series_iuid='"+seriesIUID+"'");
            if (rs_serie.next()) {
                String result = rs_serie.getString("series_status");                                
                PacsDatabaseConnection.closeDb(dbConnection, stmt_serie, rs_serie);
                return result;
            }            
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }        
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return "-1";
        
    }
    
    //Get the primary key of a table based on a unique identifier (by example sieres_iuid or study_iuid
    public static String getPkWithUniqueIdentifier(DatabaseParameters dbParam, String tableName, String uid_name, String uid_value){        
        Connection dbConnection;
        ResultSet rs_serie;        
        Statement stmt_serie;        
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        
        try {
            stmt_serie = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_serie = stmt_serie.executeQuery("SELECT pk FROM "+tableName+"  WHERE "+uid_name+"='"+uid_value+"'");
            if (rs_serie.next()) {
                String result = rs_serie.getString("pk"); 
                PacsDatabaseConnection.closeDb(dbConnection, stmt_serie, rs_serie);
                return result;
            }                      
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }        
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return "-1";
        
    }
    
    //Get the foreign key of a table based on a unique identifier (by example sieres_iuid or study_iuid
    public static String getForeignKeyWithUniqueIdentifier(DatabaseParameters dbParam, String tableName, String fkName, String uid_name, String uid_value){        
        Connection dbConnection;
        ResultSet rs_serie;        
        Statement stmt_serie;        
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        
        try {
            stmt_serie = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs_serie = stmt_serie.executeQuery("SELECT "+fkName+" FROM "+tableName+"  WHERE "+uid_name+"='"+uid_value+"'");
            if (rs_serie.next()) {
                String result = rs_serie.getString(fkName);  
                PacsDatabaseConnection.closeDb(dbConnection, stmt_serie, rs_serie);
                return result;
            }
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }        
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return "-1";
        
    }
    
    //Read steries from pacsDB (DCM4CHEE) where seriesUID is a match and return the resultset
    public static ResultSet getSeriesResultSetWithSeriesIUID(DatabaseParameters dbParam, String seriesIUID){        
        Connection dbConnection;              
        Statement stmt_serie;       
        ResultSet rs;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_serie = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);            
            rs = stmt_serie.executeQuery("SELECT * FROM series WHERE series_iuid='"+seriesIUID+"'");            
            //dbConnection.close();
            return rs;
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }        
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return null;        
    }
    
    //Read study from pacsDB (DCM4CHEE) where studyUID is a match and return resultset
    public static ResultSet getStudyResultSetWithStudyIUID(DatabaseParameters dbParam, String studyIUID){        
        Connection dbConnection;              
        Statement stmt_study;       
        ResultSet rs;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_study = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt_study.executeQuery("SELECT * FROM study WHERE study_iuid='"+studyIUID+"'");            
            //dbConnection.close();
            return rs;            
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }        
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return null;        
    }
    
    //Read tableName from pacsDB (DCM4CHEE) where pk is a match and return resultset
    public static ResultSet getTableResultSetWithPrimaryKey(DatabaseParameters dbParam, String tableName, String pk){        
        Connection dbConnection;              
        Statement stmt_study;       
        ResultSet rs;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_study = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt_study.executeQuery("SELECT * FROM "+tableName+" WHERE pk='"+pk+"'");            
            //dbConnection.close();
            return rs;            
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }        
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return null;        
    }
    
    //Read tableName from pacsDB (DCM4CHEE) where pk is a match and return resultset
    public static ResultSet getTableResultSetWithForeignKey(DatabaseParameters dbParam, String tableName, String fkName, String fk){        
        Connection dbConnection;              
        Statement stmt_study;       
        ResultSet rs;
        
        dbConnection = PacsDatabaseConnection.conDb(dbParam);
        try {
            stmt_study = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt_study.executeQuery("SELECT * FROM "+tableName+" WHERE "+fkName+"='"+fk+"'");            
            //dbConnection.close();
            return rs;            
        } catch (SQLException ex) {
            PacsDatabaseConnection.closeDb(dbConnection, null, null);
            Logger.getLogger(ReadFromPacsDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }        
        PacsDatabaseConnection.closeDb(dbConnection, null, null);
        return null;        
    }
}
