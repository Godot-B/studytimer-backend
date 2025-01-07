package umc.hackathon.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import umc.hackathon.apiPayload.ApiResponse;
import umc.hackathon.domain.DatePlan;
import umc.hackathon.domain.Subject;
import umc.hackathon.service.DatePlanService;
import umc.hackathon.service.SubjectService;
import umc.hackathon.web.converter.DatePlanConverter;
import umc.hackathon.web.converter.SubjectConverter;
import umc.hackathon.web.dto.DatePlanRequestDTO;
import umc.hackathon.web.dto.DatePlanResponseDTO;
import umc.hackathon.web.dto.SubjectRequestDTO;
import umc.hackathon.web.dto.SubjectResponseDTO;

import java.util.List;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    private final SubjectService subjectService;
    private final DatePlanService datePlanService;

    /**
     * 홈 첫 계획 화면
     */
    @Operation(summary = "오늘의 총 목표 시간 설정", description = "홈 화면에서 총 목표 시간을 설정, 또는 수정합니다.")
    @PostMapping("/set")
    public ApiResponse<DatePlanResponseDTO.SetResultDTO> setGoalTime(@Valid @RequestBody DatePlanRequestDTO.SetGoalDTO request) {

        DatePlan datePlan = datePlanService.createOrUpdateDatePlan(request);
        return ApiResponse.onSuccess(DatePlanConverter.toSetResultDTO(datePlan));
    }

    /**
     * 더 자세한 계획 세우기
     */
    @Operation(summary = "오늘의 과목 리스트 조회", description = "오늘의 과목 리스트를 조회합니다.")
    @GetMapping("/lists")
    public ApiResponse<List<SubjectResponseDTO.SubjectPreviewDTO>> getSubjectsList() {

        List<Subject> subjectList = datePlanService.getTodaySubjectsList();
        return ApiResponse.onSuccess(SubjectConverter.toSubjectPreviewListDTO(subjectList));
    }

    /**
     * [도전 과제] 키워드 추가
     */
    @Operation(summary = "[도전 과제] 추천 키워드 목록", description = "과목 이름 입력값마다 키워드를 조회합니다.")
    @GetMapping("/lists/add")
    public ApiResponse<List<String>> searchKeywords(@RequestParam String userInput) {

        List<String> keywordNameList = subjectService.searchKeywords(userInput);
        return ApiResponse.onSuccess(keywordNameList);
    }

    @Operation(summary = "과목 추가", description = "새로운 과목을 추가합니다.")
    @PostMapping("/lists/add")
    public ApiResponse<SubjectResponseDTO.SubjectPreviewDTO> addSubject(@RequestBody SubjectRequestDTO.AddSubjectDTO request) {
        Subject subject = subjectService.addSubject(request);
        return ApiResponse.onSuccess(SubjectConverter.toSubjectPreviewDTO(subject));
    }

    /**
     * 과목 삭제
     */
    @Operation(summary = "과목 삭제", description = "지정한 과목을 삭제합니다." +
            "단, 시작한 적이 있는 과목이라면 계획에서만 삭제합니다.")
    @PatchMapping("/lists/{subjectId}")
    public ApiResponse<DatePlanResponseDTO.DeleteResultDTO> deleteSubject(@PathVariable Long subjectId) {
        String deleteSubjectName = subjectService.deleteSubject(subjectId);
        return ApiResponse.onSuccess(SubjectConverter.toDeleteResultDTO(subjectId, deleteSubjectName));
    }
}
