package io.patchpilot.backend.demo;

import java.util.List;

public record DemoSessionReportRequestDto(
        List<DemoPreparedLaunchCommandRequestDto> preparedLaunchCommands
) {

    public DemoSessionReportRequestDto {
        preparedLaunchCommands = preparedLaunchCommands == null ? List.of() : List.copyOf(preparedLaunchCommands);
    }
}
