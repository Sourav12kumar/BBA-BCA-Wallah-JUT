package in.bbabca.wallah.repository;

import in.bbabca.wallah.model.Course;
import in.bbabca.wallah.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByCourseAndSemesterAndActiveTrueOrderByNameAsc(Course course, Integer semester);
    List<Subject> findByActiveTrueOrderByCourseAscSemesterAscNameAsc();

    @Override
    @Modifying
    @Transactional
    @Query("update Subject s set s.active = false where s.id = :id")
    void deleteById(@Param("id") Long id);
}
