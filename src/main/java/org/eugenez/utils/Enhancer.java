package org.eugenez.utils;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.eugenez.utils.exception.EnhanceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.repository.ClassRepository;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

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
        try {
            net.sf.cglib.proxy.Enhancer e = new net.sf.cglib.proxy.Enhancer();
            Map<String, Class<?>> parameterizedTypeMap = null;
            if (typeToWrap instanceof ParameterizedType) {
                parameterizedTypeMap = prepareTypeMap(((ParameterizedType) typeToWrap));
                Class<?> type = (Class) ((ParameterizedType) typeToWrap).getRawType();
                if (type.isInterface()) {
                    e.setInterfaces(new Class[]{type, Parametrized.class});
                } else {
                    e.setSuperclass(type);
                    e.setInterfaces(new Class[]{Parametrized.class});
                }
            } else if (typeToWrap instanceof TypeVariable) {
                e.setSuperclass((methodEntry.getReturnType()));
            } else {
                e.setSuperclass((Class<?>) typeToWrap);
            }
            e.setCallback(new Interceptor(methodEntry, parameterizedTypeMap));
            return (T) e.create();
        } catch (NoSuchFieldException e) {
            throw new EnhanceException(e);
        } catch (IllegalAccessException e) {
            throw new EnhanceException(e);
        }
    }

    private static Map<String, Class<?>> prepareTypeMap(ParameterizedType typeToWrap) throws NoSuchFieldException, IllegalAccessException {
        Type[] types = typeToWrap.getActualTypeArguments();
        TypeVariable[] typeVariables = getGenericInfo(((Class<?>) typeToWrap.getRawType())).getTypeParameters();
        Map<String, Class<?>> typeMap = new HashMap<String, Class<?>>();
        for (int i = 0; i < types.length; i++) {
            typeMap.put(typeVariables[i].getName(), (Class) types[i]);
        }
        return typeMap;
    }

    private static class Interceptor implements MethodInterceptor {

        private MethodEntry previousMethodEntry;

        private Map<String, Class<?>> parameterizedTypeMap;

        private Interceptor(MethodEntry previousMethodEntry, Map<String, Class<?>> parameterizedTypeMap) {
            this.previousMethodEntry = previousMethodEntry;
            this.parameterizedTypeMap = parameterizedTypeMap;
        }

        public Object intercept(Object object, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            //sometimes jvm call finalize and this broke the results. So, we do not need to process finalize by our logic
            if (method.getName().equals("finalize")) {
                return proxy.invokeSuper(object, args);
            }
            if (method.getName().equals("getCGLIBParametrizedTypeMap")) {
                return parameterizedTypeMap;
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
            return getGenericClassType(((Parametrized) object).getCGLIBParametrizedTypeMap(), method);
        }
        return method.getReturnType();
    }

    private static Class<?> getGenericClassType(Map<String, Class<?>> parametrizedTypeMap, Method method) {
        TypeVariable genericReturnTypeSignature = (TypeVariable) method.getGenericReturnType();
        return parametrizedTypeMap.get(genericReturnTypeSignature.getName());
    }

    public interface Parametrized {
        Map<String, Class<?>> getCGLIBParametrizedTypeMap();
    }

    public static ClassRepository getGenericInfo(Class<?> rawType) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = Class.class.getDeclaredField("genericInfo");
        declaredField.setAccessible(true);
        return (ClassRepository) declaredField.get(rawType);
    }
}
