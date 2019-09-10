package boot.spring.controller;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import boot.spring.elastic.index.IndexService;
import boot.spring.pagemodel.MSG;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "索引接口")
@Controller
public class IndexController {
	@Autowired
	IndexService indexService;
	
	/**
	 * 创建索引并设置字段类型
	 * @param indexname
	 * @param indextype
	 * @param jsonMap
	 * @return
	 * {"properties": {
        "age": {
          "type": "integer"
        },
        "born": {
          "type": "date",
          "format": "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
        },
        "desc": {
          "type": "text",
          "analyzer": "ik_smart"
        },
        "id": {
          "type": "integer"
        },
        "name": {
          "type": "keyword"
        }
      }
      }
	 */
	@ApiOperation("创建索引并设置字段类型")
	@RequestMapping(value="/indexMapping/{indexname}",method = RequestMethod.POST)
	@ResponseBody
	MSG createMapping(@PathVariable String indexname, @RequestBody Map<String, Object> mapping){
		indexService.createMapping(indexname, mapping);
		return new MSG("index success");
	}
	
	@ApiOperation("向索引添加或修改一个文档")
	@RequestMapping(value="/indexDoc/{indexname}/{indextype}",method = RequestMethod.POST)
	@ResponseBody
	MSG indexDoc(@PathVariable String indexname, @PathVariable String indextype, @RequestBody Map<String, Object> jsonMap){
		indexService.saveOrUpdateIndexDoc(indexname, indextype, jsonMap);
		return new MSG("index success");
	}
	
	@ApiOperation("向索引添加一组文档")
	@RequestMapping(value="/indexDocs/{indexname}/{indextype}",method = RequestMethod.POST)
	@ResponseBody
	MSG indexDocs(@PathVariable String indexname, @PathVariable String indextype, @RequestBody List<Map<String, Object>> jsonMap){
		indexService.indexDocs(indexname, indextype, jsonMap);
		return new MSG("index success");
	}	
	
	@ApiOperation("删除一个索引中的文档")
	@RequestMapping(value="/indexDocs/{indexname}/{indextype}/{id}",method = RequestMethod.DELETE)
	@ResponseBody
	MSG indexDocs(@PathVariable String indexname, @PathVariable String indextype, @PathVariable String id){
		int result = indexService.deleteDoc(indexname, indextype, id);
		if ( result < 0 ) {
			return new MSG("index delete failed");
		} else {
			return new MSG("index delete success");
		}
	}	


}
