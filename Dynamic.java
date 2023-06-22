package org.locutusque.CL;

import org.locutusque.CL.Exceptions.CLException;
import org.locutusque.CL.Exceptions.NoSuchObjectError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * May be used in the future for Dynamic Variable Assignment, but it seems a bit too complex for the architecture of this programming language. Loopholes will be sought out.
 */
public class Dynamic {
    protected Object[] variables;
    protected Map<Class<?>, Method[]> methodCache;

    public Dynamic() {
        variables = new Object[0];
        methodCache = new HashMap<>();
    }

    public void setVariables(Object... variables) {
        this.variables = variables;
    }

    public Object[] getVariables() {
        return variables;
    }

    public void displayMethods(Class<?> cls) {
        Method[] methods = getMethods(cls);
        for (Method method : methods) {
            System.out.println(method.getName());
        }
    }

    public void invokeMethod(Object obj, String methodName, Object... args) throws CLException, InvocationTargetException, IllegalAccessException {
        Class<?> cls = obj.getClass();
        Method method = getMethod(cls, methodName, getParameterTypes(args));
        method.setAccessible(true);
        method.invoke(obj, args);
    }

    private Method[] getMethods(Class<?> cls) {
        Method[] methods = methodCache.get(cls);
        if (methods == null) {
            methods = cls.getDeclaredMethods();
            methodCache.put(cls, methods);
        }
        return methods;
    }

    private Method getMethod(Class<?> cls, String methodName, Class<?>[] parameterTypes) throws NoSuchObjectError {
        Method[] methods = getMethods(cls);
        for (Method method : methods) {
            if (method.getName().equals(methodName) && parameterTypesMatch(method.getParameterTypes(), parameterTypes)) {
                return method;
            }
        }
        throw new NoSuchObjectError("Method not found in class " + cls.getName() + ":" + methodName);
    }

    private boolean parameterTypesMatch(Class<?>[] parameterTypes1, Class<?>[] parameterTypes2) {
        if (parameterTypes1.length != parameterTypes2.length) {
            return false;
        }

        for (int i = 0; i < parameterTypes1.length; i++) {
            if (!parameterTypes1[i].isAssignableFrom(parameterTypes2[i])) {
                return false;
            }
        }

        return true;
    }

    private Class<?>[] getParameterTypes(Object... args) {
        if (args == null) {
            return null;
        }

        Class<?>[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = args[i].getClass();
        }
        return parameterTypes;
    }
}
