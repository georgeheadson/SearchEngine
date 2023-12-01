package searchengine.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table (name = "`lemma`")
@Getter
@Setter
public class Lemma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne (cascade = CascadeType.MERGE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Site site;

    @Column (name = "`lemma`", nullable = false, columnDefinition = "VARCHAR(255)")
    private String lemma;

    @Column (name = "`frequency`", nullable = false, columnDefinition = "INT")
    private int frequency;
}
