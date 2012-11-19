/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Ralph Berendsen <>
 */
public class LoggerWrapper {

    public static final Logger myLogger = Logger.getLogger("WAD");
    private static LoggerWrapper instance = null;

    public static LoggerWrapper getInstance() {
        if (instance == null) {
            prepareLogger();
            instance = new LoggerWrapper();
        }
        return instance;
    }

    private static void prepareLogger() {
        FileHandler myFileHandler;
        try {
            myFileHandler = new FileHandler("log.txt");
            myFileHandler.setFormatter(new SimpleFormatter());
            myLogger.addHandler(myFileHandler);
            myLogger.setUseParentHandlers(false);
            myLogger.setLevel(Level.INFO);
        } catch (IOException ex) {
            Logger.getLogger(LoggerWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(LoggerWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
