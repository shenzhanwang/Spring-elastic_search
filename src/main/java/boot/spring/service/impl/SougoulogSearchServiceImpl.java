package boot.spring.service.impl;


import java.util.HashMap;

import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import boot.spring.elasticindex.SougoulogIndex;
import boot.spring.pagemodel.SougoulogSearchRequest;
import boot.spring.repository.SougoulogRepository;
import boot.spring.service.SougoulogSearchService;

@Service
public class SougoulogSearchServiceImpl implements SougoulogSearchService {
	
	@Autowired
	SougoulogRepository sougoulogRepository;
	
	/**
	 * 返回所有
	 */
	@Override
	public Page<SougoulogIndex> searchDefault(SougoulogSearchRequest request, Pageable p) {
		 // 构建查询条件
	    NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
	    // 添加基本分词查询
	    queryBuilder.withQuery(QueryBuilders.matchAllQuery())
	    			.withPageable(p);
	    // 添加过滤条件
	    HashMap<String, String> filters = request.getQueryFilter();
	    String rankstart = filters.get("rankstart");
	    String rankend = filters.get("rankend");
	    if (rankstart!=null && rankend==null){
	    	queryBuilder.withFilter(QueryBuilders.rangeQuery("rank").from(rankstart).includeLower(true));
	    } else if (rankstart==null && rankend!=null) {
	    	queryBuilder.withFilter(QueryBuilders.rangeQuery("rank").to(rankend).includeUpper(true));
	    } else if (rankstart!=null && rankend!=null){
	    	queryBuilder.withFilter(QueryBuilders.rangeQuery("rank")
	    			.from(rankstart).to(rankend).includeLower(true).includeUpper(true));
	    }
	    String userids = filters.get("userids");
	    if (userids != null) {
	    	String[] ids = userids.split(",");
	    	queryBuilder.withFilter(QueryBuilders.termsQuery("userid", ids));
	    }
	    
	    // 添加分面条件
	    String dateField = request.getQuery().getFacetDateFiled();
	    String step = request.getQuery().getStep(); 
	    if (step != null && dateField!=null){
		    queryBuilder.addAggregation(AggregationBuilders.dateHistogram("dateFacet")
							    	   .field(dateField)
							    	   .interval(Integer.valueOf(step)));
	    }
		Page<SougoulogIndex> page = sougoulogRepository.search(queryBuilder.build());
		return page;
	}
	
	/**
	 * 按条件多字段搜索文本字段
	 */
	@Override
	public Page<SougoulogIndex> searchResult(SougoulogSearchRequest request, Pageable p) {
		 // 构建查询条件
	    NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
	    // 添加多字段查询
	    String fields = request.getQuery().getSearch_field();
		String[] field;
		if (fields == null || "".equals(fields)){
			field = new String[]{"userid","keywords","url"};
		} else {
			field = fields.split(",");
		}
		String operator = request.getQuery().getOperator();
		if ("AND".equals(operator)||"and".equals(operator)){
			queryBuilder.withQuery(QueryBuilders.multiMatchQuery(request.getQuery().getKeyWords(), field).operator(Operator.AND));
		} else {
			queryBuilder.withQuery(QueryBuilders.multiMatchQuery(request.getQuery().getKeyWords(), field).operator(Operator.OR));
		}
	    // 添加过滤条件
	    HashMap<String, String> filters = request.getQueryFilter();
	    String rankstart = filters.get("rankstart");
	    String rankend = filters.get("rankend");
	    if (rankstart!=null && rankend==null){
	    	queryBuilder.withFilter(QueryBuilders.rangeQuery("rank").from(rankstart).includeLower(true));
	    } else if (rankstart==null && rankend!=null) {
	    	queryBuilder.withFilter(QueryBuilders.rangeQuery("rank").to(rankend).includeUpper(true));
	    } else if (rankstart!=null && rankend!=null){
	    	queryBuilder.withFilter(QueryBuilders.rangeQuery("rank")
	    			.from(rankstart).to(rankend).includeLower(true).includeUpper(true));
	    }
	    String userids = filters.get("userids");
	    if (userids != null) {
	    	String[] ids = userids.split(",");
	    	queryBuilder.withFilter(QueryBuilders.termsQuery("userid", ids));
	    }
	    
	    // 添加分页
	    queryBuilder.withPageable(p);
	    // 添加分面条件
	    String dateField = request.getQuery().getFacetDateFiled();
	    String step = request.getQuery().getStep(); 
	    if (step != null && dateField!=null){
	    	queryBuilder.addAggregation(AggregationBuilders.dateHistogram("dateFacet")
			    	   .field(dateField)
			    	   .interval(Integer.valueOf(step)));
	    }
	    
	    // 搜索，获取结果
	    Page<SougoulogIndex> result = sougoulogRepository.search(queryBuilder.build());
		return result;
	}

}
