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
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OpportunityType type;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String role;

    private String location;
    private String batch;
    private String eligibleCourses;
    private String eligibility;
    private String applyUrl;
    private LocalDate deadline;

    @Column(length = 2500)
    private String description;

    private boolean featured = false;
    private boolean active = true;
    private LocalDateTime createdAt = LocalDateTime.now();
}
