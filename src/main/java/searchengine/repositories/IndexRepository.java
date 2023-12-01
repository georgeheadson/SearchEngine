package searchengine.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Index;

@Repository
public interface IndexRepository extends CrudRepository<Index, Integer> {

    @Query (value = "SELECT * FROM `index` WHERE `lemma_id` = :lemmaId AND `page_id` = :pageId", nativeQuery = true)
    Index indexByLemmaAndPage(int lemmaId, int pageId);

}
