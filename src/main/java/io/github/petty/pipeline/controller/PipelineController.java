package io.github.petty.pipeline.controller;

import io.github.petty.pipeline.service.GeminiService;
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
    private final GeminiService geminiService;

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
            // 1. Together API로 반려동물 정보 추출
            String extractedPetInfoJson = togetherService.answer(
                    visionReport + " -> 이 문장에서 반려동물의 이름, 종, 무게, 맹수 여부(only true or false)를 JSON 형식으로 작성 + " +
                            "만약 반려동물의 종과 무게를 보았을 때, 입마개가 필요할 것 같다면 맹수 여부를 'true'로 작성"
            );

            // 2. Gemini API 호출 프롬프트 작성
            String prompt = String.format("""
                    반려동물 JSON 정보:
                    %s
                    
                    대한민국 %s 지역의 반려동물 동반 여행지를 추천받기 위한 프롬프트를 작성.
                    반려동물 정보는 텍스트로 작성.
                    상단에 정확한 정식 행정 구역명(시, 군, 구)과 반려동물 정보를 작성.
                    정확한 정식 행정 구역명(시, 군, 구)을 기반으로 작성(예: 인천광역시 부평구).
                    평문으로 작성 no markdown.
                    """, extractedPetInfoJson, location);

            // 3. Gemini API로 추천 요청
            String recommendation = geminiService.forRAG(prompt);

            // 4. View로 결과 전달
            model.addAttribute("extractedPetInfo", extractedPetInfoJson);
            model.addAttribute("recommendation", recommendation);
            return "pipeline";

        } catch (Exception e) {
            log.error("추천 처리 중 오류 발생", e);
            model.addAttribute("error", "추천 결과를 받아오지 못했습니다.");
            return "pipeline";
        }
    }
}