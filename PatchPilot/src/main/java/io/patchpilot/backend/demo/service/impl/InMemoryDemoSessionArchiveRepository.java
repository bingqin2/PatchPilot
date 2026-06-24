package io.patchpilot.backend.demo.service.impl;

import io.patchpilot.backend.demo.domain.DemoSessionArchiveVo;
import io.patchpilot.backend.demo.service.DemoSessionArchiveRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@Profile("default")
public class InMemoryDemoSessionArchiveRepository implements DemoSessionArchiveRepository {

    private static final int MAX_ARCHIVES = 20;

    private final List<DemoSessionArchiveVo> archives = new CopyOnWriteArrayList<>();

    @Override
    public DemoSessionArchiveVo save(DemoSessionArchiveVo archive) {
        archives.add(0, archive);
        trimArchives();
        return archive;
    }

    @Override
    public List<DemoSessionArchiveVo> listRecentArchives(int limit) {
        return archives.stream()
                .limit(limit)
                .toList();
    }

    private void trimArchives() {
        while (archives.size() > MAX_ARCHIVES) {
            archives.remove(archives.size() - 1);
        }
    }
}
