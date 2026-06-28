package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCertificateArchiveVo;

import java.util.List;
import java.util.Optional;

public interface DemoLaunchAcceptanceCertificateArchiveRepository {

    DemoLaunchAcceptanceCertificateArchiveVo save(DemoLaunchAcceptanceCertificateArchiveVo archive);

    List<DemoLaunchAcceptanceCertificateArchiveVo> listRecentArchives(int limit);

    Optional<DemoLaunchAcceptanceCertificateArchiveVo> findById(String archiveId);
}
