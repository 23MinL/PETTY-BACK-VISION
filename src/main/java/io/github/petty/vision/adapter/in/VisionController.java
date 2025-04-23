package io.github.petty.vision.adapter.in;

import java.io.IOException;
import io.github.petty.vision.port.in.VisionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/vision")
@RequiredArgsConstructor
public class VisionController {
    private final VisionUseCase vision;
    @GetMapping("/upload")
    public String page(){ return "visionUpload"; }
    @PostMapping("/species")
    @ResponseBody
    public String getSpeciesInterim(@RequestParam("file") MultipartFile file,
                                    @RequestParam("petName") String petName) throws IOException {
        return vision.interim(file.getBytes(), petName);
    }

    @PostMapping("/analyze")
    @ResponseBody
    public String analyze(@RequestParam("file") MultipartFile file,
                          @RequestParam("petName") String petName) {
        return vision.analyze(file, petName);
    }
}