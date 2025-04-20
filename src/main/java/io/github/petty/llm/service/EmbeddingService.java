package io.github.petty.llm.service;

import io.github.petty.llm.dto.EmbeddingResult;
import io.github.petty.tour.entity.Content;
import io.github.petty.tour.entity.PetTourInfo;
import io.github.petty.tour.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmbeddingService {
    private final ContentRepository contentRepository;
    // OpenAI 임베딩 모델
    private final EmbeddingModel embeddingModel;


    // 텍스트 전처리 : 장소 설명들 다 모아서 문장으로 만들기
    public String prepareContentText(Content content) {
        StringBuilder sb = new StringBuilder();

        //[관광지 이름]-[주소]-[설명]-[반려동물 동반 정보 | 동반 유형, 추가 정보] 등
        sb.append("[관광지 이름] ").append(content.getTitle()).append("\n");
        sb.append("[주소] ").append(content.getAddr1());
        if(content.getAddr2() != null){
            sb.append(" ").append(content.getAddr2());
        }
        sb.append("\n");

        if(content.getAddr1() != null){
            sb.append("[설명] ").append(content.getOverview()).append("\n");
        }

        PetTourInfo pet = content.getPetTourInfo();
        if (pet != null) {
            sb.append("[반려동물 동반 정보]").append("\n");
            if (pet.getAcmpyTypeCd() != null)
                sb.append("- 동반 유형: ").append(pet.getAcmpyTypeCd()).append("\n");
            if (pet.getEtcAcmpyInfo() != null)
                sb.append("- 동반 가능 반려 동물 정보 : ").append(pet.getEtcAcmpyInfo()).append("\n");
            if (pet.getAcmpyPsblCpam() != null)
                sb.append("- 추가 정보 : ").append(pet.getAcmpyPsblCpam()).append("\n");
            if (pet.getAcmpyNeedMtr() != null)
                sb.append("- 준비물(목줄, 기타) : ").append(pet.getAcmpyNeedMtr()).append("\n");
        }

        return sb.toString();
    }

    // 텍스트 정제 -> 임베딩
    public EmbeddingResult embedContent(Content content) {
        String text = prepareContentText(content);
        // 임베딩 벡터 생성
        EmbeddingResponse response = embeddingModel.embedForResponse(List.of(text));
        float[] output = response.getResults().get(0).getOutput();
         // 수동 변환
        List<Float> vector = new ArrayList<>();
        for (float f : output) {
            vector.add(f);
        }
        // UUID 생성
        String uuid = UUID.randomUUID().toString();

        // 원본 contentId 보존
        String contentId = content.getContentId().toString();

        // content id + 원문 텍스트 + 임베딩 벡터 반환
        return new EmbeddingResult(uuid, contentId, text, vector);
    }

    // 임베딩 생성 - UUID와 원본 ID 모두 포함해야 함
    // dto 사용
    // VectorService에서 문서 생성만 하도록 위임
    public Document toDocument(EmbeddingResult result, Content content){

        // 메타데이터에 원본 ID, 관련 데이터 저장
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("contentId", result.contentId());
        metadata.put("title", content.getTitle());
        metadata.put("source", "tourContent");

        // 지역 필터 추후 구현

        // 문서 생성 - UUID -> 문서 ID
        return Document.builder()
                .id(result.id())
                .text(result.text())
                .metadata(metadata)
                .build();

    }
}
