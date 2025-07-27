package com.logmaster.domain.verification.diary;

import com.logmaster.domain.verification.Verification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AchievementDiaryVerification extends Verification {
    private @NonNull DiaryRegion region;
    private @NonNull DiaryDifficulty difficulty;
}
