package io.github.petty.vision.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "vision")
public class VisionProperties {

    private Aws aws = new Aws();
    private Gemini gemini = new Gemini();
    private Together together = new Together();
    private String geminiModel;
    private String togetherModel;
    private String llamaModel;

    @Getter @Setter public static class Aws { private String region; }
    @Getter @Setter public static class Gemini { private String url; private String key; }
    @Getter @Setter public static class Together { private String url; private String key; }
}