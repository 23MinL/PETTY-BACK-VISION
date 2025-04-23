package io.github.petty.vision.port.in;

import org.springframework.web.multipart.MultipartFile;

public interface VisionUseCase {
    String analyze(MultipartFile file, String petName);
    String interim(byte[] image, String petName);

}