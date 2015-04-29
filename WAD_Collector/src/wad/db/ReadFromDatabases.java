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
 * @author Ralph Berendsen 
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
            
            //New faster statement
            //This statement will return a set in which unique values from the joined table are selected
            //in case the value came from table1
            
            String statement2 = "SELECT * FROM "
                    + "(SELECT *, COUNT(`"+c1+"`) FROM "
                    + "(SELECT `"+c1+"`, 't1' as `naam` FROM `"+db1+"`.`"+t1+"` "
                    + "UNION ALL " 
                    + "SELECT `"+c2+"`, 't2' as `naam` FROM `"+db2+"`.`"+t2+"`)"
                    + "as temp "
                    + "GROUP BY (`"+c1+"`) HAVING COUNT(`"+c1+"`) = 1)"
                    + "temp2 WHERE `naam`= 't1'";
            
            rs = stmt.executeQuery(statement2);
            while (rs.next()) {
                valueList.add(rs.getString(c1));
                log.debug("getDifferenceOfTwoColomns : New "+c1+" found: "+rs.getString(c1) );
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            log.error("getDifferenceOfTwoColomns : "+ex);
        }
        return valueList;
    }
    
    
}
