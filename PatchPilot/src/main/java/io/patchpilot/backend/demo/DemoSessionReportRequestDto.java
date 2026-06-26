package io.patchpilot.backend.demo;

import java.util.List;

public record DemoSessionReportRequestDto(
        List<DemoPreparedLaunchCommandRequestDto> preparedLaunchCommands,
        List<DemoArchivedLaunchOutcomeRequestDto> archivedLaunchOutcomes
) {

    public DemoSessionReportRequestDto {
        preparedLaunchCommands = preparedLaunchCommands == null ? List.of() : List.copyOf(preparedLaunchCommands);
        archivedLaunchOutcomes = archivedLaunchOutcomes == null ? List.of() : List.copyOf(archivedLaunchOutcomes);
    }

    public DemoSessionReportRequestDto(List<DemoPreparedLaunchCommandRequestDto> preparedLaunchCommands) {
        this(preparedLaunchCommands, List.of());
    }
}
