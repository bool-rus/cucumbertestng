package ru.bool.cucumber.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Хэндлер для выдергивания значений полей через reflection
 * Usage: создаем интерфейс, где возвращаются объекты нужных нам типов
 * делаем прокси-класс с этим интерфейсом
 * Примеры смотреть здесь:
 * @see ru.bool.cucumber.reflection.faces.BackgroundContainer
 * Created by bool on 05.09.17.
 */
public class FieldInvoker implements InvocationHandler {
    private  final Object paramsContainer;

    /**
     * может работать непредсказуемо, если объект содержит несколько полей интересующего нас типа
     * @param paramsContainer - объект, поля которого хотим вытянуть через reflection
     */
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
