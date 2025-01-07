package umc.hackathon.repository;

import umc.hackathon.domain.DatePlan;

import java.time.LocalDate;

public interface DatePlanRepositoryCustom {
    DatePlan findByDateAndThrow(LocalDate date);
}
