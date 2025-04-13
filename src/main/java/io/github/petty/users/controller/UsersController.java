package io.github.petty.users.controller;

import io.github.petty.users.dto.JoinDTO;
import io.github.petty.users.service.JoinService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsersController {

    private final JoinService joinService;

    public UsersController(JoinService joinService) {
        this.joinService = joinService;
    }

    @GetMapping("/join")
    public String joinForm() {
        return "join";
    }

    @PostMapping("join")
        public String joinProcess(JoinDTO joinDTO, RedirectAttributes redirectAttributes) {

            Boolean joinResult = joinService.joinProcess(joinDTO);
            if (!joinResult) {
                redirectAttributes.addFlashAttribute("error", "가입에 실패했습니다.");
                return "redirect:/join";
            }
            return "redirect:/login";
        }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String loginProcess(String username, String password) {
        // 로그인 로직 추가
        // 예시: 로그인 성공 시 홈 페이지로 리다이렉트
        return "redirect:/";
    }
}


