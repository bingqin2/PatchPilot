package io.patchpilot.backend.language;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.language.domain.SupportedLanguageAdapterVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/language-adapters")
@RequiredArgsConstructor
public class LanguageAdapterController {

    private final LanguageAdapterCatalogService languageAdapterCatalogService;

    @GetMapping
    public ApiResponse<List<SupportedLanguageAdapterVo>> listSupportedAdapters() {
        return ApiResponse.ok(languageAdapterCatalogService.listSupportedAdapters());
    }
}
