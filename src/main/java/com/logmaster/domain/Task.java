package com.logmaster.domain;

import com.logmaster.domain.verification.Verification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    private String id;
    private String name;
    private int displayItemId;
    private String tip;
    private String wikiLink;
    
    private @Nullable Verification verification;
}
