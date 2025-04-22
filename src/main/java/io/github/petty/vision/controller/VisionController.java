package io.github.petty.vision.controller;

import io.github.petty.vision.service.VisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/vision")
public class VisionController {

    @GetMapping("/upload")
    public String uploadPage() {
        return "visionUpload";  // 앞뒤 확장자 없이
    }

    @Autowired
    private VisionService visionService;

    @PostMapping("/species")
    @ResponseBody
    public String getSpeciesInterim(@RequestParam("file") MultipartFile file,
                                    @RequestParam("petName") String petName) {
        return visionService.getInterimMessage(file, petName);
    }

    @PostMapping("/analyze")
    @ResponseBody
    public String analyzeImage(@RequestParam("file") MultipartFile file,
                               @RequestParam("petName") String petName) {
        return visionService.createFinalReport(file, petName);
    }
}