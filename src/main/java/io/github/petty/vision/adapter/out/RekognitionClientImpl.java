package io.github.petty.vision.adapter.out;

import io.github.petty.vision.port.out.RekognitionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RekognitionClientImpl implements RekognitionPort {

    private final RekognitionClient client;

    @Override
    public List<Label> detectLabels(byte[] image) {
        DetectLabelsRequest req = DetectLabelsRequest.builder()
                .image(Image.builder().bytes(SdkBytes.fromByteArray(image)).build())
                .maxLabels(20)
                .build();
        DetectLabelsResponse resp = client.detectLabels(req);
        return resp.labels();          // 👈 직렬화 NO, 그대로 반환
    }
}
