/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_collector;

import java.sql.Connection;
import java.util.ArrayList;
import wad.db.ReadFromIqcDatabase;
import wad.db.ReadFromPacsDatabase;
import wad.db.WriteToIqcDatabase;

/**
 *
 * @author Ralph Berendsen <>
 */
public class UpdateFiles {
    
    public static Integer start(Connection iqcConnection, Connection pacsConnection){
        ArrayList<String> filemd5List = ReadFromIqcDatabase.getFilemd5ListFromFiles(iqcConnection);
        Integer count = 0;
        for (int i=0;i<filemd5List.size();i++){
            String iqcFilepath = ReadFromIqcDatabase.getFilepathWithFilemd5(iqcConnection, filemd5List.get(i));
            String pacsFilepath = ReadFromPacsDatabase.getFilepathWithFilemd5(pacsConnection, filemd5List.get(i));
            if (pacsFilepath==null){
                if (!iqcFilepath.equals("null")){
                    WriteToIqcDatabase.UpdateFiles(iqcConnection, filemd5List.get(i), null);
                    count++;
                }
            } else if (!iqcFilepath.equals(pacsFilepath)){
                WriteToIqcDatabase.UpdateFiles(iqcConnection, filemd5List.get(i), pacsFilepath);
                count++;
            }
        }
        return count;
    }
    
    
}
