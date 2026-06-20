package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.domain.vo.FixTaskTestRunVo;
import io.patchpilot.backend.task.service.FixTaskTestRunService;
import io.patchpilot.backend.task.service.LogSummary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Profile("default")
public class InMemoryFixTaskTestRunService implements FixTaskTestRunService {

    private final List<FixTaskTestRunVo> testRuns = new CopyOnWriteArrayList<>();

    @Override
    public FixTaskTestRunVo recordTestRun(
            String taskId,
            String command,
            int exitCode,
            String output,
            Instant startedAt,
            Instant finishedAt
    ) {
        FixTaskTestRunVo testRun = new FixTaskTestRunVo(
                UUID.randomUUID().toString(),
                taskId,
                command,
                exitCode,
                LogSummary.truncateTestRunOutput(output),
                startedAt,
                finishedAt,
                FixTaskTestRunService.durationMs(startedAt, finishedAt)
        );
        testRuns.add(testRun);
        return testRun;
    }

    @Override
    public List<FixTaskTestRunVo> listTestRuns(String taskId) {
        return testRuns.stream()
                .filter(testRun -> testRun.taskId().equals(taskId))
                .sorted(Comparator.comparing(FixTaskTestRunVo::startedAt))
                .toList();
    }
}
