package io.patchpilot.backend.task.domain.enums;

import java.util.Arrays;

public enum FixTaskSort {
    CREATED_AT_DESC("createdAtDesc"),
    CREATED_AT_ASC("createdAtAsc");

    private final String apiValue;

    FixTaskSort(String apiValue) {
        this.apiValue = apiValue;
    }

    public String apiValue() {
        return apiValue;
    }

    public static FixTaskSort fromApiValue(String value) {
        return Arrays.stream(values())
                .filter(sort -> sort.apiValue.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("sort must be createdAtDesc or createdAtAsc"));
    }
}
