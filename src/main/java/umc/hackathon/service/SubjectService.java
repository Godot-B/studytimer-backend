package umc.hackathon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.hackathon.apiPayload.code.status.ErrorStatus;
import umc.hackathon.apiPayload.exception.handler.DatePlanHandler;
import umc.hackathon.apiPayload.exception.handler.SubjectHandler;
import umc.hackathon.domain.Subject;
import umc.hackathon.domain.Keyword;
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

    // 홈 화면에 필요한 과목 리스트 반환
    public List<Subject> getSubjectsList() {
        List<Subject> subjects = subjectRepository.findAll();
        return subjects;
    }

    public List<String> searchKeywords(String userInput) {

        return keywordRepository.findByKeywordNameContainingIgnoreCase(userInput).stream()
                .map(Keyword::getKeywordName).toList();
    }

    @Transactional
    public Subject addSubject(SubjectRequestDTO.AddSubjectDTO request) {

        // 로직 구현 필요
        int goalTime = request.getGoalHour() * 60 + request.getGoalMinute();

        Subject findSubject = subjectRepository.findBySubjectName(request.getSubjectName());
        Boolean ifCompleted = findSubject.getCompleted();

        if (ifCompleted = true) {
            // 같은 이름, 완료한 과목 : 해당 과목으로 리턴 & 목표공부시간 +=으로 더해주기
            findSubject.setCompleted(false);
            findSubject.addSubjectGoalTime(goalTime);
            return findSubject;
        } else if (ifCompleted = false) {
            // 같은 이름, 완료하지 않은 과목
            throw new SubjectHandler(ErrorStatus.SUBJECT_ALREADY_EXISTS);
        } else  {
            Subject newSubject = Subject.builder()
                    .subjectName(request.getSubjectName())
                    .subjectGoalTime(goalTime)
                    .subjectStudyTime(0.0f)
                    .breakTime(10) // 휴식시간 기본값 10분
                    .started(false)
                    .completed(false)
                    .build();

            newSubject.setDatePlan(datePlanRepository.findByDate(LocalDate.now())
                    .orElseThrow(() -> new DatePlanHandler(ErrorStatus.DATEPLAN_NOT_FOUND)));
            subjectRepository.save(newSubject);

            return newSubject;
        }
    }

    @Transactional
    public String deleteSubject(Long subjectId) {

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectHandler(ErrorStatus.SUBJECT_NOT_FOUND));

        Keyword keyword = subject.getKeyword();

        String deleteSubjectName = subject.getSubjectName();
        subjectRepository.delete(subject);

        if (keyword.getSubjects().isEmpty())
            keywordRepository.delete(keyword);

        return deleteSubjectName;
    }

    @Transactional
    public String optionalSaveKeyword(Integer subjectIdx) {

        Subject subject = datePlanRepository.findByDate(LocalDate.now())
                .orElseThrow(() -> new DatePlanHandler(ErrorStatus.DATEPLAN_NOT_FOUND))
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