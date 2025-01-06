package umc.hackathon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.hackathon.domain.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long>/*, SubjectRepositoryCustom*/ {
    Subject findBySubjectName(String name);
}
