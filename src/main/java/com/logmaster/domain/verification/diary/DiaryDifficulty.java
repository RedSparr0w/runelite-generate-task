package com.logmaster.domain.verification.diary;

import com.logmaster.util.EnumUtils;
import com.logmaster.util.StringUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum DiaryDifficulty {
    EASY(2),
    MEDIUM(5),
    HARD(8),
    ELITE(11);

    private final int stackOffset;

    public static DiaryDifficulty fromString(String methodStr) throws IllegalArgumentException {
        return EnumUtils.fromString(DiaryDifficulty.class, methodStr);
    }

    @Override
    public String toString() {
        return StringUtils.kebabCase(this.name());
    }
}
