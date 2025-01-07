package umc.hackathon.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.hackathon.domain.Keyword;
import umc.hackathon.domain.Subject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class DatePlanResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteResultDTO {

        Long deleteSubjectId;
        String deleteSubjectName;
        String message;
    }
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SetResultDTO {

        Long datePlanId;
        LocalDate date;
        LocalDateTime createdAt;
    }


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomeViewDTO {

        Float totalRemainTime;

        List<SubjectResponseDTO.SubjectPreviewDTO> subjectPreviewDTOList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimerViewDTO {

        Float remainTime; // 과목 별 남은 시간

        Integer goalTime;

        Float totalStudyTime; // 분단위(소수점)

        Integer subjectGoalTime; // 분단위
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HourlyStudyTimesDTO {

        Integer hour;

        Integer studyTime;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatDTO {

        // 키워드 전체
        List<String> allKeywords;

        // 날짜별 통계
        LocalDate date;

        Integer goalTime;

        Float totalStudyTime;

        List<HourlyStudyTimesDTO> hourlyStudyTimesByDate;

        // 모든 날짜 총 공부시간 JSON (날짜 : 총 공부 시간)
        List<DateInfoDTO> allDateInfoList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DateInfoDTO {

        LocalDate date;

        Float totalStudyTime; // 모든 과목의, 또는 과목 별
    }
}
