package com.logmaster.domain.verification;

import com.logmaster.util.EnumUtils;
import com.logmaster.util.StringUtils;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum VerificationMethod {
    COLLECTION_LOG,
    ACHIEVEMENT_DIARY;

    public static VerificationMethod fromString(String methodStr) throws IllegalArgumentException {
        return EnumUtils.fromString(VerificationMethod.class, methodStr);
    }

    @Override
    public String toString() {
        return StringUtils.kebabCase(this.name());
    }
}
