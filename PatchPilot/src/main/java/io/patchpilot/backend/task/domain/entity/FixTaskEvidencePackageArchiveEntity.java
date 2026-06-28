package io.patchpilot.backend.task.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("fix_task_evidence_package_archive")
public class FixTaskEvidencePackageArchiveEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("task_id")
    private String taskId;

    @TableField("repository_owner")
    private String repositoryOwner;

    @TableField("repository_name")
    private String repositoryName;

    @TableField("issue_number")
    private Long issueNumber;

    @TableField("status")
    private String status;

    @TableField("pull_request_url")
    private String pullRequestUrl;

    @TableField("archived_at")
    private Instant archivedAt;

    @TableField("summary")
    private String summary;

    @TableField("report")
    private String report;
}
