package in.bbabca.wallah.repository;

import in.bbabca.wallah.model.Course;
import in.bbabca.wallah.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByCourseAndSemesterAndActiveTrueOrderByNameAsc(Course course, Integer semester);
    List<Subject> findByActiveTrueOrderByCourseAscSemesterAscNameAsc();
}
