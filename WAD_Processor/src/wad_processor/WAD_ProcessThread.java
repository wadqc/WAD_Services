/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_processor;

import java.io.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author titulaer
 */
public class WAD_ProcessThread extends Thread {
    
    private static Log log = LogFactory.getLog(WAD_ProcessThread.class);
    
    private int _ID = 0;

    public int ID() {
        return _ID;
    }
    private boolean _running = true;
    private boolean _error = false;
    private String[] _args;
    private Process _proc;
    //Runtime rt = Runtime.getRuntime();

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
            //LoggerWrapper.myLogger.log(Level.WARNING, "{0} {1}", new Object[]{WAD_ProcessThread.class.getName(), t.toString()});
            log.warn(t);
        }
    }

    //doc: deze routine wordt aangeroepen als de inherited method thread.start wordt aangeroepen
    public void run() {
        try {
            // JK process builder kan stderr samenvoegen met stdout stream
            //_proc = rt.exec(_args);
            ProcessBuilder builder = new ProcessBuilder( _args );
            builder.redirectErrorStream( true );
            _proc = builder.start();
            //BT alleen wachten indien geconfigureerd, bijv html pagina openen kan reden zijn dat neit te doen
            BufferedReader reader = new BufferedReader(new InputStreamReader(_proc.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // JK terminal output van tread naar logfile ipv stdout
                //System.out.println("tasklist: " + line);
                log.info("Thread " + _ID + ": " + line);
            }
            _proc.waitFor();
            //doc: Zet de parameter die aangeeft dat process klaar 
            _running = false;
        } catch (Throwable t) {
            //t.printStackTrace();
            //LoggerWrapper.myLogger.log(Level.WARNING, "{0} {1}", new Object[]{WAD_ProcessThread.class.getName(), t.toString()});
            log.warn(t);
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
