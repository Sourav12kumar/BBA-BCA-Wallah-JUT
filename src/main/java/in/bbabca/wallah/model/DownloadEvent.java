package in.bbabca.wallah.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "download_events", indexes = {
        @Index(name = "idx_download_event_time", columnList = "downloadedAt"),
        @Index(name = "idx_download_event_resource", columnList = "resourceId")
})
@Getter
@Setter
@NoArgsConstructor
public class DownloadEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long resourceId;

    @Column(nullable = false)
    private String resourceTitle;

    @Column(nullable = false)
    private LocalDateTime downloadedAt = LocalDateTime.now();

    public DownloadEvent(Long resourceId, String resourceTitle) {
        this.resourceId = resourceId;
        this.resourceTitle = resourceTitle;
        this.downloadedAt = LocalDateTime.now();
    }
}
