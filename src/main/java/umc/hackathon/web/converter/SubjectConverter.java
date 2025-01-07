package umc.hackathon.web.converter;

import umc.hackathon.domain.Subject;
import umc.hackathon.web.dto.DatePlanResponseDTO;
import umc.hackathon.web.dto.SubjectResponseDTO;

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
}
