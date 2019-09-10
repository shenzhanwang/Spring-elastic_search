package boot.spring.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import boot.spring.elasticindex.CityIndex;

public interface CityRepository extends ElasticsearchRepository<CityIndex, Integer> {
	Optional<CityIndex> findByCityid(short cityid);

    Page<CityIndex> findByCity(String city, Pageable pageable);

    Page<CityIndex> findByLastupdate(String lastupdate, Pageable pageable);
}
