package io.github.petty.vision.helper;

import io.github.petty.vision.config.VisionProperties;
import io.github.petty.vision.dto.gemini.GeminiRequest;
import io.github.petty.vision.dto.together.TogetherRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class PromptFactory {
    private final VisionProperties prop;

    /* ---------- 공통 ---------- */
    public String interimMsg(String pet, String sp){
        return "알 수 없음".equals(sp)?
                String.format("'%s'에 대해서 알아볼게요! \n잠시만 기다려 주세요. 보고서를 작성 중입니다...", pet):
                String.format("오 '%s'는 '%s'이군요!\n잠시만 기다려 주세요. 보고서를 작성 중입니다...", pet, sp);
    }

    /* ---------- Gemini ---------- */
    public GeminiRequest toGeminiReq(byte[] img, String pet, String sp){
        String base64 = Base64.getEncoder().encodeToString(img);
        String prompt = detailedPrompt(pet, sp);
        Map<String,Object> part1 = Map.of("text", prompt);
        Map<String,Object> part2 = Map.of("inline_data", Map.of("mime_type","image/jpeg","data", base64));
        Map<String,Object> content = Map.of("parts", List.of(part1, part2));
        return new GeminiRequest(List.of(content));
    }

    /* ---------- Together ---------- */
    public TogetherRequest toTogetherReq(byte[] img, String pet){
        String base64 = Base64.getEncoder().encodeToString(img);
        Map<String,Object> imageData = Map.of("format","jpeg","data", base64);
        Map<String,Object> message = Map.of(
                "role","user",
                "content", List.of(
                        Map.of("type","text","text", detailedPrompt(pet, "")),
                        Map.of("type","image_data","image_data", imageData))
        );
        return new TogetherRequest(prop.getLlamaModel(), List.of(message));
    }

    /* ---------- 내부 ---------- */
    private String detailedPrompt(String pet, String species){
        return String.format("""
반려동물 '%s'(종류: %s)에 대한 분석 보고서를 작성해줘.\n\n- 종류\n- 품종(믹스견은 추정 근거)\n- 외형(크기·털 색·특징)\n- 무게(1~40kg)\n- 맹수 여부\n- 감정·행동\n- 기타 특이사항(목줄·배경 등)\n\n보호자가 이해하기 쉬운 문장으로 요약해줘.""", pet, species);
    }
}