package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.domain.vo.FixTaskModelCallVo;
import io.patchpilot.backend.task.service.FixTaskModelCallService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Profile("default")
public class InMemoryFixTaskModelCallService implements FixTaskModelCallService {

    private final List<FixTaskModelCallVo> modelCalls = new CopyOnWriteArrayList<>();

    @Override
    public FixTaskModelCallVo recordModelCall(
            String taskId,
            String provider,
            String model,
            String promptSummary,
            String responseSummary,
            int promptTokens,
            int completionTokens,
            boolean success,
            String errorMessage,
            Instant startedAt,
            Instant finishedAt
    ) {
        FixTaskModelCallVo modelCall = new FixTaskModelCallVo(
                UUID.randomUUID().toString(),
                taskId,
                provider,
                model,
                promptSummary,
                responseSummary,
                promptTokens,
                completionTokens,
                promptTokens + completionTokens,
                success,
                errorMessage,
                startedAt,
                finishedAt,
                FixTaskModelCallService.durationMs(startedAt, finishedAt)
        );
        modelCalls.add(modelCall);
        return modelCall;
    }

    @Override
    public List<FixTaskModelCallVo> listModelCalls(String taskId) {
        return modelCalls.stream()
                .filter(modelCall -> modelCall.taskId().equals(taskId))
                .sorted(Comparator.comparing(FixTaskModelCallVo::startedAt))
                .toList();
    }
}
