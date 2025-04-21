package io.github.petty.users.controller;

import io.github.petty.users.dto.JoinDTO;
import io.github.petty.users.service.JoinService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;

@Controller
public class UsersController {

    private final JoinService joinService;

    public UsersController(JoinService joinService) {
        this.joinService = joinService;
    }

    @GetMapping("/join")
    public String joinForm(Model model) {
        model.addAttribute("JoinDTO", new JoinDTO());
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        return "login";
    }
}


