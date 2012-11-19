/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.db;

/**
 *
 * @author Ralph Berendsen <>
 */
public class DatabaseParameters {
    public String connectionURL;
    public String databasename;
    public String username;
    public String password;
    public String type; 
    public String driverclass;

    public DatabaseParameters(){
        this.connectionURL = "";
        this.databasename="";
        this.username="";
        this.password="";
        this.type="";
        this.driverclass="";
    }
}
