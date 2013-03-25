package com.eugenez.utils;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.*;

/**
 * @author eugene zadyra
 */
public class MethodMagic {

    public static ThreadLocal<MethodEntry> invokedMethodHierarchy = new ThreadLocal<MethodEntry>();

    public static <T> T m(Class<T> typeToWrap) {
        return m(typeToWrap, null);
    }

    private static <T> T m(Type typeToWrap, MethodEntry methodEntry) {
        return enhance(typeToWrap, methodEntry);
    }

    private static <T> T enhance(Type typeToWrap, MethodEntry methodEntry) {
        Enhancer e = new Enhancer();
        Class<?> parameterizedType = null;
        if (typeToWrap instanceof ParameterizedType) {
            parameterizedType = (Class<?>) ((ParameterizedType) typeToWrap).getActualTypeArguments()[0];
            Class<?> type = (Class) ((ParameterizedType) typeToWrap).getRawType();
            if (type.isInterface()) {
                e.setInterfaces(new Class[]{type, Parametrized.class});
            } else {
                e.setSuperclass(type);
            }

        } else if (typeToWrap instanceof TypeVariable) {
            e.setSuperclass((Class<?>) ((TypeVariable) typeToWrap).getGenericDeclaration());
        } else {
            e.setSuperclass((Class<?>) typeToWrap);
        }
        e.setCallback(new Interceptor(methodEntry, parameterizedType));
        return (T) e.create();
    }

    private static class Interceptor implements MethodInterceptor {

        private MethodEntry previousMethodEntry;

        private Class<?> parametrizedType;

        private Interceptor(MethodEntry previousMethodEntry, Class<?> parametrizedType) {
            this.previousMethodEntry = previousMethodEntry;
            this.parametrizedType = parametrizedType;
        }

        public Object intercept(Object object, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            //sometimes jvm call finalize and this broke the results. So, we do not need to process finalize by our logic
            if (method.getName().equals("finalize")) {
                return proxy.invokeSuper(object, args);
            }
            if (method.getName().equals("getCGLIBParametrizedType")) {
                return parametrizedType;
            }
            previousMethodEntry = createMethodEntry(previousMethodEntry, object, method, args);
            invokedMethodHierarchy.set(previousMethodEntry);
            if (!isSimpleReturnType(method, object)) {
                return m(method.getGenericReturnType(), previousMethodEntry);
            }
            return proxy.invokeSuper(object, args);
        }
    }

    private static boolean isSimpleReturnType(Method method, Object object) {
        Class<?> returnType = getActualReturnType(method, object);
        return returnType.equals(Integer.TYPE) || returnType.equals(Double.TYPE)
                || returnType.equals(Float.TYPE) || returnType.equals(String.class);
    }

    private static MethodEntry createMethodEntry(MethodEntry previousMethodEntry, Object object, Method method, Object[] args) {
        MethodEntry methodEntry = new MethodEntry(method, args);
        methodEntry.setReturnType(getActualReturnType(method, object));
        if (previousMethodEntry != null) {
            setMethodEntryToTheBottomOfHierarchy(previousMethodEntry, methodEntry);
        }
        return previousMethodEntry != null ? previousMethodEntry : methodEntry;
    }

    private static Class<?> getActualReturnType(Method method, Object object) {
        if (object instanceof Parametrized) {
            return ((Parametrized) object).getCGLIBParametrizedType();
        }
        return method.getReturnType();
    }

    private static void setMethodEntryToTheBottomOfHierarchy(MethodEntry previousMethodEntry, MethodEntry methodEntry) {
        if (previousMethodEntry.getValue() != null) {
            setMethodEntryToTheBottomOfHierarchy(previousMethodEntry.getValue(), methodEntry);
        } else {
            previousMethodEntry.setValue(methodEntry);
        }
    }

    public interface Parametrized {
        Class<?> getCGLIBParametrizedType();
    }
}
