package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.domain.vo.FixTaskToolCallVo;
import io.patchpilot.backend.task.service.FixTaskToolCallService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Profile("default")
public class InMemoryFixTaskToolCallService implements FixTaskToolCallService {

    private final List<FixTaskToolCallVo> toolCalls = new CopyOnWriteArrayList<>();

    @Override
    public FixTaskToolCallVo recordToolCall(
            String taskId,
            String toolName,
            String inputSummary,
            String outputSummary,
            boolean success,
            Instant startedAt,
            Instant finishedAt
    ) {
        FixTaskToolCallVo toolCall = new FixTaskToolCallVo(
                UUID.randomUUID().toString(),
                taskId,
                toolName,
                inputSummary,
                outputSummary,
                success,
                startedAt,
                finishedAt,
                FixTaskToolCallService.durationMs(startedAt, finishedAt)
        );
        toolCalls.add(toolCall);
        return toolCall;
    }

    @Override
    public List<FixTaskToolCallVo> listToolCalls(String taskId) {
        return toolCalls.stream()
                .filter(toolCall -> toolCall.taskId().equals(taskId))
                .sorted(Comparator.comparing(FixTaskToolCallVo::startedAt))
                .toList();
    }
}
