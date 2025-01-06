package umc.hackathon.web.converter;

import umc.hackathon.domain.DatePlan;
import umc.hackathon.web.dto.DatePlanRequestDTO;
import umc.hackathon.web.dto.DatePlanResponseDTO;

import java.time.LocalDate;

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
}
