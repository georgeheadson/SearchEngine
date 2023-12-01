package searchengine.model;

import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.persistence.Index;

@Entity
@Table (name = "`page`", indexes = {@Index(columnList = "path", name = "`idx_path`")})
@Data
public class Page {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne (cascade = CascadeType.MERGE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Site site;

    @Column (name = "`path`")//, columnDefinition = "TEXT NOT NULL, Index(path(255))")
    private String path;

    @Column (name = "`code`", nullable = false, columnDefinition = "INT")
    private int code;

    @Column (name = "`content`", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;
}
