package boot.spring.controller;


import java.util.HashMap;

import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import boot.spring.elasticindex.ActorIndex;
import boot.spring.elasticindex.ShopIndex;
import boot.spring.elasticindex.SougoulogIndex;
import boot.spring.pagemodel.QueryCommand;
import boot.spring.pagemodel.ResultResponse;
import boot.spring.pagemodel.SougoulogSearchRequest;
import boot.spring.repository.ActorRepository;
import boot.spring.repository.ShopRepository;
import boot.spring.repository.SougoulogRepository;
import boot.spring.service.SougoulogSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "搜索接口")
@Controller
public class SearchController {
	
	@Autowired
	ActorRepository actorRepository;
	
	@Autowired
	SougoulogRepository sougoulogRepository;
	
	@Autowired
	SougoulogSearchService sougoulogSearchService;
	
	@Autowired
	ShopRepository shopRepository;
	
	@ApiOperation("多字段查询")
	@RequestMapping(value="/multi_match",method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResultResponse> multi_match(@RequestBody SougoulogSearchRequest request) {
		try {
			Page<SougoulogIndex> page;
			// 如果关键词为空，则返回所有
			String content = request.getQuery().getKeyWords();
			Integer rows = request.getQuery().getRows();
			if (rows == null || rows == 0) {
				rows = 10;
			}
			Integer start = request.getQuery().getStart();
			int pagenum = start / rows;
			// 第几页，页面大小
			Pageable pageable = PageRequest.of(pagenum, rows);
			if (content == null || "".equals(content)) {
				page = sougoulogSearchService.searchDefault(request, pageable);
			} else {
				page = sougoulogSearchService.searchResult(request, pageable);
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("result", page);
			ResultResponse rep = new ResultResponse(HttpStatus.OK, 200, "查询成功", "请求成功", map);
			return new ResponseEntity<ResultResponse>(rep, HttpStatus.OK); 
		} catch (HttpMessageNotReadableException hex) {
			hex.printStackTrace();
			ResultResponse rep = new ResultResponse(HttpStatus.BAD_REQUEST, 400, "请求格式错误","请求失败", null);
			return new ResponseEntity<ResultResponse>(rep, HttpStatus.BAD_REQUEST);
		}  catch (Exception e) {
			e.printStackTrace();
			ResultResponse rep = new ResultResponse(HttpStatus.BAD_REQUEST, 500, "后台错误", "请求失败", null);
			return new ResponseEntity<ResultResponse>(rep, HttpStatus.BAD_REQUEST);
        }
	}	

	@ApiOperation("query_string全字段查找")
	@RequestMapping(value="/query_string",method = RequestMethod.POST)
	@ResponseBody
	public Page<SougoulogIndex> query_string(@RequestBody SougoulogSearchRequest request){
		 // 构建查询条件
	    NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
	    // 添加搜索条件
	    queryBuilder.withQuery(QueryBuilders.queryStringQuery(request.getQuery().getKeyWords()));
	    // 搜索，获取结果
	    Page<SougoulogIndex> result = sougoulogRepository.search(queryBuilder.build());
		return result;
	}
	
	@ApiOperation("普通查询,先分词再精准搜索")
	@RequestMapping(value="/matchSearch",method = RequestMethod.GET)
	@ResponseBody
	public Page<SougoulogIndex> matchSearch(){
		 // 构建查询条件
	    NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
	    // 添加基本分词查询
	    queryBuilder.withQuery(QueryBuilders.matchQuery("keywords", "国际国产手机"));
	    // 搜索，获取结果
	    Page<SougoulogIndex> result = sougoulogRepository.search(queryBuilder.build());
	    // 总条数
//	    long total = result.getTotalElements();
		return result;
	}
	
	@ApiOperation("精准查询,不分词直接搜索")
	@RequestMapping(value="/termSearch",method = RequestMethod.POST)
	@ResponseBody
	public Page<SougoulogIndex> termSearch(@RequestBody SougoulogSearchRequest request){
		 // 构建查询条件
	    NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
	    String field = request.getQueryFilter().get("field");
	    String content = request.getQueryFilter().get("content");
	    // 添加精准搜索条件
	    queryBuilder.withQuery(QueryBuilders.termQuery(field, content));
	    // 搜索，获取结果
	    Page<SougoulogIndex> result = sougoulogRepository.search(queryBuilder.build());
		return result;
	}	
	
	@ApiOperation("模糊查询,搜索字符串接近的词")
	@RequestMapping(value="/fuzzyQuery",method = RequestMethod.GET)
	@ResponseBody
	public Page<SougoulogIndex> fuzzyQuery(){
		 // 构建查询条件
	    NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
	    // 添加模糊搜索条件
	    queryBuilder.withQuery(QueryBuilders.fuzzyQuery("userid", "007177259245828"));
	    // 搜索，获取结果
	    Page<SougoulogIndex> result = sougoulogRepository.search(queryBuilder.build());
		return result;
	}

	@ApiOperation("布尔查询,使用布尔运算组合多个查询条件")
	@RequestMapping(value="/boolQuery",method = RequestMethod.GET)
	@ResponseBody
	public Page<SougoulogIndex> boolQuery(){
		 // 构建查询条件
	    NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
	    // 添加布尔搜索条件
	    queryBuilder.withQuery(QueryBuilders.boolQuery()
	    		.must(QueryBuilders.matchQuery("visittime","00:00:11"))// 必须有
                .should(QueryBuilders.termQuery("keywords","世界"))// 可以有
                .mustNot(QueryBuilders.termQuery("userid","336233819810687")));// 不能有
	    // 搜索，获取结果
	    Page<SougoulogIndex> result = sougoulogRepository.search(queryBuilder.build());
		return result;
	}	
	
	@ApiOperation("范围查询")
	@RequestMapping(value="/rangeQuery",method = RequestMethod.GET)
	@ResponseBody
	public Page<SougoulogIndex> rangeQuery(){
		 // 构建查询条件
	    NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
	    // 添加范围搜索条件
	    queryBuilder.withQuery(QueryBuilders.rangeQuery("visittime").from("00:00:11").to("00:01:11"));
	    // 搜索，获取结果
	    Page<SougoulogIndex> result = sougoulogRepository.search(queryBuilder.build());
		return result;
	}		
	
	@ApiOperation("前缀查询")
	@RequestMapping(value="/prefixQuery",method = RequestMethod.GET)
	@ResponseBody
	public Page<SougoulogIndex> prefixQuery(){
		Pageable pageable = PageRequest.of(0, 1000);
		 // 构建查询条件
	    NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
	    // 添加前缀查询条件
	    queryBuilder.withQuery(QueryBuilders.prefixQuery("visittime", "00:01"))
	    			.withSort(SortBuilders.fieldSort("visittime").order(SortOrder.DESC))//排序
	    			.withPageable(pageable);//分页
	    // 搜索，获取结果
	    Page<SougoulogIndex> result = sougoulogRepository.search(queryBuilder.build());
		return result;
	}	
	
	@ApiOperation("通配符查询")
	@RequestMapping(value="/wildcardQuery",method = RequestMethod.GET)
	@ResponseBody
	public Page<ActorIndex> wildcardQuery(){
		 // 构建查询条件
	    NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
	    // 添加前缀查询条件
	    queryBuilder.withQuery(QueryBuilders.wildcardQuery("firstName", "B*"));
	    			
	    // 搜索，获取结果
	    Page<ActorIndex> result = actorRepository.search(queryBuilder.build());
		return result;
	}
	
	@ApiOperation("经纬度搜索")
	@RequestMapping(value="/locationQuery",method = RequestMethod.POST)
	@ResponseBody
	public Page<ShopIndex> locationQuery(@RequestBody QueryCommand q){
		 // 构建查询条件
		GeoDistanceQueryBuilder distanceQueryBuilder = new GeoDistanceQueryBuilder("location");
	     // 以某点为中心，搜索指定范围
		distanceQueryBuilder.point(39.96820,116.4107);
		 // 定义查询单位：公里
		distanceQueryBuilder.distance(q.getD(), DistanceUnit.KILOMETERS);
		NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
		queryBuilder.withFilter(distanceQueryBuilder);
		Page<ShopIndex> result = shopRepository.search(queryBuilder.build());
		return result;
	}	
}
