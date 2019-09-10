package boot.spring.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import boot.spring.elasticindex.ShopIndex;

public interface ShopRepository extends ElasticsearchRepository<ShopIndex, Integer> {
	
}
