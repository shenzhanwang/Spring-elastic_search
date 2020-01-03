package boot.spring.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import boot.spring.elastic.service.AggsService;
import boot.spring.elastic.service.SearchService;
import boot.spring.pagemodel.DataGrid;
import boot.spring.pagemodel.DataTable;
import boot.spring.pagemodel.ElasticSearchRequest;
import boot.spring.pagemodel.FilterCommand;
import boot.spring.pagemodel.GeoDistance;
import boot.spring.pagemodel.JoinParams;
import boot.spring.pagemodel.MSG;
import boot.spring.pagemodel.QueryCommand;
import boot.spring.pagemodel.RangeQuery;
import boot.spring.pagemodel.ResultData;
import boot.spring.po.City;
import boot.spring.po.Country;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "搜索接口")
@Controller
public class SearchController {

	@Autowired
	SearchService searchService;

	
	@RequestMapping(value = "/sougoulog", method = RequestMethod.GET)
	public String sougoulog() {
		return "sougoulog";
	}
	
	@RequestMapping(value = "/distance", method = RequestMethod.GET)
	public String distance() {
		return "distance";
	}	

	@RequestMapping(value = "/city", method = RequestMethod.GET)
	public String city() {
		return "city";
	}
	
	@RequestMapping(value = "/haschild", method = RequestMethod.GET)
	public String haschild() {
		return "haschild";
	}
	
	@RequestMapping(value = "/hasparent", method = RequestMethod.GET)
	public String hasparent() {
		return "hasparent";
	}
	
    @ApiOperation("获取一个日志数据")
	@RequestMapping(value = "/sougoulog/{id}", method = RequestMethod.GET)
	@ResponseBody
    public ResultData sougoulog(@PathVariable String id) throws Exception{
    	SearchResponse rsp = searchService.termSearch("sougoulog", "id", id);
    	SearchHit[] searchHits = rsp.getHits().getHits();
    	List<Object> data = new ArrayList<>();
    	for (SearchHit hit : searchHits) {
    		Map<String, Object> map = hit.getSourceAsMap();
    		data.add(map);
    	}
    	ResultData rd = new ResultData();
    	rd.setData(data);
    	return rd;
	}	
    
    @ApiOperation("获取日志数据的总数")
	@RequestMapping(value = "/sougoulognumber", method = RequestMethod.GET)
	@ResponseBody
    public ResultData sougoulognumber() throws Exception {
    	SearchResponse rsp = searchService.matchAllSearch("sougoulog");
    	Long total = rsp.getHits().getTotalHits();
    	ResultData rd = new ResultData();
    	rd.setData(total);
    	return rd;
	}	    
	
