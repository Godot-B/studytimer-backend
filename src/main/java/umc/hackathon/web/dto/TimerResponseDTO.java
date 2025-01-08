package umc.hackathon.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TimerResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimerViewDTO {

        String subjectName;

        Float remainTime; // 과목 별 남은 시간

        Integer goalTime;

        Float totalStudyTime; // 분단위(소수점)

        Integer subjectGoalTime; // 분단위
    }
}
