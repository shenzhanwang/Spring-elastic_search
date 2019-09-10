package boot.spring.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import boot.spring.elasticindex.SougoulogIndex;

public interface SougoulogRepository extends ElasticsearchRepository<SougoulogIndex, Integer> {

}
