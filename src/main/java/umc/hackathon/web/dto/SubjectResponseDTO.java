package umc.hackathon.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


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

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomeViewDTO {

        Float totalRemainTime;

        List<SubjectPreviewDTO> subjectPreviewDTOList;
    }
}