package umc.hackathon.web.converter;

import umc.hackathon.domain.DatePlan;
import umc.hackathon.domain.Subject;
import umc.hackathon.web.dto.DatePlanRequestDTO;
import umc.hackathon.web.dto.DatePlanResponseDTO;
import umc.hackathon.web.dto.SubjectResponseDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatePlanConverter {

    public static DatePlan toDatePlan(DatePlanRequestDTO.SetGoalDTO request) {

        int goalTime = request.getGoalHour() * 60 + request.getGoalMinute();

        return DatePlan.builder()
                .date(LocalDate.now())
                .goalTime(goalTime)
                .totalStudyTime(0.0f)
                .build();
    }

    public static DatePlanResponseDTO.SetResultDTO toSetResultDTO(DatePlan plan) {

        return DatePlanResponseDTO.SetResultDTO.builder()
                .datePlanId(plan.getId())
                .date(plan.getDate())
                .createdAt(plan.getCreatedAt())
                .build();
    }


    public static SubjectResponseDTO.HomeViewDTO toHomeViewDTO(List<Subject> subjects) {

        float totalRemainTime = 0.0f;

        List<SubjectResponseDTO.SubjectPreviewDTO> subjectPreviewList = new ArrayList<>();
        for (Subject subject : subjects) {
            float remainTime = subject.getSubjectGoalTime() - subject.getSubjectStudyTime();
            totalRemainTime += remainTime;

            SubjectResponseDTO.SubjectPreviewDTO subjectDTO = SubjectResponseDTO.SubjectPreviewDTO.builder()
                    .id(subject.getId())
                    .subjectName(subject.getSubjectName())
                    .goalTime(subject.getSubjectGoalTime())
                    .breakTime(subject.getBreakTime())
                    .remainTime(remainTime)
                    .build();
            subjectPreviewList.add(subjectDTO);
        }

        return SubjectResponseDTO.HomeViewDTO.builder()
                .totalRemainTime(totalRemainTime)
                .subjectPreviewDTOList(subjectPreviewList)
                .build();
    }
}
