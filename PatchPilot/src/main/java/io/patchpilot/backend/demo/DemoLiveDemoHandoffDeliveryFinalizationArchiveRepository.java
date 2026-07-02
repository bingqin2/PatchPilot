package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffDeliveryFinalizationArchiveVo;

import java.util.List;
import java.util.Optional;

public interface DemoLiveDemoHandoffDeliveryFinalizationArchiveRepository {

    DemoLiveDemoHandoffDeliveryFinalizationArchiveVo save(
            DemoLiveDemoHandoffDeliveryFinalizationArchiveVo archive
    );

    List<DemoLiveDemoHandoffDeliveryFinalizationArchiveVo> listRecentArchives(int limit);

    Optional<DemoLiveDemoHandoffDeliveryFinalizationArchiveVo> findById(String archiveId);
}
