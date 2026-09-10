package in.bbabca.wallah.repository;

import in.bbabca.wallah.model.DownloadEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DownloadEventRepository extends JpaRepository<DownloadEvent, Long> {

    long countByDownloadedAtBetween(LocalDateTime start, LocalDateTime end);

    interface ResourceDownloadStat {
        Long getResourceId();
        String getResourceTitle();
        Long getDownloadCount();
    }

    @Query("""
            select d.resourceId as resourceId,
                   d.resourceTitle as resourceTitle,
                   count(d.id) as downloadCount
            from DownloadEvent d
            where d.downloadedAt between :start and :end
            group by d.resourceId, d.resourceTitle
            order by count(d.id) desc
            """)
    List<ResourceDownloadStat> findTopResourcesBetween(@Param("start") LocalDateTime start,
                                                        @Param("end") LocalDateTime end);
}
