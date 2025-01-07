package umc.hackathon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.hackathon.domain.DatePlan;
import umc.hackathon.domain.Subject;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByDatePlan(DatePlan datePlan);
}
