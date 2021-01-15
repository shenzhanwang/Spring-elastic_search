# Spring-elastic_search

### 介绍
Spring boot整合elastic search 7.9.1实现全文检索。主要包含以下特性：

1. 全文检索的实现主要包括构建索引、高级搜索、聚集统计、数据建模四个模块；
2. 使用 **elasticsearch-rest-high-level-client** 来操作elasticsearch，构建索引时，根据实际情况考虑哪些字段需要分词，哪些不需要分词，这会影响搜索结果。使用IK分词器虽然能解决一些中文分词的问题，但是由于分词的粒度不够细，导致很多词语可能搜不到。例如ik分词器在构建索引“三国无双”时，会把“三国”“无双”存起来建索引，但是搜索“国无”时，搜不出来，因此，我们采用把文本拆分到最细粒度来进行分词，从而最大限度地搜索到相关结果。详情参考：[如何手动控制分词粒度提高搜索的准确性](https://gitee.com/shenzhanwang/Spring-elastic_search/wikis/%E5%A6%82%E4%BD%95%E6%89%8B%E5%8A%A8%E6%8E%A7%E5%88%B6%E5%88%86%E8%AF%8D%E7%B2%92%E5%BA%A6%E6%8F%90%E9%AB%98%E6%90%9C%E7%B4%A2%E7%9A%84%E5%87%86%E7%A1%AE%E6%80%A7?sort_id=1727039)
3. 高级搜索实现了以下几种：
    - 多字段搜索,指定多个字段进行搜索:query_string，支持高亮显示
    - 经纬度搜索:distanceQuery
    - 范围过滤,对搜索结果进一步按照范围进行条件过滤：rangeQuery
4. 搜索结果的展示提供了普通分页和滚动分页两种实现。普通分页只适合数据量较小的场景，在数据量非常大的情况下，start+size普通分页会把全部记录加载到内存中，这样做不但运行速度特别慢，而且容易让es出现内存不足而挂掉。滚动分页需要得到一个scrollid，以后每次使用scrollid去获取下一页的内容，缺点是不能跳页。
5. 聚集统计包含词条聚集、日期直方图聚集、范围聚集，并使用chart.js进行可视化
6. 数据建模部分实现了嵌套对象的使用，查询时无需join性能较好，但是在建索引时就要把关联数据join好嵌套进去。
7. swagger入口：http://localhost:8080/swagger-ui.html
8. 新增数据库数据导入es的工具，编程实现比logstash灵活。使用请切换分支到master。[db-import-elastic_search](https://gitee.com/shenzhanwang/Spring-elastic_search/tree/master/)
9. 通用的多条件查询结构模板是布尔查询，其中must部分放入带分词的搜索例如match、querystring等；filter部分放入过滤条件例如term、range等查询。因为filter部分利用缓存、跳过相关度打分提高性能：

```
// 通用查询结构模板
{
  "query": { 
    "bool": { 
      "must": [
        { "match": { "title":   "Search"        }},
        { "match": { "content": "Elasticsearch" }}
      ],
      "filter": [ 
        { "term":  { "status": "published" }},
        { "range": { "publish_date": { "gte": "2015-01-01" }}}
      ]
    }
  }
}
```

### 相关WIKI
#### kibana篇
- [CentOS上Kibana安装指南](https://gitee.com/shenzhanwang/Spring-elastic_search/wikis/CentOS%E4%B8%8AKibana%E5%AE%89%E8%A3%85%E6%8C%87%E5%8D%97?sort_id=1717428)
#### Logstash篇
- [安装Logstash并全量导入数据库数据](https://gitee.com/shenzhanwang/Spring-elastic_search/wikis/%E5%AE%89%E8%A3%85Logstash%E5%B9%B6%E5%85%A8%E9%87%8F%E5%AF%BC%E5%85%A5%E6%95%B0%E6%8D%AE%E5%BA%93%E6%95%B0%E6%8D%AE?sort_id=1717557)
- [使用Logstash增量更新数据](https://gitee.com/shenzhanwang/Spring-elastic_search/wikis/%E4%BD%BF%E7%94%A8Logstash%E5%A2%9E%E9%87%8F%E6%9B%B4%E6%96%B0%E6%95%B0%E6%8D%AE?sort_id=1717614)
- [Logstash如何生成联合主键](https://gitee.com/shenzhanwang/Spring-elastic_search/wikis/Logstash%E5%A6%82%E4%BD%95%E7%94%9F%E6%88%90%E8%81%94%E5%90%88%E4%B8%BB%E9%94%AE?sort_id=1717654)
- [logstash如何对敏感配置项加密](https://gitee.com/shenzhanwang/Spring-elastic_search/wikis/logstash%E5%A6%82%E4%BD%95%E5%AF%B9%E6%95%8F%E6%84%9F%E9%85%8D%E7%BD%AE%E9%A1%B9%E5%8A%A0%E5%AF%86?sort_id=1728432)
- [logstash如何支持多开](https://gitee.com/shenzhanwang/Spring-elastic_search/wikis/logstash%E5%A6%82%E4%BD%95%E6%94%AF%E6%8C%81%E5%A4%9A%E5%BC%80?sort_id=1728531)
- [如何将logstash自动更新服务配置为WINDOWS后台服务](https://gitee.com/shenzhanwang/Spring-elastic_search/wikis/%E5%A6%82%E4%BD%95%E5%B0%86logstash%E8%87%AA%E5%8A%A8%E6%9B%B4%E6%96%B0%E6%9C%8D%E5%8A%A1%E9%85%8D%E7%BD%AE%E4%B8%BAWINDOWS%E5%90%8E%E5%8F%B0%E6%9C%8D%E5%8A%A1?sort_id=1818080)
- [logstash如何导入join类型的父子关系](https://gitee.com/shenzhanwang/Spring-elastic_search/wikis/logstash%E5%A6%82%E4%BD%95%E5%AF%BC%E5%85%A5join%E7%B1%BB%E5%9E%8B%E7%9A%84%E7%88%B6%E5%AD%90%E5%85%B3%E7%B3%BB?sort_id=1861956)
#### elastic search篇
- [elastic search的REST服务（直接使用kibana运行）](https://gitee.com/shenzhanwang/Spring-elastic_search/wikis/elastic%20search%E7%9A%84REST%E6%9C%8D%E5%8A%A1%EF%BC%88%E7%9B%B4%E6%8E%A5%E4%BD%BF%E7%94%A8kibana%E8%BF%90%E8%A1%8C%EF%BC%89?sort_id=1725842)
- [如何手动控制分词粒度提高搜索的准确性](https://gitee.com/shenzhanwang/Spring-elastic_search/wikis/%E5%A6%82%E4%BD%95%E6%89%8B%E5%8A%A8%E6%8E%A7%E5%88%B6%E5%88%86%E8%AF%8D%E7%B2%92%E5%BA%A6%E6%8F%90%E9%AB%98%E6%90%9C%E7%B4%A2%E7%9A%84%E5%87%86%E7%A1%AE%E6%80%A7?sort_id=1727039)
- [如何防止跳词提高搜索的准确性](https://gitee.com/shenzhanwang/Spring-elastic_search/wikis/%E5%A6%82%E4%BD%95%E9%98%B2%E6%AD%A2%E8%B7%B3%E8%AF%8D%E6%8F%90%E9%AB%98%E6%90%9C%E7%B4%A2%E7%9A%84%E5%87%86%E7%A1%AE%E6%80%A7?sort_id=1733939)
- [elastic search如何自定义日期格式](https://gitee.com/shenzhanwang/Spring-elastic_search/wikis/elastic%20search%E5%A6%82%E4%BD%95%E8%87%AA%E5%AE%9A%E4%B9%89%E6%97%A5%E6%9C%9F%E6%A0%BC%E5%BC%8F?sort_id=1734772)
- [索引的doc value和field data的区别和联系](https://gitee.com/shenzhanwang/Spring-elastic_search/wikis/%E7%B4%A2%E5%BC%95%E7%9A%84doc%20value%E5%92%8Cfield%20data%E7%9A%84%E5%8C%BA%E5%88%AB%E5%92%8C%E8%81%94%E7%B3%BB?sort_id=1762216)
- [如何修改es的堆内存大小加快索引查询性能](https://gitee.com/shenzhanwang/Spring-elastic_search/wikis/%E5%A6%82%E4%BD%95%E4%BF%AE%E6%94%B9es%E7%9A%84%E5%A0%86%E5%86%85%E5%AD%98%E5%A4%A7%E5%B0%8F%E5%8A%A0%E5%BF%AB%E7%B4%A2%E5%BC%95%E6%9F%A5%E8%AF%A2%E6%80%A7%E8%83%BD?sort_id=1778490)
- [如何搭建elastic search集群](https://gitee.com/shenzhanwang/Spring-elastic_search/wikis/%E5%A6%82%E4%BD%95%E6%90%AD%E5%BB%BAelastic%20search%E9%9B%86%E7%BE%A4?sort_id=1789195)
- [elastic search如何处理多个索引之间的主外键关联
](https://gitee.com/shenzhanwang/Spring-elastic_search/wikis/elastic%20search%E5%A6%82%E4%BD%95%E5%A4%84%E7%90%86%E5%A4%9A%E4%B8%AA%E7%B4%A2%E5%BC%95%E4%B9%8B%E9%97%B4%E7%9A%84%E4%B8%BB%E5%A4%96%E9%94%AE%E5%85%B3%E8%81%94?sort_id=1789201)
![输入图片说明](https://images.gitee.com/uploads/images/2019/1205/084028_e7962b37_1110335.jpeg "微信图片_20191205083903.jpg")
### 效果图
![输入图片说明](https://images.gitee.com/uploads/images/2019/1226/103749_26e8f1e2_1110335.gif "s.gif")
![输入图片说明](https://images.gitee.com/uploads/images/2019/1227/084159_2df38df8_1110335.png "1577407262(1).png")
![输入图片说明](https://images.gitee.com/uploads/images/2019/1227/083952_faa81787_1110335.png "1577407124(1).png")
![输入图片说明](https://images.gitee.com/uploads/images/2019/1226/103916_d0f9bf4f_1110335.png "1577327499(1).png")
![输入图片说明](https://images.gitee.com/uploads/images/2019/1226/103932_6fe4f3c0_1110335.png "1577327518(1).png")
![输入图片说明](https://images.gitee.com/uploads/images/2019/1226/103950_7f751403_1110335.png "1577327531(1).png")
![输入图片说明](https://images.gitee.com/uploads/images/2019/1227/084034_1ebafbc9_1110335.png "1577407143(1).png")
![输入图片说明](https://images.gitee.com/uploads/images/2019/1226/104001_568e956d_1110335.png "1577327550(1).png")
![输入图片说明](https://images.gitee.com/uploads/images/2019/1226/104013_78a63d0c_1110335.png "1577327579(1).png")

### 附录：中央技术储备仓库（Central Technique Reserve Repository）

#### 基础篇:职业化，从做好OA系统开始
1. [Spring boot整合Mybatis实现增删改查（支持多数据源）](https://gitee.com/shenzhanwang/SSM)![输入图片说明](https://img.shields.io/badge/-%E7%B2%BE%E5%93%81-orange.svg "在这里输入图片标题")
2. [Struts2,Hibernate,Spring三大框架的整合实现增删改查](https://gitee.com/shenzhanwang/S2SH)
3. [Spring,SpringMVC和Hibernate的整合实现增删改查](https://gitee.com/shenzhanwang/SSH)
4. [Spring boot整合activiti工作流引擎实现OA开发](https://gitee.com/shenzhanwang/Spring-activiti)![输入图片说明](https://img.shields.io/badge/-%E7%B2%BE%E5%93%81-orange.svg "在这里输入图片标题")
5. [Spring发布与调用REST风格的WebService](https://gitee.com/shenzhanwang/Spring-REST)
6. [Spring boot整合Axis调用SOAP风格的web服务](https://gitee.com/shenzhanwang/Spring-axis)
7. [Spring boot整合Apache Shiro实现RBAC权限控制](https://gitee.com/shenzhanwang/Spring-shiro)
8. [使用Spring security实现RBAC权限控制](https://gitee.com/shenzhanwang/spring-security-demo)
9. [Spring整合Jasig CAS框架实现单点登录](https://gitee.com/shenzhanwang/Spring-cas-sso)

#### 中级篇：中间件的各种姿势
10. [Spring boot整合mongoDB文档数据库实现增删改查](https://gitee.com/shenzhanwang/Spring-mongoDB)
11. [Spring连接Redis实现缓存](https://gitee.com/shenzhanwang/Spring-redis)
12. [Spring连接图存数据库Neo4j实现增删改查](https://gitee.com/shenzhanwang/Spring-neo4j)
13. Spring boot整合列存数据库hbase实现增删改查
14. [Spring平台整合消息队列ActiveMQ实现发布订阅、生产者消费者模型（JMS）](https://gitee.com/shenzhanwang/Spring-activeMQ)
15. [Spring boot整合消息队列RabbitMQ实现四种消息模式（AMQP）](https://gitee.com/shenzhanwang/Spring-rabbitMQ)
16. Spring boot整合kafka 2.1.0实现大数据消息管道
17. [Spring boot整合websocket实现即时通讯](https://gitee.com/shenzhanwang/Spring-websocket)![输入图片说明](https://img.shields.io/badge/-%E7%B2%BE%E5%93%81-orange.svg "在这里输入图片标题")
18. [Spring security整合oauth2实现token认证](https://gitee.com/shenzhanwang/Spring-security-oauth2)
19. [Spring MVC整合FastDFS客户端实现文件上传](https://gitee.com/shenzhanwang/Spring-fastdfs)
20. 23种设计模式，源码、注释、使用场景 
21. [使用ETL工具Kettle的实例](https://gitee.com/shenzhanwang/Kettle-demo)
22. Git指南和分支管理策略 
23. 使用数据仓库进行OLAP数据分析（Mysql+Kettle+Zeppelin）
#### 高级篇：架构之美
24. [zookeeper原理、架构、使用场景和可视化](https://gitee.com/shenzhanwang/zookeeper-practice)
25. Spring boot整合Apache dubbo v2.7.5实现分布式服务治理（SOA架构） ![输入图片说明](https://img.shields.io/badge/-%E7%B2%BE%E5%93%81-orange.svg "在这里输入图片标题") 
>  包含组件Spring boot v2.2.2+Dubbo v2.7.5+Nacos v1.1.1
<a href="https://images.gitee.com/uploads/images/2020/0114/084731_fd0b7a82_1110335.gif" target="_blank">效果图</a>
26. 使用Spring Cloud Alibaba v2.1.0实现微服务架构（MSA架构）![输入图片说明](https://img.shields.io/badge/-%E6%8B%9B%E7%89%8C-yellow.svg)   
>  包含组件Nacos+Feign+Gateway+Ribbon+Sentinel+Zipkin
<a href="https://images.gitee.com/uploads/images/2020/0106/201827_ac61db63_1110335.gif" target="_blank">效果图</a>
27. 使用jenkins+centos+git+maven搭建持续集成环境自动化部署分布式服务 
28. 使用docker+compose+jenkins+gitlab+spring cloud实现微服务的编排、持续集成和动态扩容 
29. 使用FastDFS搭建分布式文件系统（高可用、负载均衡）
30. 搭建高可用nginx集群和Tomcat负载均衡 
31. 使用mycat实现Mysql数据库的主从复制、读写分离、分表分库、负载均衡和高可用 
32. [Spring boot整合Elastic search实现全文检索和大数据分析](https://gitee.com/shenzhanwang/Spring-elastic_search) ![输入图片说明](https://img.shields.io/badge/-%E6%8B%9B%E7%89%8C-yellow.svg "在这里输入图片标题")
#### 特别篇：分布式事务和并发控制
33. 基于可靠消息最终一致性实现分布式事务（activeMQ）
34. Spring boot dubbo整合seata实现分布式事务![输入图片说明](https://img.shields.io/badge/-%E7%B2%BE%E5%93%81-orange.svg "在这里输入图片标题")
> 包含组件nacos v1.1.0 + seata v0.7.1 +spring boot dubbo v2.7.5
<a href="https://images.gitee.com/uploads/images/2020/0119/112233_62a33a77_1110335.gif" target="_blank">效果图</a>
35. Spring cloud alibaba v2.1.0整合seata实现分布式事务 ![输入图片说明](https://img.shields.io/badge/-%E7%B2%BE%E5%93%81-orange.svg "在这里输入图片标题")
> 包含组件nacos v1.1.0 + seata v0.7.1 +spring cloud alibaba v2.1.0
<a href="https://images.gitee.com/uploads/images/2020/0119/134408_ee14a016_1110335.gif" target="_blank">效果图</a>
36. 并发控制：数据库锁机制和事务隔离级别的实现![输入图片说明](https://img.shields.io/badge/-%E7%B2%BE%E5%93%81-orange.svg "在这里输入图片标题") 
37. 并发控制：使用redis实现分布式锁  ![输入图片说明](https://img.shields.io/badge/-%E7%B2%BE%E5%93%81-orange.svg "在这里输入图片标题")
38. 并发控制：使用zookeeper实现分布式锁 
39. 并发控制：Java多线程编程实例
40. 并发控制：使用netty实现高性能NIO通信 
### 视频教程
![输入图片说明](https://images.gitee.com/uploads/images/2021/0115/082443_6e3b60c8_1110335.png "封面.png")

<a href="https://www.roncoo.com/view/1303618857169457154" target="_blank">在线观看（含源码和PPT）</a>
