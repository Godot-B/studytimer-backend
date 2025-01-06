package umc.hackathon.domain;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import umc.hackathon.domain.common.BaseEntity;

import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DatePlan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private Integer goalTime; // 해당 날짜의 목표 공부 시간 : 분 단위

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "study_time_by_hour")
    private Map<Integer, Integer> hourlyStudyTimes = initializeHourlyMap(); // 시간 별 공부 시간 분포 : 분 단위

    private static Map<Integer, Integer> initializeHourlyMap() {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < 24; i++) {
            map.put(i, 0);  // 각 시간대 초기값 0으로 설정
        }
        return map;
    }

    private Float totalStudyTime; // ☑️ 전체 과목의 실제 공부 시간을 더해서 계산

    @Builder.Default
    @OneToMany(mappedBy = "datePlan", cascade = CascadeType.ALL)
    private List<Subject> subjectList = new ArrayList<>();

    public void changeGoalTime(int goalHour, int goalMinute) {
        this.goalTime = goalHour * 60 + goalMinute;
    }

    public void updateTotalStudyTime(Float studyTime) {
        if (this.totalStudyTime == null) {
            this.totalStudyTime = studyTime;
        } else {
            this.totalStudyTime += studyTime;
        }
    }

}
