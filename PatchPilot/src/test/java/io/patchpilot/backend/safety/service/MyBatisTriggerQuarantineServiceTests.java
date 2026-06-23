package io.patchpilot.backend.safety.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import io.patchpilot.backend.safety.domain.RecordTriggerQuarantineCommand;
import io.patchpilot.backend.safety.domain.TriggerQuarantineEntity;
import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;
import io.patchpilot.backend.safety.domain.TriggerQuarantineVo;
import io.patchpilot.backend.safety.mapper.TriggerQuarantineMapper;
import io.patchpilot.backend.safety.service.impl.MyBatisTriggerQuarantineService;
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

class MyBatisTriggerQuarantineServiceTests {

    private final TriggerQuarantineMapper quarantineMapper = mock(TriggerQuarantineMapper.class);
    private final TriggerQuarantineRecordService quarantineService = new MyBatisTriggerQuarantineService(quarantineMapper);

    @BeforeAll
    static void initializeMyBatisPlusMetadata() {
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), ""),
                TriggerQuarantineEntity.class
        );
    }

    @Test
    void should_insert_new_trigger_quarantine() {
        when(quarantineMapper.selectList(any())).thenReturn(List.of());
        when(quarantineMapper.insert(any(TriggerQuarantineEntity.class))).thenReturn(1);
        ArgumentCaptor<TriggerQuarantineEntity> entityCaptor =
                ArgumentCaptor.forClass(TriggerQuarantineEntity.class);

        TriggerQuarantineVo quarantine = quarantineService.recordQuarantine(new RecordTriggerQuarantineCommand(
                TriggerQuarantineScope.TRIGGER_USER,
                "Alice",
                "Unsafe request rejected: trigger user is temporarily quarantined",
                "ABUSE_QUARANTINED",
                5,
                600_000,
                Instant.parse("2026-06-24T00:30:00Z")
        ));

        verify(quarantineMapper).insert(entityCaptor.capture());
        TriggerQuarantineEntity entity = entityCaptor.getValue();
        assertThat(entity.getId()).isNotBlank();
        assertThat(entity.getScope()).isEqualTo("TRIGGER_USER");
        assertThat(entity.getScopeKey()).isEqualTo("alice");
        assertThat(entity.getReason()).isEqualTo("Unsafe request rejected: trigger user is temporarily quarantined");
        assertThat(entity.getCategory()).isEqualTo("ABUSE_QUARANTINED");
        assertThat(entity.getEvidenceCount()).isEqualTo(5);
        assertThat(entity.getWindowMs()).isEqualTo(600_000L);
        assertThat(entity.getExpiresAt()).isEqualTo(Instant.parse("2026-06-24T00:30:00Z"));
        assertThat(quarantine.scopeKey()).isEqualTo("alice");
        assertThat(quarantine.active()).isTrue();
    }

    @Test
    void should_extend_existing_trigger_quarantine() {
        TriggerQuarantineEntity existing = entity("quarantine-1", Instant.parse("2026-06-24T00:30:00Z"));
        when(quarantineMapper.selectList(any())).thenReturn(List.of(existing));
        when(quarantineMapper.updateById(any(TriggerQuarantineEntity.class))).thenReturn(1);
        ArgumentCaptor<TriggerQuarantineEntity> entityCaptor =
                ArgumentCaptor.forClass(TriggerQuarantineEntity.class);

        TriggerQuarantineVo quarantine = quarantineService.recordQuarantine(new RecordTriggerQuarantineCommand(
                TriggerQuarantineScope.TRIGGER_USER,
                "alice",
                "Unsafe request rejected: trigger user is temporarily quarantined",
                "ABUSE_QUARANTINED",
                7,
                900_000,
                Instant.parse("2026-06-24T01:00:00Z")
        ));

        verify(quarantineMapper).updateById(entityCaptor.capture());
        assertThat(entityCaptor.getValue().getId()).isEqualTo("quarantine-1");
        assertThat(entityCaptor.getValue().getEvidenceCount()).isEqualTo(7);
        assertThat(entityCaptor.getValue().getWindowMs()).isEqualTo(900_000L);
        assertThat(entityCaptor.getValue().getExpiresAt()).isEqualTo(Instant.parse("2026-06-24T01:00:00Z"));
        assertThat(quarantine.id()).isEqualTo("quarantine-1");
        assertThat(quarantine.evidenceCount()).isEqualTo(7);
    }

    @Test
    void should_find_active_quarantine() {
        TriggerQuarantineEntity entity = entity("quarantine-1", Instant.now().plusSeconds(1800));
        when(quarantineMapper.selectList(any())).thenReturn(List.of(entity));

        assertThat(quarantineService.findActiveQuarantine(TriggerQuarantineScope.TRIGGER_USER, "ALICE"))
                .hasValueSatisfying(quarantine -> {
                    assertThat(quarantine.id()).isEqualTo("quarantine-1");
                    assertThat(quarantine.scopeKey()).isEqualTo("alice");
                    assertThat(quarantine.active()).isTrue();
                });
    }

    @Test
    void should_list_quarantines_newest_first() {
        TriggerQuarantineEntity older = entity("older", Instant.now().plusSeconds(1800));
        older.setUpdatedAt(Instant.parse("2026-06-24T00:01:00Z"));
        TriggerQuarantineEntity newer = entity("newer", Instant.now().plusSeconds(1800));
        newer.setUpdatedAt(Instant.parse("2026-06-24T00:02:00Z"));
        when(quarantineMapper.selectList(any())).thenReturn(List.of(newer, older));

        List<TriggerQuarantineVo> quarantines = quarantineService.listQuarantines(true, 50);

        assertThat(quarantines)
                .extracting(TriggerQuarantineVo::id)
                .containsExactly("newer", "older");
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void should_order_query_before_limiting_quarantine_list() {
        when(quarantineMapper.selectList(any())).thenReturn(List.of());
        ArgumentCaptor<LambdaQueryWrapper<TriggerQuarantineEntity>> queryCaptor =
                ArgumentCaptor.forClass((Class) LambdaQueryWrapper.class);

        quarantineService.listQuarantines(true, 20);

        verify(quarantineMapper).selectList(queryCaptor.capture());
        assertThat(queryCaptor.getValue().getSqlSegment())
                .contains("expires_at")
                .contains("ORDER BY")
                .contains("updated_at")
                .contains("DESC")
                .contains("LIMIT 20");
    }

    private static TriggerQuarantineEntity entity(String id, Instant expiresAt) {
        TriggerQuarantineEntity entity = new TriggerQuarantineEntity();
        entity.setId(id);
        entity.setScope("TRIGGER_USER");
        entity.setScopeKey("alice");
        entity.setReason("Unsafe request rejected: trigger user is temporarily quarantined");
        entity.setCategory("ABUSE_QUARANTINED");
        entity.setEvidenceCount(5);
        entity.setWindowMs(600_000L);
        entity.setStartedAt(Instant.parse("2026-06-24T00:00:00Z"));
        entity.setExpiresAt(expiresAt);
        entity.setCreatedAt(Instant.parse("2026-06-24T00:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2026-06-24T00:00:00Z"));
        return entity;
    }
}
