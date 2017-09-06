package ru.bool.cucumber.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by bool on 05.09.17.
 */
public class FieldInvoker implements InvocationHandler {
    private  final Object paramsContainer;
    public FieldInvoker(Object paramsContainer) {
        this.paramsContainer = paramsContainer;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class type = method.getReturnType();
        Field field = Arrays.stream(paramsContainer.getClass().getDeclaredFields()).filter(
                it ->it.getType().equals(type)
        ).findFirst().get();
        field.setAccessible(true);
        return field.get(paramsContainer);
    }
}
