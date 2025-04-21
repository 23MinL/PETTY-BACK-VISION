//package io.github.petty.config;
//
//import com.google.api.gax.core.FixedCredentialsProvider;
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.cloud.vision.v1.ImageAnnotatorClient;
//import com.google.cloud.vision.v1.ImageAnnotatorSettings;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.rekognition.RekognitionClient;
//
//import java.io.IOException;
//import java.io.InputStream;
//
//@Configuration
//public class AwsGoogleCloudConfig {
//
//    private static final Logger log = LoggerFactory.getLogger(AwsGoogleCloudConfig.class);
//
//    @Value("${aws.region}")
//    private String awsRegion;
//
//    @Value("${google.credentials.path}")
//    private String googleCredentialsPath;
//
//    // AWS Rekognition 설정
//    @Bean
//    public RekognitionClient rekognitionClient() {
//        DefaultCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();
//
//        log.info("AWS Rekognition 클라이언트 생성 중... 리전: {}", awsRegion);
//        return RekognitionClient.builder()
//                .region(Region.of(awsRegion))
//                .credentialsProvider(credentialsProvider)
//                .build();
//    }
//
//    // Google Cloud Vision 설정 (✅ 수정된 코드)
//    @Bean
//    public ImageAnnotatorClient imageAnnotatorClient() throws IOException {
//        log.info("Google Cloud Vision 클라이언트 생성 중... 경로: {}", googleCredentialsPath);
//
//        // ✅ classpath 리소스 방식으로 파일을 읽어야 합니다
//        try (InputStream credentialsStream = getClass().getClassLoader().getResourceAsStream("credentials.json")) {
//            if (credentialsStream == null) {
//                throw new IOException("classpath에서 credentials.json을 찾을 수 없습니다.");
//            }
//
//            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
//
//            ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
//                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
//                    .build();
//
//            return ImageAnnotatorClient.create(settings);
//        } catch (IOException e) {
//            log.error("Google Cloud Vision 클라이언트 생성 실패. 경로를 확인해주세요: {}", googleCredentialsPath, e);
//            throw e;
//        }
//    }
//}
