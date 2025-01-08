package umc.hackathon.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import umc.hackathon.apiPayload.ApiResponse;
import umc.hackathon.domain.Subject;
import umc.hackathon.service.SubjectService;
import umc.hackathon.service.TimerService;
import umc.hackathon.web.converter.SubjectConverter;
import umc.hackathon.web.dto.TimerRequestDTO;
import umc.hackathon.web.dto.TimerResponseDTO;


@RestController
@RequestMapping("/timer") //2025-01-02
@RequiredArgsConstructor
public class TimerController {

    private final TimerService timerService;
    private final SubjectService subjectService;

    /**
     * 타이머 화면
     */
    @Operation(summary = "타이머 조회", description = "타이머를 조회합니다.")
    @GetMapping("/{subjectIdx}")
    public ApiResponse<TimerResponseDTO.TimerViewDTO> getTimer(@PathVariable Integer subjectIdx) {

        TimerResponseDTO.TimerViewDTO response = timerService.getTimerBySubjectIdx(subjectIdx);
        return ApiResponse.onSuccess(response);
    }

    /**
     * 타이머 시작 & 선택적 키워드 저장
     */
    @Operation(summary = "타이머 시작 & 키워드 저장", description = "과목을 처음 공부할 때만 키워드가 저장이 됩니다.")
    @PostMapping("/{subjectIdx}/start")
    public ApiResponse<String> postKeyword(@PathVariable Integer subjectIdx) {
        String result = subjectService.optionalSaveKeyword(subjectIdx);
        return ApiResponse.onSuccess("타이머 시작 성공" + result);
    }

    /**
     * 타이머 일시정지
     */
    @Operation(summary = "타이머 일시정지", description = "타이머를 일시정지하고, 공부 시간이 업데이트됩니다." +
            " 단, 타이머 동작 중 자정을 지나면 날짜 및 일부 과목이 복제됩니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청 성공")
    @PatchMapping("/{subjectIdx}/pause")
    public ApiResponse<TimerResponseDTO.RemainTimeDTO> pause(@PathVariable Integer subjectIdx, @RequestBody TimerRequestDTO.StudyLogDTO request) {
        Subject currentSubject = timerService.calculateStudyTime(subjectIdx, request);
        return ApiResponse.onSuccess(SubjectConverter.toRemainTimeDTO(currentSubject));
    }

    /**
     * 타이머 초기화
     */
    @Operation(summary = "타이머 초기화", description = "타이머를 초기화하고, 공부 시간이 업데이트되며 과목의 공부가 완료됩니다." +
        " 단, 타이머 동작 중 자정을 지나면 날짜 및 일부 과목이 복제됩니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청 성공")
    @PatchMapping("/{subjectIdx}/stop")
    public ApiResponse<TimerResponseDTO.RemainTimeDTO> stop(@PathVariable Integer subjectIdx, @RequestBody TimerRequestDTO.StudyLogDTO request) {
        Subject currentSubject = timerService.completeSubject(subjectIdx, request);
        return ApiResponse.onSuccess(SubjectConverter.toRemainTimeDTO(currentSubject));
    }
}
