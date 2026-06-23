package io.patchpilot.backend.agent.workflow;

import io.patchpilot.backend.agent.tool.FileReadTool;
import io.patchpilot.backend.agent.tool.FileWriteTool;
import io.patchpilot.backend.agent.workflow.domain.FileEditContext;
import io.patchpilot.backend.agent.workflow.domain.FileEditPlan;
import io.patchpilot.backend.agent.workflow.domain.FixPlan;
import io.patchpilot.backend.agent.workflow.domain.PatchWorkflowResult;
import io.patchpilot.backend.agent.workflow.domain.PatchReview;
import io.patchpilot.backend.agent.workflow.domain.PatchReviewDecision;
import io.patchpilot.backend.agent.workflow.domain.ProposedFileEdit;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.FixTaskPatchReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class PlannedPatchWorkflow {

    private static final Pattern REPLACE_INSTRUCTION = Pattern.compile("(?:^|\\s)replace\\s+(\\S+)\\s+(.+)", Pattern.DOTALL);
    private static final int MAX_EDIT_FILES = 3;
    private static final int MAX_FILE_CONTEXT_CHARS = 50_000;
    private static final int MAX_EDIT_CONTENT_CHARS = 80_000;

    private final FileWriteTool fileWriteTool;
    private final FileReadTool fileReadTool;
    private final FileEditPlanGenerator fileEditPlanGenerator;
    private final PatchReviewGenerator patchReviewGenerator;
    private final FixTaskPatchReviewService patchReviewService;

    public PatchWorkflowResult apply(FixTaskVo task, Path repositoryDir, FixPlan fixPlan) {
        Optional<ReplacementInstruction> instruction = replacementInstruction(task.triggerComment());
        if (instruction.isPresent()) {
            ReplacementInstruction replacement = instruction.get();
            if (!fixPlan.targetFiles().contains(replacement.relativePath())) {
                throw new IllegalArgumentException("Replacement target is not listed in fix plan: " + replacement.relativePath());
            }

            fileWriteTool.write(repositoryDir, replacement.relativePath(), replacement.content());
            return new PatchWorkflowResult(true, "Replaced " + replacement.relativePath() + " from planned instruction");
        }

        List<FileEditContext> fileContexts = fileContexts(repositoryDir, fixPlan);
        FileEditPlan fileEditPlan = fileEditPlanGenerator.generateEdits(task, fixPlan, fileContexts);
        List<ProposedFileEdit> edits = fileEditPlan.edits();
        if (edits.isEmpty()) {
            return new PatchWorkflowResult(false, "No model-generated file edits found");
        }
        if (edits.size() > MAX_EDIT_FILES) {
            throw new IllegalArgumentException("Model edit plan has too many files: " + edits.size());
        }
        for (ProposedFileEdit edit : edits) {
            validateEdit(edit, fixPlan);
        }
        PatchReview review = patchReviewGenerator.review(task, fixPlan, fileContexts, edits);
        recordPatchReview(task, review, edits);
        if (review.decision() == PatchReviewDecision.REJECT) {
            throw new IllegalStateException("Model patch review rejected generated edits: " + review.reason());
        }
        for (ProposedFileEdit edit : edits) {
            fileWriteTool.write(repositoryDir, edit.path(), edit.content());
        }
        return new PatchWorkflowResult(true, appliedSummary(edits, review));
    }

    private List<FileEditContext> fileContexts(Path repositoryDir, FixPlan fixPlan) {
        List<FileEditContext> contexts = new ArrayList<>();
        for (String targetFile : fixPlan.targetFiles()) {
            validateAllowedPath(targetFile, "Fix plan target");
            String content = fileReadTool.read(repositoryDir, targetFile);
            if (content.length() > MAX_FILE_CONTEXT_CHARS) {
                throw new IllegalArgumentException("Fix plan target is too large for model editing: " + targetFile);
            }
            contexts.add(new FileEditContext(targetFile, content));
        }
        return contexts;
    }

    private void validateEdit(ProposedFileEdit edit, FixPlan fixPlan) {
        if (!fixPlan.targetFiles().contains(edit.path())) {
            throw new IllegalArgumentException("Model edit target is not listed in fix plan: " + edit.path());
        }
        validateAllowedPath(edit.path(), "Model edit target");
        if (!StringUtils.hasText(edit.content())) {
            throw new IllegalArgumentException("Model edit content must not be blank: " + edit.path());
        }
        if (edit.content().length() > MAX_EDIT_CONTENT_CHARS) {
            throw new IllegalArgumentException("Model edit content is too large: " + edit.path());
        }
    }

    private void validateAllowedPath(String relativePath, String subject) {
        if (isSensitivePath(relativePath)) {
            throw new IllegalArgumentException(subject + " is sensitive and cannot be modified: " + relativePath);
        }
    }

    private boolean isSensitivePath(String relativePath) {
        return relativePath.equals(".env")
                || relativePath.startsWith(".env.")
                || relativePath.startsWith(".git/")
                || relativePath.startsWith(".github/workflows/")
                || relativePath.endsWith(".pem")
                || relativePath.endsWith(".key");
    }

    private String appliedSummary(List<ProposedFileEdit> edits, PatchReview review) {
        String files = edits.stream()
                .map(ProposedFileEdit::path)
                .reduce((left, right) -> left + ", " + right)
                .orElse("");
        String noun = edits.size() == 1 ? "edit" : "edits";
        return "Applied " + edits.size() + " model-generated file " + noun + " after review " + review.decision() + ": " + files;
    }

    private void recordPatchReview(FixTaskVo task, PatchReview review, List<ProposedFileEdit> edits) {
        patchReviewService.recordPatchReview(
                task.id(),
                review.decision().name(),
                review.reason(),
                review.confidence(),
                review.requiredFollowUp(),
                edits.stream().map(ProposedFileEdit::path).toList(),
                Instant.now()
        );
    }

    private Optional<ReplacementInstruction> replacementInstruction(String triggerComment) {
        if (!StringUtils.hasText(triggerComment)) {
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
