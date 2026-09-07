package in.bbabca.wallah.repository;

import in.bbabca.wallah.model.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByActiveTrueOrderByNoticeDateDescCreatedAtDesc();
}
