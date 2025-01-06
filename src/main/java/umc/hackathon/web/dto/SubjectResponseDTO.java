package umc.hackathon.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class SubjectResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectPreviewDTO {

        Long id; // 과목 ID
        String subjectName; // 과목 이름
        Integer goalTime; // 목표 공부 시간 (분 단위)
        Integer breakTime; // 휴식 시간 (분 단위)
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudyTimeDTO {
        Float subjectStudyTime;
    }
}