package umc.hackathon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.hackathon.apiPayload.code.status.ErrorStatus;
import umc.hackathon.apiPayload.exception.handler.DatePlanHandler;
import umc.hackathon.apiPayload.exception.handler.KeywordHandler;
import umc.hackathon.domain.DatePlan;
import umc.hackathon.domain.Subject;
import umc.hackathon.domain.Keyword;
import umc.hackathon.repository.DatePlanRepository;
import umc.hackathon.repository.KeywordRepository;
import umc.hackathon.repository.SubjectRepository;
import umc.hackathon.web.converter.DatePlanConverter;
import umc.hackathon.web.dto.DatePlanRequestDTO;
import umc.hackathon.web.dto.DatePlanResponseDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DatePlanService {

    private final DatePlanRepository datePlanRepository;
    private final KeywordRepository keywordRepository;

    @Transactional
    public DatePlan createDatePlan(DatePlanRequestDTO.SetGoalDTO request) {

        if (datePlanRepository.findByDate(LocalDate.now()).isPresent()) {
            throw new DatePlanHandler(ErrorStatus.DATEPLAN_ALREADY_EXIST);
        }

        DatePlan newDatePlan = DatePlanConverter.toDatePlan(request);

        return datePlanRepository.save(newDatePlan);
    }

    @Transactional
    public void updateGoalTime(DatePlanRequestDTO.SetGoalDTO request) {
        DatePlan datePlan = datePlanRepository.findByDate(LocalDate.now())
                .orElseGet(() -> createDatePlan(request)); // 목표 시간 수정 화면에서 자정이 지난 경우

        datePlan.changeGoalTime(request.getGoalHour(), request.getGoalMinute());
    }

    public DatePlan findDatePlanByDate(LocalDate date) {
        return datePlanRepository.findByDate(date)
                .orElseThrow(() -> new DatePlanHandler(ErrorStatus.DATEPLAN_NOT_FOUND));
    }

    public DatePlanResponseDTO.StatDTO getStatWithAllSubjectsDTO(DatePlan datePlan) {

        List<String> allKeywords = keywordRepository.findAll().stream().map(Keyword::getKeywordName).toList();
        List<DatePlan> allDatePlan = datePlanRepository.findAll();
        List<DatePlanResponseDTO.DateInfoDTO> allDateInfoDTOList = new ArrayList<>();

        for (DatePlan iterDatePlan : allDatePlan) {
            allDateInfoDTOList.add(DatePlanResponseDTO.DateInfoDTO.builder()
                    .date(iterDatePlan.getDate())
                    .totalStudyTime(iterDatePlan.getTotalStudyTime())
                    .build());
        }

        return getStatDTO(datePlan, allKeywords, allDateInfoDTOList);
    }

    public DatePlanResponseDTO.StatDTO getStatWithSubjectDTO(DatePlan datePlan, Long keywordId) {

        List<String> allKeywords = keywordRepository.findAll().stream().map(Keyword::getKeywordName).toList();
        Keyword keyword = keywordRepository.findById(keywordId)
                .orElseThrow(()-> new KeywordHandler(ErrorStatus.KEYWORD_NOT_FOUND));

        List<Subject> subjectsByKeyword = keyword.getSubjects();
        List<DatePlanResponseDTO.DateInfoDTO> allDateInfoDTOList = new ArrayList<>();

        for (Subject subject : subjectsByKeyword) {
            allDateInfoDTOList.add(DatePlanResponseDTO.DateInfoDTO.builder()
                    .date(subject.getDatePlan().getDate())
                    .totalStudyTime(subject.getSubjectStudyTime())
                    .build());
        }

        return getStatDTO(datePlan, allKeywords, allDateInfoDTOList);
    }

    private DatePlanResponseDTO.StatDTO getStatDTO(DatePlan datePlan, List<String> allKeywords, List<DatePlanResponseDTO.DateInfoDTO> allDateInfoDTOList) {
        List<DatePlanResponseDTO.HourlyStudyTimesDTO> hourlyStudyTimesDTOList = datePlan.getHourlyStudyTimes().entrySet().stream()
                .map(entry ->
                        new DatePlanResponseDTO.HourlyStudyTimesDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return DatePlanResponseDTO.StatDTO.builder()
                .allKeywords(allKeywords)
                .date(datePlan.getDate())
                .goalTime(datePlan.getGoalTime())
                .totalStudyTime(datePlan.getTotalStudyTime())
                .allDateInfoList(allDateInfoDTOList)
                .hourlyStudyTimesByDate(hourlyStudyTimesDTOList)
                .build();
    }

    // 뷰 테스트용 DB 업데이트 메서드
/*    @Transactional
    public void calcTotalStudyByHour() {
        for (DatePlan datePlan : datePlanRepository.findAll()) {
            float totalStudyTimes = datePlan.getHourlyStudyTimes().values().stream().
                    mapToInt(Integer::intValue).sum();
            totalStudyTimes += (float) Math.random();
            datePlan.setTotalStudyTime(totalStudyTimes);
        }
    }*/
}
