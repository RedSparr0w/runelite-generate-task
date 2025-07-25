package com.logmaster.util;

import lombok.NonNull;

public class EnumUtils {
    public static <T extends Enum<T>> @NonNull T fromString(@NonNull Class<T> enumClass, String name) {
        for (T e : enumClass.getEnumConstants()) {
            if (e.toString().equals(name)) {
                return e;
            }
        }

        String msg = String.format("No enum const %s for name '%s'", enumClass.getName(), name);
        throw new IllegalArgumentException(msg);
    }
}
