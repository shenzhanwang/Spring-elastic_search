package boot.spring.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import boot.spring.pagemodel.ResultResponse;
import boot.spring.elastic.search.service.SearchService;
import boot.spring.pagemodel.AYRequest;
import boot.spring.pagemodel.AggsResultResponse;
import boot.spring.pagemodel.CityGrid;
import boot.spring.pagemodel.ElasticSearchRequest;
import boot.spring.pagemodel.ResultData;
import boot.spring.pagemodel.ResultField;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "搜索接口")
@Controller
public class SearchController {
	
	@Autowired
	SearchService searchService;
	
	@Autowired
	RestHighLevelClient client;
	
	
	@ApiOperation("query_string全字段查找")
	@RequestMapping(value="/query_string",method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResultResponse> query_string(@RequestBody ElasticSearchRequest request) {
		try {
			// 搜索结果
			List<Object> data = new ArrayList<Object>();
			SearchResponse searchResponse = searchService.multiSearch(request);
			SearchHits hits = searchResponse.getHits();
			SearchHit[] searchHits = hits.getHits();
			for (SearchHit hit : searchHits) {
				// 把每条记录包装成ResultField
				List<ResultField> list = new ArrayList<ResultField>();
				Map<String, Object> map = hit.getSourceAsMap();
				for(String key : map.keySet())
			    {
					if (map.get(key) == null) {
						ResultField field = new ResultField(key, null, key);
						list.add(field);
					} else {
						ResultField field = new ResultField(key, map.get(key).toString(), key);
						list.add(field);
					}
			    }
				data.add(list);
			}
			ResultData resultData = new ResultData();
			resultData.setQtime(new Date());
			resultData.setData(data);
			resultData.setNumberFound(hits.getTotalHits());
			resultData.setStart(request.getQuery().getStart());
			ResultResponse rep = new ResultResponse(HttpStatus.OK, 200, "查询成功", "请求成功", resultData);
			return new ResponseEntity<ResultResponse>(rep, HttpStatus.OK);
		} catch (HttpMessageNotReadableException hex) {
			hex.printStackTrace();
			ResultResponse rep = new ResultResponse(HttpStatus.BAD_REQUEST, 400, "请求格式错误", "请求失败", null);
			return new ResponseEntity<ResultResponse>(rep, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			ResultResponse rep = new ResultResponse(HttpStatus.BAD_REQUEST, 500, "后台错误", "请求失败", null);
			return new ResponseEntity<ResultResponse>(rep, HttpStatus.BAD_REQUEST);
		}
	}

	@ApiOperation("日期直方图聚集")
	@RequestMapping(value="/dateHistogram",method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<AggsResultResponse> dateHistogram(@RequestBody ElasticSearchRequest request) {
		try {
			HashMap<String, Long> resultMap = searchService.dateHistogram(request);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("aggs", resultMap);
			AggsResultResponse rep = new AggsResultResponse(HttpStatus.OK, 200, "查询成功", "请求成功", map);
			return new ResponseEntity<AggsResultResponse>(rep, HttpStatus.OK);
		} catch (HttpMessageNotReadableException hex) {
			hex.printStackTrace();
			AggsResultResponse rep = new AggsResultResponse(HttpStatus.BAD_REQUEST, 400, "请求格式错误", "请求失败", null);
			return new ResponseEntity<AggsResultResponse>(rep, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			AggsResultResponse rep = new AggsResultResponse(HttpStatus.BAD_REQUEST, 500, "后台错误", "请求失败", null);
			return new ResponseEntity<AggsResultResponse>(rep, HttpStatus.BAD_REQUEST);
		}
	}	

	
	@ApiOperation("布尔查询")
	@RequestMapping(value="/boolQuery",method = RequestMethod.GET)
	@ResponseBody
	public SearchHits boolQuery() {
		SearchRequest searchRequest = new SearchRequest("sougoulog"); 
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
		searchSourceBuilder.query(QueryBuilders.boolQuery()
				.must(QueryBuilders.queryStringQuery("美国"))
				.must(QueryBuilders.rangeQuery("visittime").from("00:00:10").to("00:55:55"))
				.must(QueryBuilders.termQuery("userid","09215584418987899"))
				.must(QueryBuilders.fuzzyQuery("userid","092155844189878")));
		searchRequest.source(searchSourceBuilder);
		searchSourceBuilder.from(0); 
		searchSourceBuilder.size(10); 
		SearchHits hits = null;
		try {
			SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
			hits = searchResponse.getHits();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hits;
	}	
	
	
	
}
