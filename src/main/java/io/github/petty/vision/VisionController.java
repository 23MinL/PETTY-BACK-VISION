//package io.github.petty.vision;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//@Controller
//@RequestMapping("/api/vision") // API 경로 명확화
//public class VisionController {
//
//    @Autowired
//    private VisionService visionService;
//
//    /**
//     * 이미지와 반려동물 이름을 받아 분석 요청을 처리합니다.
//     * 결과를 모델에 담아 뷰로 전달합니다. (뷰 이름은 프로젝트에 맞게 조정 필요)
//     */
//    @PostMapping("/analyze")
//    public String analyzeImage(@RequestParam("file") MultipartFile file,
//                               @RequestParam("petName") String petName, // 이름 파라미터 추가
//                               Model model) {
//        // 입력 값 검증 (선택적)
//        if (petName == null || petName.trim().isEmpty()) {
//            model.addAttribute("message", "오류: 반려동물 이름을 입력해주세요.");
//            return "visionUpload"; // 업로드 페이지로 다시 이동
//        }
//        if (file.isEmpty()) {
//            model.addAttribute("message", "오류: 이미지 파일을 선택해주세요.");
//            model.addAttribute("petName", petName); // 이름 유지
//            return "visionUpload";
//        }
//
//
//        try {
//            // 서비스 호출 시 petName 전달
//            String analysisResultText = visionService.analyzeImage(file, petName);
//
//            model.addAttribute("analysisResult", analysisResultText); // 분석 결과 텍스트
//            model.addAttribute("message", petName + " 분석 완료!");
//            model.addAttribute("petName", petName); // 결과 페이지에서도 이름 사용 가능하도록 전달
//
//        } catch (IllegalArgumentException e) { // 유효성 검사 오류 등
//            model.addAttribute("message", "오류: " + e.getMessage());
//            model.addAttribute("petName", petName);
//        } catch (Exception e) { // 서비스 레이어에서 발생한 일반 오류
//            // 실제 운영 환경에서는 사용자에게 친화적인 메시지 + 로깅 강화 필요
//            model.addAttribute("message", "이미지 분석 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
//            model.addAttribute("petName", petName);
//            // 로깅은 서비스 레이어에서 이미 했을 것이므로 여기서는 생략 가능
//        }
//        // 결과를 보여줄 뷰 이름 반환 (예: "visionResult" 또는 "visionUpload"에 결과 섹션 표시)
//        return "visionUpload"; // 또는 "visionResult" 등 실제 사용하는 뷰 이름
//    }
//
//    // 업로드 페이지를 보여주는 GET 핸들러 (IndexController와 중복될 수 있으니 확인)
//    @GetMapping("/upload")
//    public String showUploadForm() {
//        return "visionUpload"; // 업로드 폼 HTML 파일 이름
//    }
//}