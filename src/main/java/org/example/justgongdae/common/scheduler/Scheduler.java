package org.example.justgongdae.common.scheduler;

import org.example.justgongdae.data.crossref.CrossrefRepository;
import org.example.justgongdae.service.ContentService;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {
    private final CrossrefRepository crossrefRepository;
    private final ContentService contentService;

    public Scheduler(CrossrefRepository crossrefRepository, ContentService contentService) {
        this.crossrefRepository = crossrefRepository;
        this.contentService = contentService;
    }

//    @Scheduled(cron = "0 0 9 * * ?") // 매일 오전 9시에 실행
//    public void scheduleDailyPost() {
//        Paper paper = crossrefRepository.fetchRandomPaper();
//        if (paper != null) {
//            String content = contentService.generatePostContent(paper);
//            if (content != null) {
//                // 포스팅 로직
//                System.out.println("생성된 포스팅 내용:");
//                System.out.println(content);
//                // 여기서 Instagram Threads API를 사용하여 포스팅을 시도할 수 있습니다.
//                // 하지만 공식 API가 없거나 이용 약관에 위배될 수 있으므로 실제 구현 시 주의하세요.
//            } else {
//                System.out.println("포스팅 내용을 생성하지 못했습니다.");
//            }
//        } else {
//            System.out.println("논문 데이터를 가져오지 못했습니다.");
//        }
//    }
}
