package io.patchpilot.backend.safety.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import io.patchpilot.backend.safety.domain.OperatorSafetyAuditEntity;
import io.patchpilot.backend.safety.domain.OperatorSafetyAuditVo;
import io.patchpilot.backend.safety.domain.RecordOperatorSafetyAuditCommand;
import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;
import io.patchpilot.backend.safety.mapper.OperatorSafetyAuditMapper;
import io.patchpilot.backend.safety.service.impl.MyBatisOperatorSafetyAuditService;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisOperatorSafetyAuditServiceTests {

    private final OperatorSafetyAuditMapper auditMapper = mock(OperatorSafetyAuditMapper.class);
    private final OperatorSafetyAuditService auditService = new MyBatisOperatorSafetyAuditService(auditMapper);

    @BeforeAll
    static void initializeMyBatisPlusMetadata() {
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), ""),
                OperatorSafetyAuditEntity.class
        );
    }

    @Test
    void should_insert_operator_safety_audit() {
        when(auditMapper.insert(any(OperatorSafetyAuditEntity.class))).thenReturn(1);
        ArgumentCaptor<OperatorSafetyAuditEntity> entityCaptor =
                ArgumentCaptor.forClass(OperatorSafetyAuditEntity.class);

        OperatorSafetyAuditVo audit = auditService.recordSafetyAudit(new RecordOperatorSafetyAuditCommand(
                "MANUAL_QUARANTINE_CREATED",
                "TRIGGER_QUARANTINE",
                "quarantine-1",
                TriggerQuarantineScope.TRIGGER_USER,
                "Drive-By-User",
                "local-admin",
                "Operator blocked noisy demo trigger user"
        ));

        verify(auditMapper).insert(entityCaptor.capture());
        OperatorSafetyAuditEntity entity = entityCaptor.getValue();
        assertThat(entity.getId()).isNotBlank();
        assertThat(entity.getAction()).isEqualTo("MANUAL_QUARANTINE_CREATED");
        assertThat(entity.getResourceType()).isEqualTo("TRIGGER_QUARANTINE");
        assertThat(entity.getResourceId()).isEqualTo("quarantine-1");
        assertThat(entity.getScope()).isEqualTo("TRIGGER_USER");
        assertThat(entity.getScopeKey()).isEqualTo("drive-by-user");
        assertThat(entity.getOperator()).isEqualTo("local-admin");
        assertThat(entity.getReason()).isEqualTo("Operator blocked noisy demo trigger user");
        assertThat(audit.scopeKey()).isEqualTo("drive-by-user");
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void should_query_recent_operator_safety_audits_newest_first() {
        OperatorSafetyAuditEntity entity = new OperatorSafetyAuditEntity();
        entity.setId("audit-1");
        entity.setAction("TRIGGER_QUARANTINE_RELEASED");
        entity.setResourceType("TRIGGER_QUARANTINE");
        entity.setResourceId("quarantine-1");
        entity.setScope("TRIGGER_USER");
        entity.setScopeKey("drive-by-user");
        entity.setOperator("local-admin");
        entity.setReason("False positive during demo");
        entity.setCreatedAt(Instant.parse("2026-06-24T01:05:00Z"));
        when(auditMapper.selectList(any())).thenReturn(List.of(entity));
        ArgumentCaptor<LambdaQueryWrapper<OperatorSafetyAuditEntity>> queryCaptor =
                ArgumentCaptor.forClass((Class) LambdaQueryWrapper.class);

        List<OperatorSafetyAuditVo> audits = auditService.listSafetyAudits(20);

        verify(auditMapper).selectList(queryCaptor.capture());
        assertThat(queryCaptor.getValue().getSqlSegment())
                .contains("ORDER BY")
                .contains("created_at")
                .contains("DESC")
                .contains("LIMIT 20");
        assertThat(audits).singleElement()
                .satisfies(audit -> assertThat(audit.action()).isEqualTo("TRIGGER_QUARANTINE_RELEASED"));
    }
}
