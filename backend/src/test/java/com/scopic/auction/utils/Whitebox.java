package com.scopic.auction.utils;

import java.lang.reflect.Field;
import java.util.*;

public class Whitebox {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> T getFieldValue(Object o, String fieldName) {
        try {
            if (o instanceof Class) {
                Field field = ((Class) o).getDeclaredField(fieldName);
                field.setAccessible(true);
                return (T) field.get(null);
            }
            Field field = findField(o, fieldName);
            return (T) field.get(o);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    protected static Field findField(Object o, String fieldName) {
        Objects.requireNonNull(o, "instance may not be null");
        Objects.requireNonNull(fieldName, "field's name may not be null");
        try {
            Optional<Field> findFirst = getAllFields(new ArrayList<>(), o.getClass()).stream()
                    .filter(field -> fieldName.equals(field.getName())).findFirst();
            Field field = findFirst.get();
            field.setAccessible(true);
            return field;
        } catch (SecurityException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public static void setFieldValue(Object o, String fieldName, Object value) {
        try {
            if (o instanceof Class) {
                Field field = ((Class) o).getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(null, value);
                return;
            }
            findField(o, fieldName).set(o, value);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));
        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }
        return fields;
    }
}

