package io.patchpilot.backend.agent.workflow;

import io.patchpilot.backend.agent.tool.FileWriteTool;
import io.patchpilot.backend.agent.workflow.domain.PatchWorkflowResult;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;

import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimplePatchWorkflow implements PatchWorkflow {

    private static final Pattern TOUCH_INSTRUCTION = Pattern.compile("(?:^|\\s)touch\\s+(\\S+)");

    private final FileWriteTool fileWriteTool;

    public SimplePatchWorkflow(FileWriteTool fileWriteTool) {
        this.fileWriteTool = fileWriteTool;
    }

    @Override
    public PatchWorkflowResult apply(FixTaskVo task, Path repositoryDir) {
        Optional<String> relativePath = touchPath(task.triggerComment());
        if (relativePath.isEmpty()) {
            return new PatchWorkflowResult(false, "No deterministic patch instruction found");
        }

        String content = """
                # PatchPilot generated file

                Task: %s
                Issue: %s/%s#%d
                Triggered by: %s
                """.formatted(
                task.id(),
                task.repositoryOwner(),
                task.repositoryName(),
                task.issueNumber(),
                task.triggerUser()
        );
        fileWriteTool.write(repositoryDir, relativePath.get(), content);
        return new PatchWorkflowResult(true, "Created " + relativePath.get() + " from touch instruction");
    }

    private Optional<String> touchPath(String triggerComment) {
        if (triggerComment == null || triggerComment.isBlank()) {
            return Optional.empty();
        }
        Matcher matcher = TOUCH_INSTRUCTION.matcher(triggerComment);
        if (!matcher.find()) {
            return Optional.empty();
        }
        return Optional.of(matcher.group(1));
    }
}
