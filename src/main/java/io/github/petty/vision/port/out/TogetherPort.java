package io.github.petty.vision.port.out;

import io.github.petty.vision.dto.together.TogetherRequest;
import io.github.petty.vision.dto.together.TogetherResponse;

public interface TogetherPort {
    TogetherResponse generate(TogetherRequest req);
}

