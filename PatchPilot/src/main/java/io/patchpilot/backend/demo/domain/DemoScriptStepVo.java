package io.patchpilot.backend.demo.domain;

public record DemoScriptStepVo(
        int order,
        String name,
        DemoReadinessStatus status,
        String operatorAction,
        String verificationCommand,
        String successCriteria,
        String troubleshootingPanel,
        String evidence
) {
}
