package umc.hackathon.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import umc.hackathon.apiPayload.ApiResponse;
import umc.hackathon.domain.DatePlan;
import umc.hackathon.service.DatePlanService;
import umc.hackathon.web.dto.DatePlanResponseDTO;

import java.time.LocalDate;

@RestController
@RequestMapping("/stat")
@RequiredArgsConstructor
public class StatController {

    private final DatePlanService datePlanService;

    @Operation(summary = "특정 날짜 & 모든 날짜의 '모든 과목' 통계", description = "총 공부 시간 <- 모든 과목")
    @GetMapping("/{date}") // 2025-01-02 방식
    public ApiResponse<DatePlanResponseDTO.StatDTO> dateInfoWithAllSubjects(@PathVariable LocalDate date) {

        DatePlan datePlan = datePlanService.findDatePlanByDate(date);
        DatePlanResponseDTO.StatDTO response = datePlanService.getStatWithAllSubjectsDTO(datePlan);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "특정 날짜 & 모든 날짜의 '특정 과목' 통계", description = "총 공부 시간 <- 과목(키워드)별")
    @GetMapping("/{date}/{keywordId}")
    public ApiResponse<DatePlanResponseDTO.StatDTO> dateInfoBySubject(@PathVariable LocalDate date, @PathVariable Long keywordId) {

        DatePlan datePlan = datePlanService.findDatePlanByDate(date);
        DatePlanResponseDTO.StatDTO response = datePlanService.getStatWithSubjectDTO(datePlan, keywordId);
        return ApiResponse.onSuccess(response);
    }
}