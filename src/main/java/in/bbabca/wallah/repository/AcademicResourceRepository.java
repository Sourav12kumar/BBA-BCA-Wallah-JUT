package in.bbabca.wallah.repository;

import in.bbabca.wallah.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AcademicResourceRepository extends JpaRepository<AcademicResource, Long> {
    List<AcademicResource> findByActiveTrueOrderByCreatedAtDesc();
    List<AcademicResource> findByCourseAndSemesterAndTypeAndActiveTrueOrderByCreatedAtDesc(Course course, Integer semester, ResourceType type);
    List<AcademicResource> findByCourseAndSemesterAndActiveTrueOrderByCreatedAtDesc(Course course, Integer semester);
    List<AcademicResource> findBySubjectIdAndActiveTrueOrderByCreatedAtDesc(Long subjectId);
    List<AcademicResource> findBySubjectIdAndTypeAndActiveTrueOrderByCreatedAtDesc(Long subjectId, ResourceType type);
    List<AcademicResource> findByTitleContainingIgnoreCaseAndActiveTrueOrderByCreatedAtDesc(String keyword);
}
