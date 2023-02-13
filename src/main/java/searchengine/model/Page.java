package searchengine.model;

import javax.persistence.*;

@Entity
@Table (name = "page", indexes = {@Index (name="idx_page_path", columnList = "path", unique = false)})
public class Page {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne (cascade = CascadeType.ALL)
    private Site site;

    @Column (name = "path", nullable = false, columnDefinition = "TEXT")
    private String path;

    @Column (name = "code", nullable = false, columnDefinition = "INT")
    private int code;

    @Column (name = "content", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

}
