package com.scit.proj.scitsainanguide.scheduler;

import com.scit.proj.scitsainanguide.domain.entity.MemberEntity;
import com.scit.proj.scitsainanguide.domain.entity.StatusEntity;
import com.scit.proj.scitsainanguide.repository.MemberJpaRepository;
import com.scit.proj.scitsainanguide.repository.StatusJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component // bean 객체
public class StatusScheduler {

    @Autowired
    private MemberJpaRepository memberJpaRepository;
    @Autowired
    private StatusJpaRepository statusJpaRepository;
    @Qualifier("messageSource")
    @Autowired
    private MessageSource messageSource;

    // 1분마다 실행 -> DB 반영까지 몇초~몇십초 지연있으나 초마다 실행보다 부하 적고 웬만한 어플은 기본 1분 단위
    @Scheduled(fixedRate = 60000)
    // 스케줄러에서 사용하는 엔티티는 세션이 열린 상태에서 접근해야 합
    // 그러나, MemberEntity의 status 객체가 지연로딩(LAZY)로 설정되어 있어
    // 객체의 속성에 접근할 때 세션이 없어서 오류가 발생함
    // 따라서, 스프링의 트랜잭션 관리 기능 사용해 세션을 열고 닫는 것이 필요
    @Transactional
    public void checkStatusExpiry() {

        LocalDateTime now = LocalDateTime.now(); // 현재 시간 가져오기

        // 현재 시간보다 'endTime'이 이전인 모든 회원 조회
        List<MemberEntity> mentities = memberJpaRepository.findByEndTimeBefore(now);

        // 조회된 각 회원에 대해 '평상시'로 상태 변경
        for (MemberEntity mentity : mentities) {
            if (mentity.getEndTime() != null) { // 'endTime'이 null이 아닌지 확인
                StatusEntity defaultStatus = statusJpaRepository.findById(1)
                        .orElseThrow(() -> new RuntimeException("Status not found"));

                mentity.setStatus(defaultStatus); // 상태 엔티티를 변경
                // 변경된 회원 정보를 데이터베이스에 저장
                memberJpaRepository.save(mentity);
            }
        }
    }

    // 상대적인 시간을 계산해, 최신 상태 변경 시간을 반환
    public String getRelativeTime(LocalDateTime dateTime) {
        Duration duration = Duration.between(dateTime, LocalDateTime.now());
        long seconds = duration.getSeconds();
        long days = duration.toDays();
        // Locale 클래스를 사용, 사용자의 언어 환경에 따라 현재 사용자 언어 확인해 적절한 메시지를 가져오도록
        Locale locale = LocaleContextHolder.getLocale();

        if (seconds < 60) {
            return seconds + messageSource.getMessage("main.myStatus.seconds", null, locale);
        } else if (seconds < 3600) {
            return (seconds / 60) + messageSource.getMessage("main.myStatus.minutes", null, locale);
        } else if (seconds < 86400) {
            return (seconds / 3600) + messageSource.getMessage("main.myStatus.hours", null, locale);
        } else if (days < 3) {
            return days + messageSource.getMessage("main.myStatus.days", null, locale);
        } else {
            // 3일 이상인 경우, formatTime 메서드 사용
            return formatTime(dateTime);
        }
    }

    // 날짜 정보를 포맷해서 반환하는 메서드
    public String formatTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd / HH:mm:ss");
        return dateTime.format(formatter);
    }

}
