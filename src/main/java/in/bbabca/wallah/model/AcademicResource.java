package in.bbabca.wallah.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AcademicResource {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private Course course;

    @Column(nullable = false)
    private Integer semester;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private ResourceType type;

    private String sessionYear;
    private String fileUrl;

    @Column(length = 1500)
    private String description;

    @Column(nullable = false)
    private long downloadCount = 0L;

    private boolean active = true;
    private LocalDateTime createdAt = LocalDateTime.now();
}
