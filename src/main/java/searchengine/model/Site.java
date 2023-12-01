package searchengine.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table (name = "`site`")
@Getter
@Setter
public class Site {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated (EnumType.STRING)
    @Column (name = "`status`", nullable = false, columnDefinition = "ENUM('INDEXING', 'INDEXED', 'FAILED')")
    private Status status;

    @Column (name = "`status_time`", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime statusTime;

    @Column (name = "`last_error`", columnDefinition = "VARCHAR(255)")
    private String lastError;

    @Column (name = "`url`", nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column (name = "`name`", nullable = false, columnDefinition = "VARCHAR(255)")
    private String name;

    public Site clone() {
        Site result = new Site();
        result.setName(this.getName());
        result.setUrl(this.getUrl());
        result.setLastError(this.getLastError());
        result.setStatus(this.getStatus());
        result.setStatusTime(this.getStatusTime());
        return result;
    }
}
