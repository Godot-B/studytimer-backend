package umc.hackathon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.hackathon.apiPayload.code.status.ErrorStatus;
import umc.hackathon.apiPayload.exception.handler.KeywordHandler;
import umc.hackathon.domain.DatePlan;
import umc.hackathon.domain.Keyword;
import umc.hackathon.domain.Subject;
import umc.hackathon.repository.DatePlanRepository;
import umc.hackathon.repository.KeywordRepository;
import umc.hackathon.web.converter.DatePlanConverter;
import umc.hackathon.web.dto.DatePlanRequestDTO;
import umc.hackathon.web.dto.DatePlanResponseDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.floor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DatePlanService {

    private final DatePlanRepository datePlanRepository;
    private final KeywordRepository keywordRepository;

    @Transactional
    public DatePlan createOrUpdateDatePlan(DatePlanRequestDTO.SetGoalDTO request) {
        if (datePlanRepository.findByDate(LocalDate.now()).isPresent()) {
            return updateGoalTime(request);
        } else {
            DatePlan newDatePlan = DatePlanConverter.toDatePlan(request);
            return datePlanRepository.save(newDatePlan);
        }
    }

    @Transactional
    public DatePlan updateGoalTime(DatePlanRequestDTO.SetGoalDTO request) {
        DatePlan datePlan = datePlanRepository.findByDate(LocalDate.now())
                .orElseGet(() -> createOrUpdateDatePlan(request)); // 목표 시간 수정 화면에서 자정이 지난 경우

        datePlan.changeGoalTime(request.getGoalHour(), request.getGoalMinute());
        return datePlan;
    }

    public List<Subject> getTodaySubjectsList() {
        DatePlan todayPlan = datePlanRepository.findByDateAndThrow(LocalDate.now());
        return todayPlan.getSubjectList().stream()
                .filter(Subject::getOnListView)
                .toList();
    }

    public DatePlan findDatePlanByDate(LocalDate date) {
        return datePlanRepository.findByDateAndThrow(date);
    }

    // 잔디 색깔 결정
    private int checkColorFlag(float totalStudyTime) {
        int colorFlag = (int) floor(totalStudyTime / 90f); // 1시간 반마다 잔디 색이 진해진다.
        if (colorFlag >= 4)
            colorFlag = 4;

        return colorFlag;
    }

    public DatePlanResponseDTO.StatDTO getStatWithAllSubjectsDTO(DatePlan datePlan) {

        List<String> allKeywords = keywordRepository.findAll().stream().map(Keyword::getKeywordName).toList();
        List<DatePlan> allDatePlan = datePlanRepository.findAll();
        List<DatePlanResponseDTO.DateInfoDTO> allDateInfoDTOList = new ArrayList<>();

        int colorFlag;
        for (DatePlan iterDatePlan : allDatePlan) {
            colorFlag = checkColorFlag(iterDatePlan.getTotalStudyTime());
            allDateInfoDTOList.add(DatePlanResponseDTO.DateInfoDTO.builder()
                    .date(iterDatePlan.getDate())
                    .colorFlag(colorFlag)
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

        int colorFlag;
        for (Subject subject : subjectsByKeyword) {
            colorFlag = checkColorFlag(subject.getSubjectStudyTime());
            allDateInfoDTOList.add(DatePlanResponseDTO.DateInfoDTO.builder()
                    .date(subject.getDatePlan().getDate())
                    .colorFlag(colorFlag)
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
}
