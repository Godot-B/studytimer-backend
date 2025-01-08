package umc.hackathon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.hackathon.apiPayload.code.status.ErrorStatus;
import umc.hackathon.apiPayload.exception.handler.SubjectHandler;
import umc.hackathon.domain.DatePlan;
import umc.hackathon.domain.Keyword;
import umc.hackathon.domain.Subject;
import umc.hackathon.repository.DatePlanRepository;
import umc.hackathon.repository.KeywordRepository;
import umc.hackathon.repository.SubjectRepository;
import umc.hackathon.web.dto.SubjectRequestDTO;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final DatePlanRepository datePlanRepository;
    private final KeywordRepository keywordRepository;

    public List<String> searchKeywords(String userInput) {
        return keywordRepository.findByKeywordNameContainingIgnoreCase(userInput).stream()
                .map(Keyword::getKeywordName).toList();
    }

    @Transactional
    public Subject addSubject(SubjectRequestDTO.AddSubjectDTO request) {

        int goalTime = request.getGoalHour() * 60 + request.getGoalMinute();

        // 추가 시점 날짜의 과목 리스트 중, 같은 이름의 과목 있는지 검사
        DatePlan datePlan = datePlanRepository.findByDateAndThrow(LocalDate.now());
        List<Subject> subjectList = datePlan.getSubjectList();
        Subject findSubject = subjectList.stream()
                .filter(subject -> subject.getSubjectName().equals(request.getSubjectName()))
                .findAny().orElse(null);

        // 없다면 신규 생성
        if (findSubject == null) {

            Subject newSubject = Subject.builder()
                    .subjectName(request.getSubjectName())
                    .subjectGoalTime(goalTime)
                    .subjectStudyTime(0.0f)
                    .breakTime(10) // 휴식시간 기본값 10분
                    .onListView(true)
                    .started(false)
                    .completed(false)
                    .build();

            newSubject.setDatePlan(datePlanRepository.findByDateAndThrow(LocalDate.now()));
            subjectRepository.save(newSubject);

            return newSubject;
        } else {
            // 있던 과목으로 리턴 & 목표공부시간 +=으로 더해주기
            findSubject.setCompleted(false);
            findSubject.addSubjectGoalTime(goalTime);
            datePlan.getSubjectList().add(findSubject);
            return findSubject;
        }
    }

    @Transactional
    public String deleteSubject(Long subjectId) {

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectHandler(ErrorStatus.SUBJECT_NOT_FOUND));
        Boolean isStarted = subject.getStarted();

        if (isStarted) {
            subject.setOnListView(false);
            return "계획에서만 " + subject.getSubjectName();
        } else {
            Keyword keyword = subject.getKeyword();

            String deleteSubjectName = subject.getSubjectName();
            subjectRepository.delete(subject);

            if (keyword != null && keyword.getSubjects().isEmpty())
                keywordRepository.delete(keyword);

            return deleteSubjectName;
        }
    }

    @Transactional
    public String optionalSaveKeyword(Integer subjectIdx) {

        Subject subject = datePlanRepository.findByDateAndThrow(LocalDate.now())
                .getSubjectList().get(subjectIdx);

        if (subject == null)
            throw new SubjectHandler(ErrorStatus.SUBJECT_NOT_FOUND);

        if (subject.getStarted()) {
            return "";
        } else {
            subject.setStarted(true);
            String keywordName = subject.getSubjectName();

            Keyword keyword = Keyword.builder().keywordName(keywordName).build();
            keywordRepository.save(keyword);

            subject.setKeyword(keyword);

            return ", " + keywordName + " 키워드 저장 성공";
        }
    }
}