/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Ralph Berendsen <>
 */
public class ReadFromDatabases {

    private static Log log = LogFactory.getLog(ReadFromDatabases.class);

    public static ArrayList<String> getDifferenceOfTwoColomns(Connection dbConnectionPacs, String db1, String t1, String c1, String db2, String t2, String c2) {
        ArrayList<String> valueList = new ArrayList<String>();
        ResultSet rs;
        Statement stmt;

        try {
            stmt = dbConnectionPacs.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String statement = "SELECT DISTINCT " +c1+ " " 
                    + "FROM "+db1+"."+t1+" "
                    + "WHERE NOT "
                    + "EXISTS ("
                    + "SELECT * "
                    + "FROM "+db2+"."+t2+" "
                    + "WHERE "+db2+"."+t2+"."+c2+"="+db1+"."+t1+"."+c1+")";            
            rs = stmt.executeQuery(statement);
            while (rs.next()) {
                valueList.add(rs.getString(c1));
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            log.error("getDifferenceOfTwoColomns : "+ex);
        }
        return valueList;
    }
    
    
}
