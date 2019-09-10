package boot.spring.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import boot.spring.elasticindex.CountryIndex;

public interface CountryRepository extends ElasticsearchRepository<CountryIndex, Integer> {

}
