package umc.hackathon.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import umc.hackathon.domain.common.BaseEntity;

@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Subject extends BaseEntity {       // 과목이자 타이머

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dateplan_id", nullable = false)
    private DatePlan datePlan;

    private String subjectName;

    private Integer subjectGoalTime; // 과목 목표 공부 시간 : 분 단위

    private Float subjectStudyTime; // 과목 실제 공부 시간 : '소수점' 분 단위

    private Integer breakTime; // 휴식 시간 : 분 단위

    private Boolean onListView; // 과목 리스트 view 여부

    private Boolean started;

    private Boolean completed;

    public void setOnListView(Boolean onListView) {
        this.onListView = onListView;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void addSubjectGoalTime(Integer subjectGoalTime) {
        this.subjectGoalTime += subjectGoalTime;
    }

    public void addStudyTime(Float studyTime) {
        if (subjectStudyTime == null) {
            subjectStudyTime = studyTime;
        } else {
            this.subjectStudyTime += studyTime;
        }

        datePlan.updateTotalStudyTime(studyTime); // datePlan에 전체 공부 시간도 업데이트

        if (subjectStudyTime >= subjectGoalTime) {
            completed = true;
        }
    }

    public void setDatePlan(DatePlan datePlan) {
        this.datePlan = datePlan;
    }

    public void setKeyword(Keyword keyword) {
        this.keyword = keyword;
    }

    // 자정 이벤트 위한 깊은 복사
    public Subject copy() {
        return Subject.builder()
                .subjectName(subjectName)
                .subjectGoalTime(subjectGoalTime)
                .subjectStudyTime(0.0f)
                .breakTime(breakTime)
                .onListView(onListView)
                .started(started)
                .completed(completed)
                .keyword(keyword)
                .datePlan(datePlan)
                .build();
    }
}