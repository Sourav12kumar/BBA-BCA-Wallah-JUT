package in.bbabca.wallah.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Notice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(length = 3000)
    private String content;
    private String category;
    private String externalUrl;
    private LocalDate noticeDate = LocalDate.now();
    private boolean active = true;
    private LocalDateTime createdAt = LocalDateTime.now();
}
