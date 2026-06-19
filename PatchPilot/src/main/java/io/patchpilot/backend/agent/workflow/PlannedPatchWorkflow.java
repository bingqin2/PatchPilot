package io.patchpilot.backend.agent.workflow;

import io.patchpilot.backend.agent.tool.FileWriteTool;
import io.patchpilot.backend.agent.workflow.domain.FixPlan;
import io.patchpilot.backend.agent.workflow.domain.PatchWorkflowResult;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;

import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlannedPatchWorkflow {

    private static final Pattern REPLACE_INSTRUCTION = Pattern.compile("(?:^|\\s)replace\\s+(\\S+)\\s+(.+)", Pattern.DOTALL);

    private final FileWriteTool fileWriteTool;

    public PlannedPatchWorkflow(FileWriteTool fileWriteTool) {
        this.fileWriteTool = fileWriteTool;
    }

    public PatchWorkflowResult apply(FixTaskVo task, Path repositoryDir, FixPlan fixPlan) {
        Optional<ReplacementInstruction> instruction = replacementInstruction(task.triggerComment());
        if (instruction.isEmpty()) {
            return new PatchWorkflowResult(false, "No planned replace instruction found");
        }

        ReplacementInstruction replacement = instruction.get();
        if (!fixPlan.targetFiles().contains(replacement.relativePath())) {
            throw new IllegalArgumentException("Replacement target is not listed in fix plan: " + replacement.relativePath());
        }

        fileWriteTool.write(repositoryDir, replacement.relativePath(), replacement.content());
        return new PatchWorkflowResult(true, "Replaced " + replacement.relativePath() + " from planned instruction");
    }

    private Optional<ReplacementInstruction> replacementInstruction(String triggerComment) {
        if (triggerComment == null || triggerComment.isBlank()) {
            return Optional.empty();
        }
        Matcher matcher = REPLACE_INSTRUCTION.matcher(triggerComment);
        if (!matcher.find()) {
            return Optional.empty();
        }
        return Optional.of(new ReplacementInstruction(matcher.group(1), matcher.group(2)));
    }

    private record ReplacementInstruction(String relativePath, String content) {
    }
}