	@ApiOperation("分页查询搜狗日志")
	@RequestMapping(value = "/sougoulog", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Object> listsougoulog(@RequestParam(value="current") int current, @RequestParam(value="rowCount") int rowCount
			,@RequestParam(value="searchPhrase") String searchPhrase,@RequestParam(value="startdate",required=false) String startdate
			,@RequestParam(value="enddate",required=false) String enddate) {
		DataGrid<Object> grid = new DataGrid<Object>();
		List<Object> data = new ArrayList<>();
		ElasticSearchRequest request = new ElasticSearchRequest();
		QueryCommand query = new QueryCommand();
		query.setIndexname("sougoulog");
		if (StringUtils.isBlank(searchPhrase)) {
			query.setKeyWords("*");
		} else {
			query.setKeyWords(searchPhrase);
		}
		query.setRows(rowCount);
		query.setStart((current-1)*rowCount);
		query.setSort("id");
		request.setQuery(query);
		if (StringUtils.isNotBlank(startdate) || StringUtils.isNotBlank(enddate)) {
			FilterCommand filter = new FilterCommand();
			filter.setField("visittime");
			filter.setStartdate(startdate);
			filter.setEnddate(enddate);
			request.setFilter(filter);
		}
		SearchResponse searchResponse = searchService.query_string(request);
		SearchHits hits = searchResponse.getHits();
		SearchHit[] searchHits = hits.getHits();
		for (SearchHit hit : searchHits) {
			Map<String, Object> highlights = new HashMap<String, Object>();
			Map<String, Object> map = hit.getSourceAsMap();
			// 获取高亮结果
			Map<String, HighlightField> highlightFields = hit.getHighlightFields();
			for (Map.Entry<String, HighlightField> entry : highlightFields.entrySet()) {
				String mapKey = entry.getKey();
				HighlightField mapValue = entry.getValue();
				Text[] fragments = mapValue.fragments();
				String fragmentString = fragments[0].string();
				highlights.put(mapKey, fragmentString);
			}
			map.put("highlight", highlights);
			data.add(map);
		}
		grid.setCurrent(current);
		grid.setRowCount(rowCount);
		grid.setRows(data);
		grid.setTotal(hits.getTotalHits());
		return grid;
	}	
	

	@ApiOperation("query_string全字段查找-普通分页版")
	@RequestMapping(value = "/query_string", method = RequestMethod.POST)
	@ResponseBody
	public ResultData query_string(@RequestBody ElasticSearchRequest request) {
		// 搜索结果
		List<Object> data = new ArrayList<Object>();
		SearchResponse searchResponse = searchService.query_string(request);
		SearchHits hits = searchResponse.getHits();
		SearchHit[] searchHits = hits.getHits();
		for (SearchHit hit : searchHits) {
			Map<String, Object> highlights = new HashMap<String, Object>();
			Map<String, Object> map = hit.getSourceAsMap();
			// 获取高亮结果
			Map<String, HighlightField> highlightFields = hit.getHighlightFields();
			for (Map.Entry<String, HighlightField> entry : highlightFields.entrySet()) {
				String mapKey = entry.getKey();
				HighlightField mapValue = entry.getValue();
				Text[] fragments = mapValue.fragments();
				String fragmentString = fragments[0].string();
				highlights.put(mapKey, fragmentString);
			}
			map.put("highlight", highlights);
			data.add(map);
		}
		ResultData resultData = new ResultData();
		resultData.setQtime(new Date());
		resultData.setData(data);
		resultData.setNumberFound(hits.getTotalHits());
		resultData.setStart(request.getQuery().getStart());
		return resultData;
	}
	
	@ApiOperation("query_string全字段查找-滚动分页版")
	@RequestMapping(value = "/query_string/scroll", method = RequestMethod.POST)
	@ResponseBody
	public ResultData scrollquery_string(@RequestBody ElasticSearchRequest request) {
		// 搜索结果
		List<Object> data = new ArrayList<Object>();
		SearchResponse searchResponse = searchService.scrollquerystring(request);
		SearchHits hits = searchResponse.getHits();
		SearchHit[] searchHits = hits.getHits();
		String scrollid = searchResponse.getScrollId();
		for (SearchHit hit : searchHits) {
			Map<String, Object> highlights = new HashMap<String, Object>();
			Map<String, Object> map = hit.getSourceAsMap();
			// 获取高亮结果
			Map<String, HighlightField> highlightFields = hit.getHighlightFields();
			for (Map.Entry<String, HighlightField> entry : highlightFields.entrySet()) {
				String mapKey = entry.getKey();
				HighlightField mapValue = entry.getValue();
				Text[] fragments = mapValue.fragments();
				String fragmentString = fragments[0].string();
				highlights.put(mapKey, fragmentString);
			}
			map.put("highlight", highlights);
			data.add(map);
		}
		ResultData resultData = new ResultData();
		resultData.setQtime(new Date());
		resultData.setData(data);
		resultData.setNumberFound(hits.getTotalHits());
		resultData.setStart(request.getQuery().getStart());
		resultData.setScrollid(scrollid);
		return resultData;
	}	

	@ApiOperation("经纬度搜索")
	@RequestMapping(value = "/geosearch", method = RequestMethod.POST)
	@ResponseBody
	public DataTable<Object> geosearch(@RequestBody GeoDistance geo) {
		// 搜索结果
		List<Object> data = new ArrayList<Object>();
		SearchResponse searchResponse = searchService.geoDistanceSearch("shop", geo, geo.getPagenum(), geo.getPagesize());
		SearchHits hits = searchResponse.getHits();
		SearchHit[] searchHits = hits.getHits();
		for (SearchHit hit : searchHits) {
			Map<String, Object> map = hit.getSourceAsMap();
			data.add(map);
		}
		DataTable<Object> grid = new DataTable<Object>();
		grid.setDraw(UUID.randomUUID().toString());
		grid.setRecordsFiltered(hits.getTotalHits());
		grid.setRecordsTotal(hits.getTotalHits());
		grid.setData(data);
		grid.setLength(geo.getPagesize());
		return grid;
	}
	
	@ApiOperation("分页查询城市索引-嵌套父文档")
	@RequestMapping(value = "/city", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Object> listCitys(@RequestParam(value="current") int current, @RequestParam(value="rowCount") int rowCount
			,@RequestParam(value="searchPhrase") String searchPhrase) {
		DataGrid<Object> grid = new DataGrid<Object>();
		List<Object> data = new ArrayList<>();
		ElasticSearchRequest request = new ElasticSearchRequest();
		QueryCommand query = new QueryCommand();
		query.setIndexname("city");
		if (StringUtils.isBlank(searchPhrase)) {
			query.setKeyWords("*");
		} else {
			query.setKeyWords(searchPhrase);
		}
		query.setRows(rowCount);
		query.setStart((current-1)*rowCount);
		request.setQuery(query);
		SearchResponse searchResponse;
		if (StringUtils.isBlank(searchPhrase)) {
			searchResponse = searchService.query_string(request);
		} else {
			searchResponse = searchService.matchNestedObjectSearch("country", "city", "country.countryname", searchPhrase, current, rowCount);
		}
		SearchHits hits = searchResponse.getHits();
		SearchHit[] searchHits = hits.getHits();
		for (SearchHit hit : searchHits) {
			Map<String, Object> highlights = new HashMap<String, Object>();
			Map<String, Object> map = hit.getSourceAsMap();
			// 获取高亮结果
			Map<String, HighlightField> highlightFields = hit.getHighlightFields();
			for (Map.Entry<String, HighlightField> entry : highlightFields.entrySet()) {
				String mapKey = entry.getKey();
				HighlightField mapValue = entry.getValue();
				Text[] fragments = mapValue.fragments();
				String fragmentString = fragments[0].string();
				highlights.put(mapKey, fragmentString);
			}
			map.put("highlight", highlights);
			data.add(map);
		}
		grid.setCurrent(current);
		grid.setRowCount(rowCount);
		grid.setRows(data);
		grid.setTotal(hits.getTotalHits());
		return grid;
	}	

	@ApiOperation("join搜索-用城市搜国家")
	@RequestMapping(value = "/haschild", method = RequestMethod.POST)
	@ResponseBody
	public DataTable<Object> haschild(@RequestBody JoinParams param) {
		// 搜索结果
		List<Object> data = new ArrayList<Object>();
		SearchResponse searchResponse = searchService.hasChildSearch("city", "cityjoincountry", "cityname", param.getName(), param.getPagenum(), param.getPagesize());
		SearchHits hits = searchResponse.getHits();
		SearchHit[] searchHits = hits.getHits();
		for (SearchHit hit : searchHits) {
			Map<String, Object> map = hit.getSourceAsMap();
			data.add(map);
		}
		DataTable<Object> grid = new DataTable<Object>();
		grid.setDraw(UUID.randomUUID().toString());
		grid.setRecordsFiltered(hits.getTotalHits());
		grid.setLength(param.getPagesize());
		grid.setRecordsTotal(hits.getTotalHits());
		grid.setData(data);
		return grid;
	}
	
	@ApiOperation("join搜索-用国家搜城市")
	@RequestMapping(value = "/hasparent", method = RequestMethod.POST)
	@ResponseBody
	public DataTable<Object> hasparent(@RequestBody JoinParams param) {
		// 搜索结果
		List<Object> data = new ArrayList<Object>();
		SearchResponse searchResponse = searchService.hasParentSearch("country", "cityjoincountry", "country", param.getName(), param.getPagenum(), param.getPagesize());
		SearchHits hits = searchResponse.getHits();
		SearchHit[] searchHits = hits.getHits();
		for (SearchHit hit : searchHits) {
			Map<String, Object> map = hit.getSourceAsMap();
			data.add(map);
		}
		DataTable<Object> grid = new DataTable<Object>();
		grid.setDraw(UUID.randomUUID().toString());
		grid.setRecordsFiltered(hits.getTotalHits());
		grid.setRecordsTotal(hits.getTotalHits());
		grid.setLength(param.getPagesize());
		grid.setData(data);
		return grid;
	}	
}
