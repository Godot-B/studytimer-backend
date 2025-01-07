package umc.hackathon.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;


public class SubjectRequestDTO {

    @Getter
    public static class AddSubjectDTO {

        @Size(min = 1, max = 20)
        String subjectName; // 과목 이름

        @NotNull
        Integer goalHour;       // 목표 공부 시간 (시간 단위)

        @NotNull
        Integer goalMinute;       // 목표 공부 시간 (분 단위)
    }
}
