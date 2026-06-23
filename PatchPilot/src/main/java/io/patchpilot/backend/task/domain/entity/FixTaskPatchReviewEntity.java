package io.patchpilot.backend.task.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("fix_task_patch_review")
public class FixTaskPatchReviewEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("task_id")
    private String taskId;

    @TableField("decision")
    private String decision;

    @TableField("reason")
    private String reason;

    @TableField("confidence")
    private String confidence;

    @TableField("required_follow_up")
    private String requiredFollowUp;

    @TableField("edited_files")
    private String editedFiles;

    @TableField("created_at")
    private Instant createdAt;
}
