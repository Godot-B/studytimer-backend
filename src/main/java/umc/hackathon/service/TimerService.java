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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TimerService {

    private final DatePlanRepository datePlanRepository;

    public DatePlanResponseDTO.TimerViewDTO getTimerBySubjectIdx(Integer subjectIdx) {

        DatePlan todayPlan = datePlanRepository.findByDateAndThrow(LocalDate.now());
        Subject subject = getSubjectByIndex(subjectIdx, todayPlan);
        Integer subjectGoalTime = subject.getSubjectGoalTime();
        Float subjectStudyTime = subject.getSubjectStudyTime();

        return DatePlanResponseDTO.TimerViewDTO.builder()
                .subjectName(subject.getSubjectName())
                .goalTime(todayPlan.getGoalTime())
                .totalStudyTime(todayPlan.getTotalStudyTime())
                .subjectGoalTime(subjectGoalTime)
                .remainTime(subjectGoalTime - subjectStudyTime)
                .build();
    }

    // 완료되지 않은 과목이 존재한 채로 자정이 되면
    // 새로운 DatePlan 생성하고 과목리스트 이어받음
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void midnightCopyDatePlan() {

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime yesterday = now.minusDays(1);
        DatePlan yesterdayPlan = datePlanRepository.findByDateAndThrow(yesterday.toLocalDate());
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

        Duration duration = Duration.between(startTime, endTime);
        // ** 타이머가 24시간 동작 ? 고려 X - 이스터에그 표시 **
        if (duration.toHours() >= 24) {
            throw new DatePlanHandler(ErrorStatus.GONGSIN_APPEARED); // 이스터에그를 위한 예외
        }

        if (startTime.toLocalDate().equals(endTime.toLocalDate())) {
            return calcSameDayStudyTime(subjectIdx, startTime, endTime);
        } else {
            // 자정이 지나고 endTime 기록
            return calcCrossMidnightStudyTime(subjectIdx, startTime, endTime);
        }
    }

    private Subject getSubjectByIndex(Integer subjectIdx, DatePlan datePlan) {

        if (subjectIdx < 0 || subjectIdx >= datePlan.getSubjectList().size()) {
            throw new SubjectHandler(ErrorStatus.INVALID_SUBJECT_INDEX);
        }

        return datePlan.getSubjectList().get(subjectIdx);
    }

    private Subject calcSameDayStudyTime(Integer subjectIdx, LocalDateTime startTime, LocalDateTime endTime) {

        DatePlan startDatePlan = datePlanRepository.findByDateAndThrow(startTime.toLocalDate());
        float totalMinutes = calcSameDayTimeDifferAndLog(startDatePlan, startTime, endTime);

        Subject subject = getSubjectByIndex(subjectIdx, startDatePlan);
        subject.addStudyTime(totalMinutes);
        return subject;
    }

    private Subject calcCrossMidnightStudyTime(Integer subjectIdx, LocalDateTime startTime, LocalDateTime endTime) {
        // startTime의 다음 날 자정
        LocalDateTime midnight = startTime.toLocalDate().atStartOfDay()
                .plusDays(1);

        /*
          자정 이전 기록
         */
        DatePlan startDatePlan = datePlanRepository.findByDateAndThrow(startTime.toLocalDate());
        Subject subject = getSubjectByIndex(subjectIdx, startDatePlan);

        // 어제의 subject <- [startTime ~ 자정] 공부 시간 기록
        float startToMidnight = calcSameDayTimeDifferAndLog(startDatePlan, startTime, midnight);
        subject.addStudyTime(startToMidnight);

        /*
          자정 이후 기록
          : 오늘의 새로운 datePlan에서, 어제와 같은 키워드 과목
         */
        DatePlan todayNewPlan = datePlanRepository.findByDateAndThrow(endTime.toLocalDate());
        Subject nextDaySubject = findNextDaySubject(todayNewPlan, subject.getKeyword().getId());

        // 오늘의 subject <- [자정 ~ endTime] 시간 기록
        float midnightToEndTime = calcSameDayTimeDifferAndLog(todayNewPlan, midnight, endTime);
        nextDaySubject.addStudyTime(midnightToEndTime);

        return nextDaySubject;
    }

    /**
     * 시간 별 공부 분포 기록
     * @return start와 end의 차이 (float 분 단위)
     */
    private float calcSameDayTimeDifferAndLog(DatePlan datePlan, LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start, end);
        float totalMinutes = duration.toMinutes();

        int startHour = start.getHour();
        int endHour = end.getHour();
        if (startHour != 0 && endHour == 0) {
            endHour = 24; // 00:00:00 -> 24:00:00
        }

        Map<Integer, Integer> hourlyStudyTimes = datePlan.getHourlyStudyTimes();

        if (startHour == endHour) {
            hourlyStudyTimes.merge(endHour, end.getMinute() - start.getMinute(), Integer::sum);
        } else {
            // 시작 시간
            hourlyStudyTimes.merge(startHour, 60 - start.getMinute(), Integer::sum);
            // 중간 시간
            for (int iterHour = startHour + 1; iterHour < endHour; iterHour++) {
                hourlyStudyTimes.merge(iterHour, 60, Integer::sum);
            }
            // 종료 시간
            hourlyStudyTimes.merge(endHour, end.getMinute(), Integer::sum);
        }

        return totalMinutes;
    }

    private Subject findNextDaySubject(DatePlan datePlan, Long keywordId) {
        return datePlan.getSubjectList().stream()
                .filter(s -> s.getKeyword().getId().equals(keywordId))
                .findAny()
                .orElseThrow(() -> new SubjectHandler(ErrorStatus.SUBJECT_NOT_FOUND));
    }
}
