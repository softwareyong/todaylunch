package com.todaylunch.controller;

import com.todaylunch.domain.Category;
import com.todaylunch.dto.CategoryRequestDTO;
import com.todaylunch.dto.MenuDTO;
import com.todaylunch.service.KakaoMapsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/kakao/v1") // 이거 어떠세요?
public class KakaoApiController {

    private final KakaoMapsService kakaoMapsService;

    @PostMapping("/categorie")
    public ResponseEntity<MenuDTO> getRandomFromApi(@RequestBody CategoryRequestDTO categoryRequest) throws Exception {
        List<Category> categories = categoryRequest.getCategory();
        String randomCategory = kakaoMapsService.getRandomCategory(categories);

        return ResponseEntity.ok()
            .body(kakaoMapsService.getRandomFromApi(randomCategory, 1));
    }

    @GetMapping("categorie/list/{category}")
    public ResponseEntity<List<MenuDTO>> getListFromApi(@PathVariable Category category) throws Exception {

        return ResponseEntity.ok()
                .body(kakaoMapsService.getListFromApi(category));
    }


}
