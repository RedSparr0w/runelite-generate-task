package com.logmaster.domain.verification;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum VerificationMethod {
    COLLECTION_LOG,
    ACHIEVEMENT_DIARY;

    public static VerificationMethod fromMethodString(String methodStr) throws IllegalArgumentException {
        for (VerificationMethod method : VerificationMethod.values()) {
            if (method.toString().equals(methodStr)) {
                return method;
            }
        }

        String msg = String.format("Unknown verification method for string '%s'", methodStr);
        throw new IllegalArgumentException(msg);
    }

    @Override
    public String toString() {
        return this.name().toLowerCase().replace('_', '-');
    }
}
