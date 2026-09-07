package in.bbabca.wallah.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "course", "semester"}))
public class Subject {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String code;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private Course course;
    @Column(nullable = false)
    private Integer semester;
    private boolean active = true;
}
