package org.example.justgongdae.data.crossref.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.justgongdae.data.crossref.CrossrefRepository;
import org.example.justgongdae.data.Paper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class CrossrefRepositoryImpl implements CrossrefRepository {
    private final RestTemplate restTemplate;
    private final String[] crossrefQueries = {
            "https://api.crossref.org/works?query=Computer%20Science&filter=type:journal-article,from-pub-date:2020,has-abstract:true&sample=1",
            "https://api.crossref.org/works?query=Artificial%20Intelligence&filter=type:journal-article,from-pub-date:2020,has-abstract:true&sample=1",
            "https://api.crossref.org/works?query=Computational%20Theory%20and%20Mathematics&filter=type:journal-article,from-pub-date:2020,has-abstract:true&sample=1",
            "https://api.crossref.org/works?query=Computer%20Graphics%20and%20Computer-Aided%20Design&filter=type:journal-article,from-pub-date:2020,has-abstract:true&sample=1",
            "https://api.crossref.org/works?query=Computer%20Networks%20and%20Communications&filter=type:journal-article,from-pub-date:2020,has-abstract:true&sample=1",
            "https://api.crossref.org/works?query=Computer%20Science%20Applications&filter=type:journal-article,from-pub-date:2020,has-abstract:true&sample=1",
            "https://api.crossref.org/works?query=Computer%20Vision%20and%20Pattern%20Recognition&filter=type:journal-article,from-pub-date:2020,has-abstract:true&sample=1",
            "https://api.crossref.org/works?query=Hardware%20and%20Architecture&filter=type:journal-article,from-pub-date:2020,has-abstract:true&sample=1",
            "https://api.crossref.org/works?query=Human-Computer%20Interaction&filter=type:journal-article,from-pub-date:2020,has-abstract:true&sample=1",
            "https://api.crossref.org/works?query=Information%20Systems&filter=type:journal-article,from-pub-date:2020,has-abstract:true&sample=1",
            "https://api.crossref.org/works?query=Signal%20Processing&filter=type:journal-article,from-pub-date:2020,has-abstract:true&sample=1",
            "https://api.crossref.org/works?query=Software&filter=type:journal-article,from-pub-date:2020,has-abstract:true&sample=1",
            "https://api.crossref.org/works?query=Computer%20Science%20miscellaneous&filter=type:journal-article,from-pub-date:2020,has-abstract:true&sample=1"
    };

    public CrossrefRepositoryImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public Paper fetchRandomPaper() {
        // 랜덤하게 쿼리 선택
        String query = crossrefQueries[new Random().nextInt(crossrefQueries.length)];
        log.info("[CrossrefRepositoryImpl]-[fetchRandomPaper] {}", query);

        // API 호출
        ResponseEntity<String> response = restTemplate.getForEntity(query, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            String body = response.getBody();
            log.info("[CrossrefRepositoryImpl]-[fetchRandomPaper] body : {}", body);
            // JSON 파싱
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(body);
                JsonNode itemsNode = rootNode.path("message").path("items");
                if (itemsNode.isArray() && !itemsNode.isEmpty()) {
                    JsonNode paperNode = itemsNode.get(0);
                    Paper paper = new Paper();

                    // DOI 가져오기
                    if (paperNode.has("DOI")) {
                        paper.setDOI(paperNode.get("DOI").asText());
                    }

                    // 제목 가져오기
                    if (paperNode.has("title") && paperNode.path("title").isArray() && paperNode.path("title").size() > 0) {
                        paper.setTitle(paperNode.path("title").get(0).asText());
                    } else {
                        paper.setTitle("제목 없음");
                    }

                    // 초록 가져오기
                    if (paperNode.has("abstract")) {
                        String abstractText = paperNode.path("abstract").asText();

                        // 만약 500자보다 적은 요약본이 있다면
                        // 재귀함수를 호출해 새로운 데이터 fetch
                        if(abstractText.length() < 500) {
                            log.error("[CrossrefRepositoryImpl]-[fetchRandomPaper] abstractText len is under 500!");
                            return fetchRandomPaper();
                        }

                        // HTML 태그 제거
                        abstractText = abstractText.replaceAll("<[^>]*>", "");
                        paper.setAbstractText(abstractText);
                    } else {
                        paper.setAbstractText("초록 없음");
                    }

                    // 저자 가져오기
                    List<String> authors = new ArrayList<>();
                    if (paperNode.has("author")) {
                        for (JsonNode authorNode : paperNode.path("author")) {
                            String given = authorNode.path("given").asText("");
                            String family = authorNode.path("family").asText("");
                            String authorName = (given + " " + family).trim();
                            authors.add(authorName);
                        }
                    } else {
                        authors.add("저자 정보 없음");
                    }
                    paper.setAuthors(authors);

                    return paper;
                }
            } catch (Exception e) {
                log.error("JSON 파싱 중 오류 발생", e);
            }
        } else {
            log.error("API 호출 실패: 상태 코드 {}", response.getStatusCode());
        }
        return null;
    }
}
