package in.bbabca.wallah.repository;

import in.bbabca.wallah.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AcademicResourceRepository extends JpaRepository<AcademicResource, Long> {
    List<AcademicResource> findByActiveTrueOrderByCreatedAtDesc();
    List<AcademicResource> findByCourseAndSemesterAndTypeAndActiveTrueOrderByCreatedAtDesc(Course course, Integer semester, ResourceType type);
    List<AcademicResource> findByCourseAndSemesterAndActiveTrueOrderByCreatedAtDesc(Course course, Integer semester);
    List<AcademicResource> findBySubjectIdAndActiveTrueOrderByCreatedAtDesc(Long subjectId);
    List<AcademicResource> findBySubjectIdAndTypeAndActiveTrueOrderByCreatedAtDesc(Long subjectId, ResourceType type);
    List<AcademicResource> findByTitleContainingIgnoreCaseAndActiveTrueOrderByCreatedAtDesc(String keyword);
    List<AcademicResource> findTop6ByActiveTrueAndFileUrlIsNotNullOrderByDownloadCountDescCreatedAtDesc();

    @Modifying
    @Transactional
    @Query("update AcademicResource r set r.downloadCount = r.downloadCount + 1 where r.id = :id and r.active = true")
    int incrementDownloadCount(@Param("id") Long id);

    @Query("select coalesce(sum(r.downloadCount), 0) from AcademicResource r")
    Long getTotalDownloads();
}
