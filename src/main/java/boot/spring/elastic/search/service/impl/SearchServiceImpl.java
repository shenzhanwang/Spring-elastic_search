package boot.spring.elastic.search.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import boot.spring.elastic.search.querytypes.FuzzyQuery;
import boot.spring.elastic.search.querytypes.MatchQuery;
import boot.spring.elastic.search.querytypes.RangeQuery;
import boot.spring.elastic.search.querytypes.TermQuery;
import boot.spring.elastic.search.service.SearchService;
import boot.spring.pagemodel.AYRequest;
import boot.spring.pagemodel.ElasticSearchRequest;
import boot.spring.pagemodel.FilterCommand;

@Service
public class SearchServiceImpl implements SearchService {
	
	@Autowired
	RestHighLevelClient client;
	
	@Override
	public SearchResponse multiSearch(ElasticSearchRequest request) {
		SearchRequest searchRequest = new SearchRequest("fkdb");
		// 如果关键词为空，则返回所有
		String content = request.getQuery().getKeyWords();
		Integer rows = request.getQuery().getRows();
		if (rows == null || rows == 0) {
			rows = 10;
		}
		Integer start = request.getQuery().getStart();
		if (content == null || "".equals(content)) {
			// 查询所有
			content = "*";
		}
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		// 提取搜索内容
		BoolQueryBuilder builder = QueryBuilders.boolQuery().must(QueryBuilders.queryStringQuery(content));
		// 提取过滤条件
		FilterCommand filter = request.getFilter();
		List<RangeQuery> ranges = filter.getRanges();
		if (ranges != null && ranges.size() > 0) {
			for (RangeQuery range : ranges) {
				builder.must(QueryBuilders.rangeQuery(range.getField()).from(range.getFrom()).to(range.getTo()));
			}
		}
		List<TermQuery> terms = filter.getTerms();
		if (terms != null && terms.size() > 0) {
			for (TermQuery term : terms) {
				builder.must(QueryBuilders.termQuery(term.getField(), term.getValue()));
			}
		}

		List<FuzzyQuery> fuzzys = filter.getFuzzys();
		if (fuzzys != null && fuzzys.size() > 0) {
			for (FuzzyQuery fuzzy : fuzzys) {
				builder.must(QueryBuilders.fuzzyQuery(fuzzy.getField(), fuzzy.getValue()));
			}
		}
		
		List<MatchQuery> matches = filter.getMatches();
		if (matches != null && matches.size() > 0) {
			for (MatchQuery match : matches) {
				builder.must(QueryBuilders.matchQuery(match.getField(), match.getValue()));
			}
		}		
	    searchSourceBuilder.query(builder);
		searchRequest.source(searchSourceBuilder);
		searchSourceBuilder.from(start);
		searchSourceBuilder.size(rows);
		SearchResponse searchResponse = null;
		try {
			searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return searchResponse;
	}

	@Override
	public HashMap<String, Long> dateHistogram(ElasticSearchRequest request) {
		SearchRequest searchRequest = new SearchRequest("sougoulog");
		// 如果关键词为空，则返回所有
		String content = request.getQuery().getKeyWords();
		Integer rows = request.getQuery().getRows();
		if (rows == null || rows == 0) {
			rows = 10;
		}
		Integer start = request.getQuery().getStart();
		if (content == null || "".equals(content)) {
			// 查询所有
			content = "*";
		}
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		// 提取搜索内容
		BoolQueryBuilder builder = QueryBuilders.boolQuery().must(QueryBuilders.queryStringQuery(content));
		// 提取过滤条件
		FilterCommand filter = request.getFilter();
		List<RangeQuery> ranges = filter.getRanges();
		if (ranges != null && ranges.size() > 0) {
			for (RangeQuery range : ranges) {
				builder.must(QueryBuilders.rangeQuery(range.getField()).from(range.getFrom()).to(range.getTo()));
			}
		}
		
		List<TermQuery> terms = filter.getTerms();
		if (terms != null && terms.size() > 0) {
			for (TermQuery term : terms) {
				builder.must(QueryBuilders.termQuery(term.getField(), term.getValue()));
			}
		}

		List<FuzzyQuery> fuzzys = filter.getFuzzys();
		if (fuzzys != null && fuzzys.size() > 0) {
			for (FuzzyQuery fuzzy : fuzzys) {
				builder.must(QueryBuilders.fuzzyQuery(fuzzy.getField(), fuzzy.getValue()));
			}
		}
		
		List<MatchQuery> matches = filter.getMatches();
		if (matches != null && matches.size() > 0) {
			for (MatchQuery match : matches) {
				builder.must(QueryBuilders.matchQuery(match.getField(), match.getValue()));
			}
		}
		
		// 聚集
	    String dateField = request.getQuery().getFacetDateFiled();
	    String step = request.getQuery().getStep(); 
	    if (step != null && dateField!=null){
	         DateHistogramAggregationBuilder dateHistogramAggregationBuilder = AggregationBuilders
	                 .dateHistogram("aggsName")
	                 .field(dateField) 
	                 .dateHistogramInterval(DateHistogramInterval.seconds(Integer.parseInt(step)))
	                 .minDocCount(0L);
	         searchSourceBuilder.query(builder).aggregation(dateHistogramAggregationBuilder);
	    } else {
	    	searchSourceBuilder.query(builder);
	    }
		searchRequest.source(searchSourceBuilder);
		searchSourceBuilder.from(start);
		searchSourceBuilder.size(rows);
		SearchResponse searchResponse = null;
		HashMap<String, Long> resultMap = new HashMap<String, Long>();
		try {
			searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
			Aggregations jsonAggs = searchResponse.getAggregations();
			Histogram dateHistogram =(Histogram) jsonAggs.get("aggsName");
			List<? extends Histogram.Bucket> bucketList = dateHistogram.getBuckets();
			for (Histogram.Bucket bucket : bucketList) {
			    resultMap.put(bucket.getKeyAsString(), bucket.getDocCount());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultMap;
	}
	
	@Override
	public SearchResponse termsAggs(String index, AYRequest request) {
		SearchRequest searchRequest = new SearchRequest(index);
		BoolQueryBuilder builder = QueryBuilders.boolQuery().must(QueryBuilders.matchAllQuery());
		String code = request.getCode();
		String fieldName = "";
		String lastCode ="";
		if (code!=null){
		   lastCode = code.substring(code.length()-2);
		}
		if (code == null ||"".equals(code)) {
			// 一级
			fieldName = "bjlbbh";
		} else if (code.length()==2){
			// 传入一级编号 
			fieldName = "bjlxbh"; //在类型上聚集
			builder.must(QueryBuilders.termQuery("bjlbbh", code));
		}else if ("00".equals(lastCode)) {
			// 传入二级类型编号
			fieldName = "bjxlbh";
			builder.must(QueryBuilders.termQuery("bjlxbh", code));
		} 
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		// terms聚集
		AggregationBuilder aggregationBuilder = AggregationBuilders.terms("aggsName")
				.field(fieldName).size(Integer.MAX_VALUE).order(BucketOrder.key(true));
		searchSourceBuilder.query(builder).aggregation(aggregationBuilder);
		searchRequest.source(searchSourceBuilder);
		SearchResponse searchResponse = null;
		try {
			searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return searchResponse;
	}

	@Override
	public SearchResponse termSearch(String index, String field, String term) {
		SearchRequest searchRequest = new SearchRequest(index);
		BoolQueryBuilder builder = QueryBuilders.boolQuery().must(QueryBuilders.termQuery(field, term));
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(builder);
		searchRequest.source(searchSourceBuilder);
		SearchResponse searchResponse = null;
		try {
			 searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return searchResponse;
	}
	
}
