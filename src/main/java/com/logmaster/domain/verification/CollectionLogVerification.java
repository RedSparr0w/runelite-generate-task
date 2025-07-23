package com.logmaster.domain.verification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CollectionLogVerification extends Verification {
    private @NonNull int[] itemIds;
    private int count;
}
