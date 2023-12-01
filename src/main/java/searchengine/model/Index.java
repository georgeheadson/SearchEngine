package searchengine.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table (name = "`index`")
@Getter
@Setter
public class Index {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne (cascade = CascadeType.MERGE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Page page;

    @ManyToOne (cascade = CascadeType.MERGE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Lemma lemma;

    @Column (name = "`rank`", nullable = false, columnDefinition = "FLOAT")
    private float rank;
}
