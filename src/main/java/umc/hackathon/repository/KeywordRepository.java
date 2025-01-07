package umc.hackathon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.hackathon.domain.Keyword;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    List<Keyword> findByKeywordNameContainingIgnoreCase(String keywordName);
}
