package in.bbabca.wallah.repository;

import in.bbabca.wallah.model.Opportunity;
import in.bbabca.wallah.model.OpportunityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {
    List<Opportunity> findByTypeAndActiveTrueOrderByFeaturedDescDeadlineAscCreatedAtDesc(OpportunityType type);
    List<Opportunity> findByActiveTrueOrderByFeaturedDescDeadlineAscCreatedAtDesc();
    List<Opportunity> findTop6ByActiveTrueOrderByFeaturedDescCreatedAtDesc();
    long countByType(OpportunityType type);
}
