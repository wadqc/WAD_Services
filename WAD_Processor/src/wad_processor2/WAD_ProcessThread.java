/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_processor2;

import java.util.*;
import java.io.*;
import java.util.logging.Level;
import wad.logger.LoggerWrapper;

/**
 *
 * @author titulaer
 */
public class WAD_ProcessThread extends Thread {

    private int _ID = 0;

    public int ID() {
        return _ID;
    }
    private boolean _running = true;
    private boolean _error = false;
    private String[] _args;
    private Process _proc;
    Runtime rt = Runtime.getRuntime();

    public boolean isRunning() {
        return _running;
    }

    public boolean stoppedWithError() {
        return _error;
    }

    public void newProcessThread(String args[], Integer processID) {
        //doc: fk uit db zetten
        _ID = processID;

        if (args.length < 1) {
            System.out.println("USAGE java GoodWinRedirect <outputfile>");
        }
        try {   // any error???
            // any error???
            _args = args;
        } catch (Throwable t) {
            //t.printStackTrace();
            LoggerWrapper.myLogger.log(Level.WARNING, "{0} {1}", new Object[]{WAD_ProcessThread.class.getName(), t.toString()});
        }
    }

    //doc: deze routine wordt aangeroepen als de inherited method thread.start wordt aangeroepen
    public void run() {
        try {
            _proc = rt.exec(_args);
            //BT alleen wachten indien geconfigureerd, bijv html pagina openen kan reden zijn dat neit te doen
            BufferedReader reader = new BufferedReader(new InputStreamReader(_proc.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("tasklist: " + line);
            }
            _proc.waitFor();
            //doc: Zet de parameter die aangeeft dat process klaar 
            _running = false;
        } catch (Throwable t) {
            //t.printStackTrace();
            LoggerWrapper.myLogger.log(Level.WARNING, "{0} {1}", new Object[]{WAD_ProcessThread.class.getName(), t.toString()});
            _running = false;
            _error = true;
        }
    }
    //TODO BT niet de netste methode, well OS onafhankelijk. Willen we alle processen killen bij afsluiten?

    public void kill() {

        _proc.destroy();
    }
    //TODO BT alle checks aanpassen en uitbreiden

    public boolean validProcess(String args[]) {
        boolean result = true;

        /** 
         * 
         * check whether file exists, can be executed and directory exists
         * 
         * **/
        String _Executable = args[0];
        File f = new File(_Executable);
        if (!f.exists()) {
            result = false;
        }
        if (!f.canExecute()) {
            result = false;
        }
        String _WorkingPath = args[1];
        File d = new File(_WorkingPath);
        if (!f.exists()) {
            result = false;
        }
        return result;

    }
}
