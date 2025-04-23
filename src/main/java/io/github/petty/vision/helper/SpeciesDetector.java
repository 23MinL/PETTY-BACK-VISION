package io.github.petty.vision.helper;

import io.github.petty.vision.port.out.RekognitionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.rekognition.model.Label;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SpeciesDetector {

    private final RekognitionPort rekognition;

    /** 영어 → 한글 매핑표 (생략) */
    private static final Map<String,String> MAP = Map.of(
            "cat","고양이","kitten","고양이",
            "dog","개","puppy","개",
            "rabbit","토끼", "hamster","햄스터"
            /* … */
    );

    public String detect(byte[] img){
        List<Label> labels = rekognition.detectLabels(img);

        List<String> matched = new ArrayList<>();
        for (Label l : labels){
            if(l.name()==null) continue;
            String name = l.name().toLowerCase();
            MAP.keySet().stream()
                    .filter(name::contains)
                    .map(MAP::get)
                    .forEach(matched::add);
        }
        if(!matched.isEmpty()) return String.join(" + ", new HashSet<>(matched));
        return labels.isEmpty() ? "알 수 없음" : MAP.getOrDefault(labels.get(0).name().toLowerCase(),"알 수 없음");
    }
}
