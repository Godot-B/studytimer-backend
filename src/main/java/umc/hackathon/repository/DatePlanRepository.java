package umc.hackathon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.hackathon.domain.DatePlan;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DatePlanRepository extends JpaRepository<DatePlan, Long> {
    Optional<DatePlan> findByDate(LocalDate date);
}
