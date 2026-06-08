package ru.itmo.prog.lab6.common.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public final class ConsoleInterrupt {
    private ConsoleInterrupt() {}

    public static boolean install(Runnable action) {
        try {
            Class<?> signalClass = Class.forName("sun.misc.Signal");
            Class<?> handlerClass = Class.forName("sun.misc.SignalHandler");
            Constructor<?> constructor = signalClass.getConstructor(String.class);
            Object signal = constructor.newInstance("INT");
            Object handler = Proxy.newProxyInstance(
                    handlerClass.getClassLoader(),
                    new Class<?>[]{handlerClass},
                    (proxy, method, args) -> invoke(action, method));
            Method handle = signalClass.getMethod("handle", signalClass, handlerClass);
            handle.invoke(null, signal, handler);
            return true;
        } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
            return false;
        }
    }

    private static Object invoke(Runnable action, Method method) {
        if (method.getDeclaringClass() == Object.class) {
            return objectMethod(method);
        }
        if ("handle".equals(method.getName())) {
            action.run();
        }
        return null;
    }

    private static Object objectMethod(Method method) {
        String name = method.getName();
        if ("toString".equals(name)) return "ConsoleInterrupt";
        if ("hashCode".equals(name)) return System.identityHashCode(ConsoleInterrupt.class);
        if ("equals".equals(name)) return false;
        return null;
    }
}
