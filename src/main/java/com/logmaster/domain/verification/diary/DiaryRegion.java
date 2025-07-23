package com.logmaster.domain.verification.diary;

import com.logmaster.util.EnumUtils;
import com.logmaster.util.StringUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum DiaryRegion {
    KARAMJA(0),
    ARDOUGNE(1),
    FALADOR(2),
    FREMENNIK(3),
    KANDARIN(4),
    DESERT(5),
    LUMBRIDGE_AND_DRAYNOR(6),
    MORYTANIA(7),
    VARROCK(8),
    WILDERNESS(9),
    WESTERN_PROVINCES(10),
    KOUREND_AND_KEBOS(11);

    private final int id;

    public static DiaryRegion fromString(String methodStr) throws IllegalArgumentException {
        return EnumUtils.fromString(DiaryRegion.class, methodStr);
    }

    @Override
    public String toString() {
        return StringUtils.kebabCase(this.name());
    }
}
