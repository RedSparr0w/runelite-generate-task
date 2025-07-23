package com.logmaster.domain.verification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AchievementDiaryVerification extends Verification {
    private @NonNull String region;
    private @NonNull String difficulty;
}
