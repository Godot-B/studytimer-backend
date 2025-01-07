package umc.hackathon.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import umc.hackathon.apiPayload.code.status.ErrorStatus;
import umc.hackathon.apiPayload.exception.handler.DatePlanHandler;
import umc.hackathon.domain.DatePlan;

import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class DatePlanRepositoryCustomImpl implements DatePlanRepositoryCustom {

    private final EntityManager em;

    @Override
    public DatePlan findByDateAndThrow(LocalDate date) {

        String jpql = "SELECT d FROM DatePlan d WHERE d.date = :date";
        TypedQuery<DatePlan> query = em.createQuery(jpql, DatePlan.class);
        query.setParameter("date", date);

        DatePlan result = query.getSingleResult();

        if (result == null) {
            throw new DatePlanHandler(ErrorStatus.DATEPLAN_NOT_FOUND);
        }

        return result;
    }
}
