package com.logmaster.domain.adapters;

import com.google.gson.*;
import com.logmaster.util.EnumUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

@Slf4j
@RequiredArgsConstructor
public class EnumAdapter<T extends Enum<T>> implements JsonDeserializer<T> {
    private final Class<T> clazz;

    @Override
    public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        return EnumUtils.fromString(clazz, jsonElement.getAsString());
    }
}
