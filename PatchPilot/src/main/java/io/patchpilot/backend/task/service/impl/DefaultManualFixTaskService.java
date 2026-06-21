package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.bo.CreateManualFixTaskCommand;
import io.patchpilot.backend.task.domain.enums.FixTaskTimelineEventType;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.FixTaskDispatcher;
import io.patchpilot.backend.task.service.FixTaskService;
import io.patchpilot.backend.task.service.FixTaskTimelineService;
import io.patchpilot.backend.task.service.ManualFixTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultManualFixTaskService implements ManualFixTaskService {

    private final FixTaskService fixTaskService;
    private final FixTaskTimelineService fixTaskTimelineService;
    private final FixTaskDispatcher fixTaskDispatcher;

    @Override
    public FixTaskVo createManualTask(CreateManualFixTaskCommand command) {
        fixTaskService.findActiveTaskForIssue(
                        command.repositoryOwner(),
                        command.repositoryName(),
                        command.issueNumber()
                )
                .ifPresent(activeTask -> {
                    throw new IllegalStateException("An active task already exists for this issue");
                });

        FixTaskVo task = fixTaskService.createFixTask(new CreateFixTaskCommand(
                command.repositoryOwner(),
                command.repositoryName(),
                command.issueNumber(),
                0,
                command.triggerUser(),
                command.triggerComment(),
                "manual-" + UUID.randomUUID(),
                0
        ));
        fixTaskTimelineService.recordEvent(
                task.id(),
                FixTaskTimelineEventType.TASK_CREATED,
                "Task accepted from dashboard manual creation"
        );
        fixTaskDispatcher.dispatch(task.id());
        return task;
    }
}
