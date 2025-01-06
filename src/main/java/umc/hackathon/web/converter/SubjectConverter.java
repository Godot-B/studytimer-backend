package umc.hackathon.web.converter;

import umc.hackathon.domain.Subject;
import umc.hackathon.web.dto.SubjectResponseDTO;

import java.util.ArrayList;
import java.util.List;

public class SubjectConverter {

    public static SubjectResponseDTO.SubjectPreviewDTO toSubjectPreviewDTO(Subject subject) {
        return SubjectResponseDTO.SubjectPreviewDTO.builder()
                .id(subject.getId())
                .subjectName(subject.getSubjectName())
                .goalTime(subject.getSubjectGoalTime())
                .breakTime(subject.getBreakTime())
                .build();
    }

    public static List<SubjectResponseDTO.SubjectPreviewDTO> toSubjectPreviewListDTO(List<Subject> subjects) {
        List<SubjectResponseDTO.SubjectPreviewDTO> subjectPreviewList = new ArrayList<>();

        for (Subject subject : subjects) {
            SubjectResponseDTO.SubjectPreviewDTO subjectDTO = SubjectResponseDTO.SubjectPreviewDTO.builder()
                    .id(subject.getId())
                    .subjectName(subject.getSubjectName())
                    .goalTime(subject.getSubjectGoalTime())
                    .breakTime(subject.getBreakTime())
                    .build();
            subjectPreviewList.add(subjectDTO);
        }
        return subjectPreviewList;
    }
}
