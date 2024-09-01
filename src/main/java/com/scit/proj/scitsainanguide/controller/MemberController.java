package com.scit.proj.scitsainanguide.controller;

import com.scit.proj.scitsainanguide.domain.entity.MemberEntity;
import com.scit.proj.scitsainanguide.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("member", new MemberEntity());
        return "/member/register";
    }

    @PostMapping("/member/register")
    public String registerMember(
            @RequestParam("memberId") String memberId,
            @RequestParam("password") String password,
            @RequestParam("nickname") String nickname,
            @RequestParam("email") String email,
            @RequestParam("gender") String gender,
            @RequestParam("phone") String phone,
            @RequestParam("nationality") String nationality,
            @RequestParam("fileName") MultipartFile file,
            @RequestParam("statusId") Integer statusId
    ) {

        // 파일 처리 로직을 여기에 추가할 수 있습니다.
        String fileName = file.getOriginalFilename(); // 예를 들어 파일 이름만 가져온다.

        // MemberService를 사용하여 회원 가입 처리
        memberService.registerMember(memberId, password, nickname, email, gender, phone, nationality, fileName, statusId);

        // 회원가입 후 리디렉션 또는 다른 페이지로 이동
        return "redirect:/login";
    }
    /**
     * 회원가입 페이지에서 "ID 중복확인" 버튼을 클릭하면 새 창으로 보여줄 검색 페이지로 이동
     * @return ID검색 HTML파일 경로
     */
    @GetMapping("idCheck")
    public String idCheck() {
        return "member/idCheck";
    }

    /**
     * ID중복확인 페이지에서 검색 요청했을 때 처리
     * @param searchId 아이디 중복 체크를 위해 새 창에서 입력 받은 아이디를 받는 변수
     * @return ID검색 HTML파일 경로
     */
    @PostMapping("idCheck")
    public String idCheckResult(
            @RequestParam("searchId") String searchId,
            Model model) {
        boolean result = memberService.findId(searchId);
        model.addAttribute("searchId", searchId);
        model.addAttribute("result", result);

        return "member/idCheck";
    }
}
