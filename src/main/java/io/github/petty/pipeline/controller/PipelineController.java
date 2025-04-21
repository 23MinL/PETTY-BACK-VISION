package io.github.petty.pipeline.controller;

import io.github.petty.pipeline.service.PromptGeneratorService;
import io.github.petty.pipeline.service.TogetherService;
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

    private final TogetherService togetherService;
    private final PromptGeneratorService promptGeneratorService;

    @GetMapping("/pipeline")
    public String showPipelineForm() {
        return "pipeline";
    }

    @PostMapping("/pipeline")
    public String processPipeline(
            @RequestParam("visionReport") String visionReport,
            @RequestParam("location") String location,
            Model model
    ) {
        try {
            String extractedPetInfoJson = togetherService.answer(
                    visionReport + " -> 이 문장에서 반려동물의 이름(name), 종(species), 무게(weight), 맹수 여부(is_danger(only true or false))를 JSON 형식으로 작성 + " +
                            "만약 반려동물의 종과 무게를 보았을 때, 입마개가 필요할 것 같다면 맹수 여부를 'true'로 작성 + " + "무게는 kg 단위를 반드시 포함"
            );

            String prompt = promptGeneratorService.generatePrompt(extractedPetInfoJson, location);

            model.addAttribute("recommendation", prompt); // 실제 추천 대신 prompt만 전달

            return "pipeline";

        } catch (Exception e) {
            log.error("프롬프트 제작 중 오류 발생", e);
            model.addAttribute("error", "결과를 받아오지 못했습니다.");
            return "pipeline";
        }
    }
}
