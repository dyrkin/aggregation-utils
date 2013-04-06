package org.eugenez.utils;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * @author eugene zadyra
 */
public class Enhancer {

    private static final Logger log = LoggerFactory.getLogger(Enhancer.class);

    protected static ThreadLocal<MethodEntry> invokedMethodHierarchy = new ThreadLocal<MethodEntry>();

    public static <T> T e(Class<T> typeToWrap) {
        return e(typeToWrap, null);
    }

    private static <T> T e(Type typeToWrap, MethodEntry methodEntry) {
        return enhance(typeToWrap, methodEntry);
    }

    private static <T> T enhance(Type typeToWrap, MethodEntry methodEntry) {
        net.sf.cglib.proxy.Enhancer e = new net.sf.cglib.proxy.Enhancer();
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
            e.setSuperclass((methodEntry.getReturnType()));
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
            MethodEntry newMethodEntry = createMethodEntry(previousMethodEntry, object, method, args);
            invokedMethodHierarchy.set(newMethodEntry);
            if (!isSimpleReturnType(method, object)) {
                return e(method.getGenericReturnType(), newMethodEntry);
            }
            return null;
        }

        private MethodEntry createMethodEntry(MethodEntry previousMethodEntry, Object object, Method method, Object[] args) {
            MethodEntry methodEntry = new MethodEntry(method, args);
            methodEntry.setReturnType(getActualReturnType(method, object));
            methodEntry.setPreviousMethod(previousMethodEntry);
            if (previousMethodEntry != null) {
                methodEntry.getPreviousMethod().setNextMethodEntry(methodEntry);
            }
            return methodEntry;
        }
    }

    private static boolean isSimpleReturnType(Method method, Object object) {
        Class<?> returnType = getActualReturnType(method, object);
        return returnType.equals(Integer.TYPE) || returnType.equals(Integer.class) || returnType.equals(Double.TYPE)
                || returnType.equals(Double.class)
                || returnType.equals(Float.TYPE) || returnType.equals(Float.class) || returnType.equals(String.class);
    }

    private static Class<?> getActualReturnType(Method method, Object object) {
        if (method.getReturnType().equals(Object.class) && object instanceof Parametrized) {
            return ((Parametrized) object).getCGLIBParametrizedType();
        }
        return method.getReturnType();
    }

    public interface Parametrized {
        Class<?> getCGLIBParametrizedType();
    }
}
