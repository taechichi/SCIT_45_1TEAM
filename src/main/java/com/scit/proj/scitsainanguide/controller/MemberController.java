package com.scit.proj.scitsainanguide.controller;

import com.scit.proj.scitsainanguide.domain.dto.MemberDTO;
import com.scit.proj.scitsainanguide.security.AuthenticatedUser;
import com.scit.proj.scitsainanguide.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("member")
public class MemberController {

    private final MemberService memberService;

    @Value("${board.uploadPath}")
    String uploadPath;

    /**
     * 로그인폼으로 이동
     * @return 로그인 HTML
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("memberDTO", new MemberDTO());
        return "/member/register";
    }

    /**
     * 
     * @param memberDTO 회원정보 dto
     * @param file  프로필 사진 저장
     * @param model 모댈 객체
     * @return 성공하면 매인으로 실패하면 다시 로그인 페이지로
     */
    @PostMapping("/register")
    public String registerMember(MemberDTO memberDTO,
                                 @RequestParam("file") MultipartFile file, // 파일 업로드를 위한 MultipartFile 사용
                                 Model model) {
        try {
            // 파일 저장 및 경로 설정
            memberService.registerMember(memberDTO, file);

            // 파일명을 model에 추가하여 뷰에서 사용할 수 있도록 전달
            model.addAttribute("member", memberDTO);

            return "redirect:/";
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "파일 저장 중 오류가 발생했습니다.");
            return "/member/register";
        }
    }

    /**
     * 프로필 사진 다운로드 (로그인한 본인)
     * @param user 로그인 한 사용자 아이디
     * @param response  응답객체
     * @throws IOException
     */
    @GetMapping("download")
    public void download(@AuthenticationPrincipal AuthenticatedUser user
            , HttpServletResponse response) throws IOException {
        memberService.download(user.getId(), uploadPath, response);
    }

    /**
     * 프로필 사진 다운로드 (다른 회원)
     * @param memberId 회원 아이디
     * @param response 응답 객체
     * @throws IOException
     */
    @GetMapping("download/{memberId}")
    public void download(@PathVariable String memberId, HttpServletResponse response) throws IOException {
        memberService.download(memberId, uploadPath, response);
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
     *
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

    /**
     * 가입폼으로 이동
     * @return 가입폼 HTML
     */
    @GetMapping("login")
    public String showLoginForm(Model model) {
        model.addAttribute("memberDTO", new MemberDTO());
        return "member/login";  // login.html로 이동
    }

    /**
     * 현재 로그인 중인 사용자가 아이콘 버튼을 클릭하면 상태 변경
     * @param user
     * @param statusId
     * @return 변경 후 사용자의 상태값 반환
     * @throws IOException
     */
    @PostMapping("/changeMyStatus")
    public ResponseEntity<Integer> changeMyStatus(@AuthenticationPrincipal AuthenticatedUser user
                                                , @RequestParam("statusId") Integer statusId) throws IOException{
        String memberId = user.getUsername();
        Integer statusNum = memberService.changeMyStatus(memberId, statusId);
        // 상태 변경 후의 상태 ID를 반환합니다.
        return new ResponseEntity<>(statusNum, HttpStatus.OK);
    }
}

