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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("member")
public class MemberController {

    private final MemberService memberService;

    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${board.uploadPath}")
    String uploadPath;

    /**
     * 로그인폼으로 이동
     *
     * @return 로그인 HTML
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("memberDTO", new MemberDTO());
        return "/member/register";
    }

    /**
     * @param memberDTO 회원정보 dto
     * @param file      프로필 사진 저장
     * @param model     모댈 객체
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
     *
     * @param user     로그인 한 사용자 아이디
     * @param response 응답객체
     * @throws IOException
     */
    @GetMapping("download")
    public void download(@AuthenticationPrincipal AuthenticatedUser user
            , HttpServletResponse response) throws IOException {
        memberService.download(user.getId(), uploadPath, response);
    }

    /**
     * 프로필 사진 다운로드 (다른 회원)
     *
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
     *
     * @return ID검색 HTML파일 경로
     */
    @GetMapping("idCheck")
    public String idCheck() {
        return "member/idCheck";
    }

    @PostMapping("idCheck")
    @ResponseBody
    public Map<String, Object> idCheckResult(@RequestParam("searchId") String searchId) {
        log.debug("전달된 객체 : {}", searchId);
        boolean result = memberService.findId(searchId);

        // JSON 형태로 반환
        Map<String, Object> response = new HashMap<>();
        response.put("searchId", searchId);
        response.put("result", result);

        return response;
    }

    @PostMapping("passwordCheck")
    @ResponseBody
    public Map<String, Object> passwordCheckResult(@RequestParam("searchPw") String searchPw, @AuthenticationPrincipal AuthenticatedUser user) {
        log.debug("전달된 비밀번호 : {}", searchPw);

        // 사용자 정보를 DB에서 가져옴 (로그인한 사용자의 ID를 사용)
        MemberDTO memberDTO = memberService.findByMemberId(user.getUsername());

        // 암호화된 비밀번호와 입력된 비밀번호 비교
        boolean result = passwordEncoder.matches(searchPw, memberDTO.getPassword());

        // JSON 형태로 반환
        Map<String, Object> response = new HashMap<>();
        response.put("searchPw", searchPw);
        response.put("result", result); // 비밀번호 일치 여부 반환

        return response;
    }


    /**
     * 가입폼으로 이동
     *
     * @return 가입폼 HTML
     */
    @GetMapping("login")
    public String showLoginForm(Model model) {
        model.addAttribute("memberDTO", new MemberDTO());
        return "member/login";  // login.html로 이동
    }

    /**
     * 현재 로그인 중인 사용자가 아이콘을 클릭해 상태 변경
     * @param user 현재 로그인 중인 유저
     * @param statusId 바꾸고자 하는 상태
     * @param hours 상태 유지 시간
     */
    @ResponseBody
    @PatchMapping("/change/status")
    public ResponseEntity<Void> changeMyStatus(@AuthenticationPrincipal AuthenticatedUser user
            , @RequestParam("statusId") Integer statusId
            , @RequestParam("duration") Integer hours) {
        String memberId = user.getUsername();
        memberService.changeMyStatus(memberId, statusId, hours);
        return ResponseEntity.ok().build();
    }

    /**
     * 상태 메시지 변경하는 메서드
     * @param user
     * @param newStatusMessage
     */
    @ResponseBody
    @PatchMapping("/change/smessage")
    public ResponseEntity<Void> changeMyStatusMessage(@AuthenticationPrincipal AuthenticatedUser user,
                                                        @RequestParam("newStatusMessage") String newStatusMessage) {
        String memberId = user.getUsername();
        memberService.changeMyStatusMessage(memberId, newStatusMessage);
        return ResponseEntity.ok().build();
    }

    /**
     * 최신 상태를 불러와 반환하는 메서드
     * @param user
     * @return
     */
    @ResponseBody
    @GetMapping("/view/statuses")
    public ResponseEntity<Map<String, Object>> viewStatuses(@AuthenticationPrincipal AuthenticatedUser user) {
        Map<String, Object> response = memberService.viewStatuses(user.getUsername());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 개인정보 수정폼으로 이동
     *
     * @return info HTML
     */
    @GetMapping("/info")
    public String showInfoForm(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
        // 로그인된 사용자의 정보를 가져와서 MemberDTO로 변환
        MemberDTO memberDTO = memberService.findByMemberId(user.getUsername());
        model.addAttribute("memberDTO", memberDTO);
        return "/member/info";
    }

    @PostMapping("/info")
    public String infoForm(
            @AuthenticationPrincipal AuthenticatedUser user,
            @ModelAttribute MemberDTO memberDTO,
            @RequestParam("file") MultipartFile file // 프로필 사진 파일
    ) {
        try {
            // 사용자 정보 업데이트 처리
            memberService.updateMember(memberDTO, file); // MultipartFile도 함께 전달
        } catch (IOException e) {
            log.error("회원 정보 업데이트 중 오류 발생: {}", e.getMessage());
            // 오류 처리 (예: 에러 메시지 설정, 에러 페이지 리다이렉트 등)
            return "redirect:/error"; // 오류 페이지로 리다이렉트
        }

        return "redirect:/";  // 수정 후 메인 페이지로 리다이렉트
    }





}

