package io.patchpilot.backend.language;

@FunctionalInterface
public interface ExecutableAvailabilityChecker {

    boolean isAvailable(String executable);
}
