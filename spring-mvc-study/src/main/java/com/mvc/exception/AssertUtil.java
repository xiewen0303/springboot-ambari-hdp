package com.mvc.exception;

public class AssertUtil {

    /**
     * 判定是否为空
     * @param param
     * @param msg
     * @throws ParameterException
     */
    public static void assertNull(Object param,String msg) throws ParameterException {
        if(param == null) {
            throw new ParameterException(msg);
        }
    }
}
