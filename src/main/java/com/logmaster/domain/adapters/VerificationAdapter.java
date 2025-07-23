package com.logmaster.domain.adapters;

import com.google.gson.*;
import com.logmaster.domain.verification.diary.AchievementDiaryVerification;
import com.logmaster.domain.verification.clog.CollectionLogVerification;
import com.logmaster.domain.verification.Verification;
import com.logmaster.domain.verification.VerificationMethod;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

@Slf4j
public class VerificationAdapter implements JsonDeserializer<Verification> {
    private static final String DISCRIMINATOR_FIELD = "method";

    @Override
    public Verification deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        if (!obj.has(DISCRIMINATOR_FIELD)) {
            log.error("Verification object has no required discriminator field '{}'", DISCRIMINATOR_FIELD);
            return null;
        }

        String methodStr = obj.get(DISCRIMINATOR_FIELD).getAsString();
        VerificationMethod method;
        try {
            method = VerificationMethod.fromString(methodStr);
        } catch (IllegalArgumentException e) {
            log.error("Verification object has unknown discriminator field value '{}'", methodStr);
            return null;
        }

        switch (method) {
            case COLLECTION_LOG:
                return context.deserialize(jsonElement, CollectionLogVerification.class);

            case ACHIEVEMENT_DIARY:
                return context.deserialize(jsonElement, AchievementDiaryVerification.class);
        }

        log.error("Unhandled verification method '{}'", method);
        return null;
    }
}
