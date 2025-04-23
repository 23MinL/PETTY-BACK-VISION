package io.github.petty.vision.service;

import io.github.petty.vision.config.VisionProperties;
import io.github.petty.vision.helper.*;
import io.github.petty.vision.port.in.VisionUseCase;
import io.github.petty.vision.port.out.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class VisionServiceImpl implements VisionUseCase {
    private final VisionProperties prop;
    private final SpeciesDetector detector;
    private final PromptFactory prompt;
    private final GeminiPort gemini;
    private final TogetherPort together;

    // private final RestTemplate rest;
    public String interim(byte[] image, String petName) {
        String species = detector.detect(image);
        return prompt.interimMsg(petName, species);
    }

    @Override
    public String analyze(MultipartFile file, String pet) {
        byte[] img;
        try {
            img = file.getBytes();
        } catch (Exception e) {
            throw new IllegalStateException("이미지를 읽을 수 없습니다", e);
        }

        String species = detector.detect(img);
        String interim  = prompt.interimMsg(pet, species);   // (원하면 프론트에 먼저 보내기)

        /* ---------------- Gemini ---------------- */
        try {
            return gemini.generate(
                    prompt.toGeminiReq(img, pet, species)
            ).plainText();
        } catch (Exception gex) {
            log.warn("Gemini 실패 → Together fallback", gex);
        }

        /* ---------------- Together -------------- */
        try {
            return together.generate(
                    prompt.toTogetherReq(img, pet)
            ).plainText();
        } catch (Exception tex) {
            log.error("Together 실패", tex);
            return interim + "\n\n최종 분석 보고서 생성에 실패했습니다.";
        }
    }
}