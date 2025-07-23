package com.logmaster.synchronization;

import com.logmaster.domain.Task;
import lombok.NonNull;

public interface Verifier {
    boolean supports(@NonNull Task task);
    boolean verify(@NonNull Task task);
}
