package boot.spring.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import boot.spring.elasticindex.ActorIndex;

public interface ActorRepository extends ElasticsearchRepository<ActorIndex, Integer> {
	Optional<ActorIndex> findById(short id);

    Page<ActorIndex> findByFirstName(String firstName, Pageable pageable);

    Page<ActorIndex> findByLastName(String lastName, Pageable pageable);
    
    Page<ActorIndex> findByLastUpdate(String lastName, Pageable pageable);
}
