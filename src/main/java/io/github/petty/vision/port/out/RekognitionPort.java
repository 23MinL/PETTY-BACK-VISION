package io.github.petty.vision.port.out;

import software.amazon.awssdk.services.rekognition.model.Label;
import java.util.List;

public interface RekognitionPort {
    List<Label> detectLabels(byte[] image);
}
