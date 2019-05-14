package com.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerHandler {

    private static Logger LOG = LogManager.getLogger("error_logger");

    public static void debug(String msg,Object... args){
        LOG.debug(msg,args);
    }

    public static void error(String msg,Object... args){
        LOG.debug(msg,args);
    }
}
