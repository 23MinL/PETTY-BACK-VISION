package io.github.petty.pipeline.controller;

import io.github.petty.pipeline.util.TogetherPromptBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PipelineController {

    private final TogetherPromptBuilder togetherPromptBuilder;

    @GetMapping("/pipeline")
    public String showPipelineForm() {
        return "pipeline";
    }

    @PostMapping("/pipeline")
    public String processPipeline(
            // form 으로 받은 임시 Vision 보고서
            @RequestParam("visionReport") String visionReport,
            // form 으로 받은 사용자 위치
            @RequestParam("location") String location,
            Model model
    ) {
        try {
            String prompt = togetherPromptBuilder.buildPrompt(visionReport, location);
            model.addAttribute("recommendation", prompt);
            return "pipeline";

        } catch (Exception e) {
            log.error("프롬프트 제작 중 오류 발생", e);
            model.addAttribute("error", "결과를 받아오지 못했습니다.");
            return "pipeline";
        }
    }
}
