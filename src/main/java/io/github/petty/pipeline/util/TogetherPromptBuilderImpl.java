package io.github.petty.pipeline.util;

import io.github.petty.pipeline.service.PromptGeneratorService;
import io.github.petty.pipeline.service.TogetherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TogetherPromptBuilderImpl implements TogetherPromptBuilder {

    private final TogetherService togetherService;
    private final PromptGeneratorService promptGeneratorService;

    public String buildPrompt(String visionReport, String location) throws Exception {
        String extractedPetInfoJson = togetherService.answer(
                visionReport + " -> 이 문장에서 반려동물의 이름(name), 종(species), 무게(weight), 맹수 여부(is_danger(only true or false))를 JSON 형식으로 작성 + " +
                        "만약 반려동물의 종과 무게를 보았을 때, 입마개가 필요할 것 같다면 맹수 여부를 'true'로 작성 + " +
                        "무게는 kg 단위를 반드시 포함" + "no markdown, only JSON" + "최종 결과에 {}는 제거"
        );

        return promptGeneratorService.generatePrompt(extractedPetInfoJson, location);
    }
}
