package umc.hackathon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.hackathon.apiPayload.code.status.ErrorStatus;
import umc.hackathon.apiPayload.exception.handler.DatePlanHandler;
import umc.hackathon.apiPayload.exception.handler.SubjectHandler;
import umc.hackathon.domain.DatePlan;
import umc.hackathon.domain.Subject;
import umc.hackathon.repository.DatePlanRepository;
import umc.hackathon.web.dto.DatePlanResponseDTO;
import umc.hackathon.web.dto.TimerRequestDTO;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TimerService {

    private final DatePlanRepository datePlanRepository;

    public DatePlanResponseDTO.TimerViewDTO getTimerBySubjectIdx(Integer subjectIdx) {

        DatePlan todayPlan = datePlanRepository.findByDate(LocalDate.now())
                .orElseThrow(() -> new DatePlanHandler(ErrorStatus.DATEPLAN_NOT_FOUND));
        Integer subjectGoalTime = getCurrentSubject(subjectIdx, todayPlan).getSubjectGoalTime();

        return DatePlanResponseDTO.TimerViewDTO.builder()
                .goalTime(todayPlan.getGoalTime())
                .totalStudyTime(todayPlan.getTotalStudyTime())
                .subjectGoalTime(subjectGoalTime)
                .build();
    }

    // 완료되지 않은 과목이 존재한 채로 자정이 되면
    // 새로운 DatePlan 생성하고 과목리스트 이어받음
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void midnightCopyDatePlan() {

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime yesterday = now.minusDays(1);
        DatePlan yesterdayPlan = datePlanRepository.findByDate(yesterday.toLocalDate())
                .orElseThrow(() -> new DatePlanHandler(ErrorStatus.DATEPLAN_NOT_FOUND));
        if (yesterdayPlan == null) {
            return;
        }

        List<Subject> remainSubjectList = yesterdayPlan.getSubjectList().stream()
                .filter(subject -> !subject.getCompleted())  // 완료되지 않은 subject만
                .map(Subject::copy) // 오늘의 datePlan에 깊은 복사
                .collect(Collectors.toList());

        DatePlan newDatePlan = DatePlan.builder()
                .date(now.toLocalDate())
                .goalTime(0)
                .totalStudyTime(0.0f)
                .subjectList(remainSubjectList)
                .build();

        datePlanRepository.save(newDatePlan);
    }

    @Transactional
    public void completeSubject(Integer subjectIdx, TimerRequestDTO.StudyLogDTO log) {
        Subject subject = calculateStudyTime(subjectIdx, log);
        subject.setCompleted(true);
    }

    @Transactional
    public Subject calculateStudyTime(Integer subjectIdx, TimerRequestDTO.StudyLogDTO log) {
        LocalDateTime startTime = log.getStartTime();
        LocalDateTime endTime = log.getEndTime();

        DatePlan startDatePlan = datePlanRepository.findByDate(startTime.toLocalDate())
                .orElseThrow(() -> new DatePlanHandler(ErrorStatus.DATEPLAN_NOT_FOUND));

        Subject subject = getCurrentSubject(subjectIdx, startDatePlan);

        Duration duration = Duration.between(startTime, endTime);

        // 자정이 지나고 endTime 기록
        // ** 2일 이상 연속 공부는 고려하지 않음 - 이스터에그 표시 **
        if (duration.toHours() > 24) {
            throw new DatePlanHandler(ErrorStatus.GONGSIN_APPEARED); // 이스터에그를 위한 예외
        } else if (!startTime.toLocalDate().equals(endTime.toLocalDate())) {
            return calcCrossMidnightStudyTime(startDatePlan, subject, startTime, endTime);
        } else {
            return calcSameDayStudyTime(startDatePlan, subject, startTime, endTime);
        }
    }

    private Subject getCurrentSubject(Integer subjectIdx, DatePlan datePlan) {

        if (subjectIdx < 0 || subjectIdx >= datePlan.getSubjectList().size()) {
            throw new SubjectHandler(ErrorStatus.INVALID_SUBJECT_INDEX);
        }

        return datePlan.getSubjectList().get(subjectIdx);
    }

    private Subject calcSameDayStudyTime(DatePlan startDatePlan, Subject subject, LocalDateTime startTime, LocalDateTime endTime) {
        float totalMinutes = calcTimeDifferAndHourlyStudy(startDatePlan, startTime, endTime);
        subject.addStudyTime(totalMinutes);
        return subject;
    }

    private Subject calcCrossMidnightStudyTime(DatePlan startDatePlan, Subject subject, LocalDateTime startTime, LocalDateTime endTime) {

        LocalDateTime midnight = startTime.toLocalDate().atStartOfDay().plusDays(1); // 자정
        // 어제의 subject <- [startTime ~ 자정] 공부 시간 기록
        float startToMidnight = calcTimeDifferAndHourlyStudy(startDatePlan, startTime, midnight);
        subject.addStudyTime(startToMidnight);

        // 오늘의 새로운 datePlan에서, 어제와 같은 이름 과목 찾기 (id가 다르므로)
        DatePlan todayNewPlan = datePlanRepository.findByDate(endTime.toLocalDate())
                .orElseThrow(() -> new DatePlanHandler(ErrorStatus.DATEPLAN_NOT_FOUND));
        Subject nextDaySubject = findNextDaySubject(todayNewPlan, subject.getSubjectName());

        // 오늘의 subject <- [자정 ~ endTime] 시간 기록
        float midnightToEndTime = calcTimeDifferAndHourlyStudy(todayNewPlan, midnight, endTime);
        nextDaySubject.addStudyTime(midnightToEndTime);

        return nextDaySubject;
    }

    private Subject findNextDaySubject(DatePlan datePlan, String subjectName) {
        return datePlan.getSubjectList().stream()
                .filter(s -> s.getSubjectName().equals(subjectName))
                .findAny()
                .orElseThrow(() -> new SubjectHandler(ErrorStatus.SUBJECT_NOT_FOUND));
    }

    // LocalDateTime 시간 차이 계산해서 리턴
    //       계산하는 동안 - 시간 별 공부 분포까지 기록!
    private float calcTimeDifferAndHourlyStudy(DatePlan datePlan, LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start, end);
        float totalMinutes = duration.toMinutes();

        int startHour = start.getHour();
        int endHour = end.getHour();
        if (endHour == 0) endHour = 24;

        // 시작 시간
        datePlan.getHourlyStudyTimes().merge(startHour, 60 - start.getMinute(), Integer::sum);
        // 중간 시간
        for (int hour = startHour + 1; hour < endHour; hour++) {
            datePlan.getHourlyStudyTimes().merge(hour % 24, 60, Integer::sum);
        }
        // 종료 시간
        if (startHour != endHour) {
            datePlan.getHourlyStudyTimes().merge(endHour % 24, end.getMinute(), Integer::sum);
        }

        return totalMinutes;
    }
}
