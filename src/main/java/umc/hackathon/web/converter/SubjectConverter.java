package umc.hackathon.web.converter;

import umc.hackathon.domain.Subject;
import umc.hackathon.web.dto.DatePlanResponseDTO;
import umc.hackathon.web.dto.SubjectResponseDTO;

import java.util.ArrayList;
import java.util.List;

public class SubjectConverter {

    public static DatePlanResponseDTO.DeleteResultDTO toDeleteResultDTO(Long deleteSubjectId, String deleteSubjectName) {
        return DatePlanResponseDTO.DeleteResultDTO.builder()
                .deleteSubjectId(deleteSubjectId)
                .message(deleteSubjectName + " 이(가) 삭제되었습니다.")
                .build();
    }

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
            float remainTime = subject.getSubjectGoalTime() - subject.getSubjectStudyTime();
            SubjectResponseDTO.SubjectPreviewDTO subjectDTO = SubjectResponseDTO.SubjectPreviewDTO.builder()
                    .id(subject.getId())
                    .subjectName(subject.getSubjectName())
                    .goalTime(subject.getSubjectGoalTime())
                    .breakTime(subject.getBreakTime())
                    .remainTime(remainTime)
                    .build();
            subjectPreviewList.add(subjectDTO);
        }
        return subjectPreviewList;
    }
}
