package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoCompletionCertificateArchiveVo;

import java.util.List;
import java.util.Optional;

public interface DemoLiveDemoCompletionCertificateArchiveRepository {

    DemoLiveDemoCompletionCertificateArchiveVo save(DemoLiveDemoCompletionCertificateArchiveVo archive);

    List<DemoLiveDemoCompletionCertificateArchiveVo> listRecentArchives(int limit);

    Optional<DemoLiveDemoCompletionCertificateArchiveVo> findById(String archiveId);
}
