package com.todaylunch.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaylunch.domain.Category;
import com.todaylunch.dto.MenuDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Service
@Slf4j
public class KakaoMapsService {

    private final String apiKey;
    private final String apiUrl;
    private final String xAddress;
    private final String yAddress;

    public KakaoMapsService(@Value("${kakao.key}") String apiKey, @Value("${kakao.maps.url}") String apiUrl) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.xAddress = "127.1066904"; // 교육장 주소?
        this.yAddress = "37.4003183"; // 교육장 주소?
    }

    public MenuDTO getRandomFromApi(String category, int size) throws Exception {
        Random ran = new Random();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "KakaoAK " + apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, String> param = new HashMap<>();

        // CategoryDAO.all==all, category.label()
        if (category.equals("전체")) {
            param.put("query", "맛집");
        } else {
            param.put("query", category);
        }
        param.put("x", xAddress);
        param.put("y", yAddress);
        param.put("radius", "5000"); // 500m
        param.put("size", Integer.toString(size)); // size 갯수 받기
        param.put("sort", "distance");
        param.put("page", String.valueOf(ran.nextInt(45) + 1));

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(apiUrl);
        for (Map.Entry<String, String> entry : param.entrySet()) {
            uriBuilder.queryParam(entry.getKey(), entry.getValue());
        }
        URI uri = uriBuilder.build().encode().toUri();

        ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                String.class
        );

        String responseBody = response.getBody();

        if (responseBody == null) {
            throw new RuntimeException("API 응답이 비어 있습니다.");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode documents = root.path("documents");

        if (!documents.isArray() || documents.size() == 0) {
            throw new RuntimeException("API 응답에 'documents' 데이터가 없습니다.");
        }

        JsonNode firstDocument = documents.get(0);

        return MenuDTO.builder()
                .id(firstDocument.path("id").asLong())
                .xAddress(firstDocument.path("x").asText())
                .yAddress(firstDocument.path("y").asText())
                .address(firstDocument.path("road_address_name").asText())
                .name(firstDocument.path("place_name").asText())
                .url(firstDocument.path("place_url").asText())
                .categoryName(firstDocument.path("category_name").asText())
                .distance(firstDocument.path("distance").asInt())
                .build();
    }

    public List<MenuDTO> getListFromApi(Category category) throws Exception {
        List<MenuDTO> responseList = new ArrayList<>();
        for(int i=0;i<2;i++) {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "KakaoAK " + apiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            Map<String, String> param = setParam(category, 15, i+1);

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(apiUrl);

            for (Map.Entry<String, String> entry : param.entrySet()) {
                uriBuilder.queryParam(entry.getKey(), entry.getValue());
            }
            URI uri = uriBuilder.build().encode().toUri();

            ResponseEntity<String> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode documents = root.path("documents");
            Iterator<JsonNode> elements = documents.elements();
            while (elements.hasNext()) {
                JsonNode document = elements.next();
                System.out.println(document);
                MenuDTO menuDTO = MenuDTO.builder()
                        .id(document.path("id").asLong())
                        .xAddress(document.path("x").asText())
                        .yAddress(document.path("y").asText())
                        .address(document.path("road_address_name").asText())
                        .name(document.path("place_name").asText())
                        .url(document.path("place_url").asText())
                        .categoryName(document.path("category_name").asText())
                        .distance(document.path("distance").asInt())
                        .build();

                responseList.add(menuDTO);
            }
        }
        return responseList;
    }

    public String getRandomCategory(List<Category> categories) {
        Random random = new Random();
        int randomIndex = random.nextInt(categories.size());
        Category randomCategory = categories.get(randomIndex);
        return randomCategory.getRandomLabel();
    }

    private Map<String, String> setParam(Category category, int size, int page) {
        Map<String, String> param = new HashMap<>();
        String randomCategory = category.getRandomLabel();
        if (randomCategory.equals("전체")) {
            param.put("query", "맛집");
        } else {
            param.put("query", randomCategory);
        }
        param.put("x", xAddress);
        param.put("y", yAddress);
        param.put("radius", "5000");
        param.put("size", String.valueOf(size));
        param.put("sort", "distance");
        param.put("page", String.valueOf(page));
        System.out.println(param);
        return param;
    }
}

