package boot.spring.elastic.search.service;

import java.util.HashMap;

import org.elasticsearch.action.search.SearchResponse;

import boot.spring.pagemodel.AYRequest;
import boot.spring.pagemodel.ElasticSearchRequest;

public interface SearchService {
	/**
	 * 多字段搜索
	 * @param request
	 * @return
	 */
	SearchResponse multiSearch(ElasticSearchRequest request);
	
	/**
	 * 精准搜索
	 */
	SearchResponse termSearch(String index, String field,String term);
	/**
	 * 日期分面搜索
	 */
	HashMap<String, Long> dateHistogram(ElasticSearchRequest request);
	
	/**
	 * terms聚集
	 */
	SearchResponse termsAggs(String index, AYRequest request);
	
}
