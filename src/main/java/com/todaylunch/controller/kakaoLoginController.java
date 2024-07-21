package com.todaylunch.controller;

import com.todaylunch.dto.KakaoUserInfoResponseDto;
import com.todaylunch.service.KakaoLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
//@RequestMapping("/login/oauth2/code/kakao") // 이거 어떠세요?
public class kakaoLoginController {

    private final KakaoLoginService kakaoLoginService;

    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        String accessToken = kakaoLoginService.getAccessTokenFromKakao(code);
        KakaoUserInfoResponseDto userInfo = kakaoLoginService.getUserInfo(accessToken);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
