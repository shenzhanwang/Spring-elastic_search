package boot.spring.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import boot.spring.elasticindex.SougoulogIndex;
import boot.spring.pagemodel.SougoulogSearchRequest;

public interface SougoulogSearchService {
	Page<SougoulogIndex> searchDefault(SougoulogSearchRequest request, Pageable p);
	
	Page<SougoulogIndex> searchResult(SougoulogSearchRequest request, Pageable p);
}
