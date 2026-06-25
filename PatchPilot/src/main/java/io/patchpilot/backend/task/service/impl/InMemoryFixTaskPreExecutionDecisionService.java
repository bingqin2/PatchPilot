package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.domain.bo.RecordFixTaskPreExecutionDecisionCommand;
import io.patchpilot.backend.task.domain.vo.FixTaskPreExecutionDecisionVo;
import io.patchpilot.backend.task.service.FixTaskPreExecutionDecisionService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Profile("default")
public class InMemoryFixTaskPreExecutionDecisionService implements FixTaskPreExecutionDecisionService {

    private final List<FixTaskPreExecutionDecisionVo> decisions = new CopyOnWriteArrayList<>();

    @Override
    public FixTaskPreExecutionDecisionVo recordDecision(RecordFixTaskPreExecutionDecisionCommand command) {
        FixTaskPreExecutionDecisionVo decision = new FixTaskPreExecutionDecisionVo(
                UUID.randomUUID().toString(),
                command.taskId(),
                command.source(),
                command.finalDecision(),
                command.safetyDecision(),
                command.activeTaskDecision(),
                command.quarantineDecision(),
                command.rateLimitDecision(),
                command.triggerIntentDecision(),
                command.issueContextLoaded(),
                command.createdAt()
        );
        decisions.add(decision);
        return decision;
    }

    @Override
    public Optional<FixTaskPreExecutionDecisionVo> findLatestDecision(String taskId) {
        return decisions.stream()
                .filter(decision -> decision.taskId().equals(taskId))
                .max(Comparator.comparing(FixTaskPreExecutionDecisionVo::createdAt));
    }

    @Override
    public List<FixTaskPreExecutionDecisionVo> listRecentDecisions(int limit) {
        return decisions.stream()
                .sorted(Comparator.comparing(FixTaskPreExecutionDecisionVo::createdAt).reversed())
                .limit(Math.max(0, limit))
                .toList();
    }
}
