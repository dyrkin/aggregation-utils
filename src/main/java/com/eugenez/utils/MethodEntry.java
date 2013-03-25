package com.eugenez.utils;

import java.lang.reflect.Method;

/**
* @author eugene zadyra
*/
public class MethodEntry {
    private Method method;
    private Class<?> returnType;
    private Object[] args;
    private MethodEntry previousMethod;

    MethodEntry(Method method, Object[] args) {
        this.method = method;
        this.args = args;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public MethodEntry getPreviousMethod() {
        return previousMethod;
    }

    public void setPreviousMethod(MethodEntry previousMethod) {
        this.previousMethod = previousMethod;
    }
}
