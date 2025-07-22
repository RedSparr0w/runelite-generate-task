package com.logmaster.diary;

import java.util.HashMap;
import java.util.Map;

import net.runelite.api.gameval.VarbitID;

/**
 * Achievement diary mapping from old varbit/value format to new area/tier format
 * Auto-generated from JSON files
 */
public class AchievementDiaryMapping {
    
    public static class DiaryInfo {
        public final int varbit;
        public final int value;
        public final String region;
        public final String difficulty;
        
        public DiaryInfo(int varbit, int value, String region, String difficulty) {
            this.varbit = varbit;
            this.value = value;
            this.region = region;
            this.difficulty = difficulty;
        }
    }
    
    private static final Map<String, DiaryInfo> DIARY_MAPPING = new HashMap<>();
    
    static {
        DIARY_MAPPING.put("ardougne_easy", new DiaryInfo(VarbitID.ARDOUGNE_DIARY_EASY_COMPLETE, 1, "ardougne", "easy"));
        DIARY_MAPPING.put("ardougne_medium", new DiaryInfo(VarbitID.ARDOUGNE_DIARY_MEDIUM_COMPLETE, 1, "ardougne", "medium"));
        DIARY_MAPPING.put("ardougne_hard", new DiaryInfo(VarbitID.ARDOUGNE_DIARY_HARD_COMPLETE, 1, "ardougne", "hard"));
        DIARY_MAPPING.put("ardougne_elite", new DiaryInfo(VarbitID.ARDOUGNE_DIARY_ELITE_COMPLETE, 1, "ardougne", "elite"));
        DIARY_MAPPING.put("desert_easy", new DiaryInfo(VarbitID.DESERT_DIARY_EASY_COMPLETE, 1, "desert", "easy"));
        DIARY_MAPPING.put("desert_medium", new DiaryInfo(VarbitID.DESERT_DIARY_MEDIUM_COMPLETE, 1, "desert", "medium"));
        DIARY_MAPPING.put("desert_hard", new DiaryInfo(VarbitID.DESERT_DIARY_HARD_COMPLETE, 1, "desert", "hard"));
        DIARY_MAPPING.put("desert_elite", new DiaryInfo(VarbitID.DESERT_DIARY_ELITE_COMPLETE, 1, "desert", "elite"));
        DIARY_MAPPING.put("falador_easy", new DiaryInfo(VarbitID.FALADOR_DIARY_EASY_COMPLETE, 1, "falador", "easy"));
        DIARY_MAPPING.put("falador_medium", new DiaryInfo(VarbitID.FALADOR_DIARY_MEDIUM_COMPLETE, 1, "falador", "medium"));
        DIARY_MAPPING.put("falador_hard", new DiaryInfo(VarbitID.FALADOR_DIARY_HARD_COMPLETE, 1, "falador", "hard"));
        DIARY_MAPPING.put("falador_elite", new DiaryInfo(VarbitID.FALADOR_DIARY_ELITE_COMPLETE, 1, "falador", "elite"));
        DIARY_MAPPING.put("fremennik_easy", new DiaryInfo(VarbitID.FREMENNIK_DIARY_EASY_COMPLETE, 1, "fremennik", "easy"));
        DIARY_MAPPING.put("fremennik_medium", new DiaryInfo(VarbitID.FREMENNIK_DIARY_MEDIUM_COMPLETE, 1, "fremennik", "medium"));
        DIARY_MAPPING.put("fremennik_hard", new DiaryInfo(VarbitID.FREMENNIK_DIARY_HARD_COMPLETE, 1, "fremennik", "hard"));
        DIARY_MAPPING.put("fremennik_elite", new DiaryInfo(VarbitID.FREMENNIK_DIARY_ELITE_COMPLETE, 1, "fremennik", "elite"));
        DIARY_MAPPING.put("kandarin_easy", new DiaryInfo(VarbitID.KANDARIN_DIARY_EASY_COMPLETE, 1, "kandarin", "easy"));
        DIARY_MAPPING.put("kandarin_medium", new DiaryInfo(VarbitID.KANDARIN_DIARY_MEDIUM_COMPLETE, 1, "kandarin", "medium"));
        DIARY_MAPPING.put("kandarin_hard", new DiaryInfo(VarbitID.KANDARIN_DIARY_HARD_COMPLETE, 1, "kandarin", "hard"));
        DIARY_MAPPING.put("kandarin_elite", new DiaryInfo(VarbitID.KANDARIN_DIARY_ELITE_COMPLETE, 1, "kandarin", "elite"));
        DIARY_MAPPING.put("karamja_easy", new DiaryInfo(VarbitID.ATJUN_EASY_DONE, 2, "karamja", "easy"));
        DIARY_MAPPING.put("karamja_medium", new DiaryInfo(VarbitID.ATJUN_MED_DONE, 2, "karamja", "medium"));
        DIARY_MAPPING.put("karamja_hard", new DiaryInfo(VarbitID.ATJUN_HARD_DONE, 2, "karamja", "hard"));
        DIARY_MAPPING.put("karamja_elite", new DiaryInfo(VarbitID.KARAMJA_DIARY_ELITE_COMPLETE, 1, "karamja", "elite"));
        DIARY_MAPPING.put("kourend-and-kebos_easy", new DiaryInfo(VarbitID.KOUREND_DIARY_EASY_COMPLETE, 1, "kourend-and-kebos", "easy"));
        DIARY_MAPPING.put("kourend-and-kebos_medium", new DiaryInfo(VarbitID.KOUREND_DIARY_MEDIUM_COMPLETE, 1, "kourend-and-kebos", "medium"));
        DIARY_MAPPING.put("kourend-and-kebos_hard", new DiaryInfo(VarbitID.KOUREND_DIARY_HARD_COMPLETE, 1, "kourend-and-kebos", "hard"));
        DIARY_MAPPING.put("kourend-and-kebos_elite", new DiaryInfo(VarbitID.KOUREND_DIARY_ELITE_COMPLETE, 1, "kourend-and-kebos", "elite"));
        DIARY_MAPPING.put("lumbridge-and-draynor_easy", new DiaryInfo(VarbitID.LUMBRIDGE_DIARY_EASY_COMPLETE, 1, "lumbridge-and-draynor", "easy"));
        DIARY_MAPPING.put("lumbridge-and-draynor_medium", new DiaryInfo(VarbitID.LUMBRIDGE_DIARY_MEDIUM_COMPLETE, 1, "lumbridge-and-draynor", "medium"));
        DIARY_MAPPING.put("lumbridge-and-draynor_hard", new DiaryInfo(VarbitID.LUMBRIDGE_DIARY_HARD_COMPLETE, 1, "lumbridge-and-draynor", "hard"));
        DIARY_MAPPING.put("lumbridge-and-draynor_elite", new DiaryInfo(VarbitID.LUMBRIDGE_DIARY_ELITE_COMPLETE, 1, "lumbridge-and-draynor", "elite"));
        DIARY_MAPPING.put("morytania_easy", new DiaryInfo(VarbitID.MORYTANIA_DIARY_EASY_COMPLETE, 1, "morytania", "easy"));
        DIARY_MAPPING.put("morytania_medium", new DiaryInfo(VarbitID.MORYTANIA_DIARY_MEDIUM_COMPLETE, 1, "morytania", "medium"));
        DIARY_MAPPING.put("morytania_hard", new DiaryInfo(VarbitID.MORYTANIA_DIARY_HARD_COMPLETE, 1, "morytania", "hard"));
        DIARY_MAPPING.put("morytania_elite", new DiaryInfo(VarbitID.MORYTANIA_DIARY_ELITE_COMPLETE, 1, "morytania", "elite"));
        DIARY_MAPPING.put("varrock_easy", new DiaryInfo(VarbitID.VARROCK_DIARY_EASY_COMPLETE, 1, "varrock", "easy"));
        DIARY_MAPPING.put("varrock_medium", new DiaryInfo(VarbitID.VARROCK_DIARY_MEDIUM_COMPLETE, 1, "varrock", "medium"));
        DIARY_MAPPING.put("varrock_hard", new DiaryInfo(VarbitID.VARROCK_DIARY_HARD_COMPLETE, 1, "varrock", "hard"));
        DIARY_MAPPING.put("varrock_elite", new DiaryInfo(VarbitID.VARROCK_DIARY_ELITE_COMPLETE, 1, "varrock", "elite"));
        DIARY_MAPPING.put("western-provinces_easy", new DiaryInfo(VarbitID.WESTERN_DIARY_EASY_COMPLETE, 1, "western-provinces", "easy"));
        DIARY_MAPPING.put("western-provinces_medium", new DiaryInfo(VarbitID.WESTERN_DIARY_MEDIUM_COMPLETE, 1, "western-provinces", "medium"));
        DIARY_MAPPING.put("western-provinces_hard", new DiaryInfo(VarbitID.WESTERN_DIARY_HARD_COMPLETE, 1, "western-provinces", "hard"));
        DIARY_MAPPING.put("western-provinces_elite", new DiaryInfo(VarbitID.WESTERN_DIARY_ELITE_COMPLETE, 1, "western-provinces", "elite"));
        DIARY_MAPPING.put("wilderness_easy", new DiaryInfo(VarbitID.WILDERNESS_DIARY_EASY_COMPLETE, 1, "wilderness", "easy"));
        DIARY_MAPPING.put("wilderness_medium", new DiaryInfo(VarbitID.WILDERNESS_DIARY_MEDIUM_COMPLETE, 1, "wilderness", "medium"));
        DIARY_MAPPING.put("wilderness_hard", new DiaryInfo(VarbitID.WILDERNESS_DIARY_HARD_COMPLETE, 1, "wilderness", "hard"));
        DIARY_MAPPING.put("wilderness_elite", new DiaryInfo(VarbitID.WILDERNESS_DIARY_ELITE_COMPLETE, 1, "wilderness", "elite"));
    }

    public static DiaryInfo getDiaryInfo(String area, String tier) {
        return DIARY_MAPPING.get(area + "_" + tier);
    }
}