package io.patchpilot.backend.demo.domain;

public record DemoEndToEndAcceptanceMatrixItemVo(
        String category,
        String name,
        String status,
        String evidence,
        String gap,
        String nextAction
) {
}
