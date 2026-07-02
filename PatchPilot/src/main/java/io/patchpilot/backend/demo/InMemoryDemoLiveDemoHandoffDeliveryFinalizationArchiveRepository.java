package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffDeliveryFinalizationArchiveVo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryDemoLiveDemoHandoffDeliveryFinalizationArchiveRepository
        implements DemoLiveDemoHandoffDeliveryFinalizationArchiveRepository {

    private static final int MAX_ARCHIVES = 20;

    private final List<DemoLiveDemoHandoffDeliveryFinalizationArchiveVo> archives =
            new CopyOnWriteArrayList<>();

    @Override
    public DemoLiveDemoHandoffDeliveryFinalizationArchiveVo save(
            DemoLiveDemoHandoffDeliveryFinalizationArchiveVo archive
    ) {
        archives.add(0, archive);
        trimArchives();
        return archive;
    }

    @Override
    public List<DemoLiveDemoHandoffDeliveryFinalizationArchiveVo> listRecentArchives(int limit) {
        return archives.stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<DemoLiveDemoHandoffDeliveryFinalizationArchiveVo> findById(String archiveId) {
        return archives.stream()
                .filter(archive -> archive.id().equals(archiveId))
                .findFirst();
    }

    private void trimArchives() {
        while (archives.size() > MAX_ARCHIVES) {
            archives.remove(archives.size() - 1);
        }
    }
}
