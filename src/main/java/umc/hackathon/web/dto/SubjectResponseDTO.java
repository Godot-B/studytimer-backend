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

        Long id;

        String subjectName;

        Integer goalTime;

        Float remainTime;

        Integer breakTime;
    }
}