package cn.itcast.es.demo1;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONPOJOBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class EStudyFirst {
    TransportClient client =null;
    @BeforeTest
    public void getClient() throws UnknownHostException {
        final Settings setting = Settings.builder().put("cluster.name", "myes").build();
        final TransportAddress transportAddress1 = new TransportAddress(InetAddress.getByName("node01"), 9300);
        final TransportAddress transportAddress2 = new TransportAddress(InetAddress.getByName("node02"), 9300);
        final TransportAddress transportAddress3 = new TransportAddress(InetAddress.getByName("node03"), 9300);
        client = new PreBuiltTransportClient(setting).addTransportAddress(transportAddress1).addTransportAddress(transportAddress2).addTransportAddress(transportAddress3);
        System.out.println(client.toString());

    }

    @Test
    public void createIndex1(){
        String json = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"travelying out Elasticsearch\"" +
                "}";
        IndexResponse indexResponse = client.prepareIndex("myindex1", "article", "1").setSource(json, XContentType.JSON).get();

    }
    @Test
    public void createIndex2(){

        HashMap<String, String> jsonMap = new HashMap<String, String>();
        jsonMap.put("name", "zhangsan");
        jsonMap.put("sex", "1");
        jsonMap.put("age", "18");
        jsonMap.put("address", "bj");
        IndexResponse indexResponse = client.prepareIndex("myindex1", "article", "2")
                .setSource(jsonMap)
                .get();

        }


    @Test
    public void index3() throws IOException {
        IndexResponse indexResponse = client.prepareIndex("myindex1", "article", "3")
                .setSource(new XContentFactory().jsonBuilder()
                        .startObject()
                        .field("name", "lisi")
                        .field("age", "18")
                        .field("sex", "0")
                        .field("address", "bj")
                        .endObject())
                .get();
        client.close();

    }
    @Test
    public void objToIndex(){
        Person person = new Person();
        person.setAge(18);
        person.setId(20);
        person.setName("张三丰");
        person.setAddress("武当山");
        person.setEmail("zhangsanfeng@163.com");
        person.setPhone("18588888888");
        person.setSex(1);
        String json = JSONObject.toJSONString(person);
        System.out.println(json);
        client.prepareIndex("myindex1","article","32").setSource(json,XContentType.JSON).get();
    }

    @Test
    public void index4() throws IOException {
        BulkRequestBuilder bulk = client.prepareBulk();
        bulk.add(client.prepareIndex("myindex1", "article", "4")
                .setSource(new XContentFactory().jsonBuilder()
                        .startObject()
                        .field("name", "wangwu")
                        .field("age", "18")
                        .field("sex", "0")
                        .field("address", "bj")
                        .endObject()));
        bulk.add(client.prepareIndex("news", "article", "5")
                .setSource(new XContentFactory().jsonBuilder()
                        .startObject()
                        .field("name", "zhaoliu")
                        .field("age", "18")
                        .field("sex", "0")
                        .field("address", "bj")
                        .endObject()));
        BulkResponse bulkResponse = bulk.get();
        System.out.println(bulkResponse);

    }
    @Test
    public void addBatch(){
         BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        Person person = new Person();
        person.setPhone("13587963214");
        person.setId(5);

        Person person1 = new Person();
        person1.setPhone("13587963214");
        person1.setId(5);

        Person person2 = new Person();
        person2.setPhone("13587963214");
        person2.setId(5);

        Person person3 = new Person();
        person3.setPhone("13587963214");
        person3.setId(5);

        String personstr = JSONObject.toJSONString(person);
        String personstr1 = JSONObject.toJSONString(person1);
        String personstr2 = JSONObject.toJSONString(person2);
        String personstr3 = JSONObject.toJSONString(person3);
        final IndexRequestBuilder indexRequestBuilder = client.prepareIndex("myindex1", "article", "5").setSource(personstr, XContentType.JSON);
        final IndexRequestBuilder indexRequestBuilder1 = client.prepareIndex("myindex1", "article", "6").setSource(personstr1, XContentType.JSON);

        final IndexRequestBuilder indexRequestBuilder2 = client.prepareIndex("myindex1", "article", "7").setSource(personstr2, XContentType.JSON);

        final IndexRequestBuilder indexRequestBuilder3 = client.prepareIndex("myindex1", "article", "8").setSource(personstr3, XContentType.JSON);

        bulkRequestBuilder.add(indexRequestBuilder).add(indexRequestBuilder1).add(indexRequestBuilder2).add(indexRequestBuilder3).get();


    }

    @Test
    public void updateIndex(){
        Person guansheng = new Person(5, "宋江", 88, 0, "水泊梁山", "17666666666", "wusong@itcast.com","及时雨宋江");
        client.prepareUpdate().setIndex("myindex1").setType("article").setId("8")
                .setDoc(JSONObject.toJSONString(guansheng),XContentType.JSON)
                .get();
        client.close();
    }
    @Test
    public void deleteById(){
        DeleteResponse deleteResponse = client.prepareDelete("myindex1", "article", "8").get();
        client.close();
    }
    @Test
    public  void  deleteIndex(){
       client.admin().indices().prepareDelete("myindex1").execute().actionGet();
        client.close();
    }






    @Test
    public void createIndexBatch() throws Exception {
        Settings settings = Settings
                .builder()
                .put("cluster.name", "myes") //节点名称， 在es配置的时候设置
                //自动发现我们其他的es的服务器
                .put("client.transport.sniff", "true")
                .build();
        //创建客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("node01"), 9300));//以本机作为节点
        //创建映射
        XContentBuilder mapping = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("properties")
                //      .startObject("m_id").field("type","keyword").endObject()
                .startObject("id").field("type", "integer").endObject()
                .startObject("name").field("type", "text").field("analyzer", "ik_max_word").endObject()
                .startObject("age").field("type", "integer").endObject()
                .startObject("sex").field("type", "text").field("analyzer", "ik_max_word").endObject()
                .startObject("address").field("type", "text").field("analyzer", "ik_max_word").endObject()
                .startObject("phone").field("type", "text").endObject()
                .startObject("email").field("type", "text").endObject()
                .startObject("say").field("type", "text").field("analyzer", "ik_max_word").endObject()
                .endObject()
                .endObject();
        //pois：索引名   cxyword：类型名（可以自己定义）
        PutMappingRequest putmap = Requests.putMappingRequest("indexsearch").type("mysearch").source(mapping);
        //创建索引
        client.admin().indices().prepareCreate("indexsearch").execute().actionGet();
        //为索引添加映射
        client.admin().indices().putMapping(putmap).actionGet();


        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        Person lujunyi = new Person(2, "玉麒麟卢俊义", 28, 1, "水泊梁山", "17666666666", "lujunyi@itcast.com","hello world今天天气还不错");
        Person wuyong = new Person(3, "智多星吴用", 45, 1, "水泊梁山", "17666666666", "wuyong@itcast.com","行走四方，抱打不平");
        Person gongsunsheng = new Person(4, "入云龙公孙胜", 30, 1, "水泊梁山", "17666666666", "gongsunsheng@itcast.com","走一个");
        Person guansheng = new Person(5, "大刀关胜", 42, 1, "水泊梁山", "17666666666", "wusong@itcast.com","我的大刀已经饥渴难耐");
        Person linchong = new Person(6, "豹子头林冲", 18, 1, "水泊梁山", "17666666666", "linchong@itcast.com","梁山好汉");
        Person qinming = new Person(7, "霹雳火秦明", 28, 1, "水泊梁山", "17666666666", "qinming@itcast.com","不太了解");
        Person huyanzhuo = new Person(8, "双鞭呼延灼", 25, 1, "水泊梁山", "17666666666", "huyanzhuo@itcast.com","不是很熟悉");
        Person huarong = new Person(9, "小李广花荣", 50, 1, "水泊梁山", "17666666666", "huarong@itcast.com","打酱油的");
        Person chaijin = new Person(10, "小旋风柴进", 32, 1, "水泊梁山", "17666666666", "chaijin@itcast.com","吓唬人的");
        Person zhisheng = new Person(13, "花和尚鲁智深", 15, 1, "水泊梁山", "17666666666", "luzhisheng@itcast.com","倒拔杨垂柳");
        Person wusong = new Person(14, "行者武松", 28, 1, "水泊梁山", "17666666666", "wusong@itcast.com","二营长。。。。。。");

        bulkRequestBuilder.add(client.prepareIndex("indexsearch", "mysearch", "1")
                .setSource(JSONObject.toJSONString(lujunyi), XContentType.JSON)
        );
        bulkRequestBuilder.add(client.prepareIndex("indexsearch", "mysearch", "2")
                .setSource(JSONObject.toJSONString(wuyong), XContentType.JSON)
        );
        bulkRequestBuilder.add(client.prepareIndex("indexsearch", "mysearch", "3")
                .setSource(JSONObject.toJSONString(gongsunsheng), XContentType.JSON)
        );
        bulkRequestBuilder.add(client.prepareIndex("indexsearch", "mysearch", "4")
                .setSource(JSONObject.toJSONString(guansheng), XContentType.JSON)
        );
        bulkRequestBuilder.add(client.prepareIndex("indexsearch", "mysearch", "5")
                .setSource(JSONObject.toJSONString(linchong), XContentType.JSON)
        );
        bulkRequestBuilder.add(client.prepareIndex("indexsearch", "mysearch", "6")
                .setSource(JSONObject.toJSONString(qinming), XContentType.JSON)
        );
        bulkRequestBuilder.add(client.prepareIndex("indexsearch", "mysearch", "7")
                .setSource(JSONObject.toJSONString(huyanzhuo), XContentType.JSON)
        );
        bulkRequestBuilder.add(client.prepareIndex("indexsearch", "mysearch", "8")
                .setSource(JSONObject.toJSONString(huarong), XContentType.JSON)
        );
        bulkRequestBuilder.add(client.prepareIndex("indexsearch", "mysearch", "9")
                .setSource(JSONObject.toJSONString(chaijin), XContentType.JSON)
        );
        bulkRequestBuilder.add(client.prepareIndex("indexsearch", "mysearch", "10")
                .setSource(JSONObject.toJSONString(zhisheng), XContentType.JSON)
        );
        bulkRequestBuilder.add(client.prepareIndex("indexsearch", "mysearch", "11")
                .setSource(JSONObject.toJSONString(wusong), XContentType.JSON)
        );

        bulkRequestBuilder.get();
        client.close();

    }

    @Test
    public void query1() {
        GetResponse documentFields = client.prepareGet("indexsearch", "mysearch", "11").get();
        String index = documentFields.getIndex();
        String type = documentFields.getType();
        String id = documentFields.getId();
        System.out.println(index);
        System.out.println(type);
        System.out.println(id);
        Map<String, Object> source = documentFields.getSource();
        for (String s : source.keySet()) {
            System.out.println(source.get(s));
        }
    }
    @Test
    public void queryAll() {
        SearchResponse searchResponse = client
                .prepareSearch("indexsearch")
                .setTypes("mysearch")
                .setQuery(new MatchAllQueryBuilder())
                .get();
        SearchHits searchHits = searchResponse.getHits();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
        }
        client.close();
    }
    @Test
    public void  rangeQuery(){
        SearchResponse searchResponse = client.prepareSearch("indexsearch")
                .setTypes("mysearch")
                .setQuery(new RangeQueryBuilder("age").gt(17).lt(29))
                .get();
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit documentFields : hits1) {
            System.out.println(documentFields.getSourceAsString());
        }
        client.close();
    }

    /**
     * 词条查询
     */
    @Test
    public  void termQuery(){
        SearchResponse searchResponse = client.prepareSearch("indexsearch").setTypes("mysearch")
                .setQuery(new TermQueryBuilder("say", "熟悉"))
                .get();
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit documentFields : hits1) {
            System.out.println(documentFields.getSourceAsString());
        }
    }
    /**
     * fuzzyQuery表示英文单词的最大可纠正次数，最大可以自动纠正两次
     */
    @Test
    public void fuzzyQuery(){
        SearchResponse searchResponse = client.prepareSearch("indexsearch").setTypes("mysearch")
                .setQuery(QueryBuilders.fuzzyQuery("say", "helOL").fuzziness(Fuzziness.TWO))
                .get();
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit documentFields : hits1) {
            System.out.println(documentFields.getSourceAsString());
        }
        client.close();
    }

    @Test
    public void wildCardQueryTest(){
        SearchResponse searchResponse = client.prepareSearch("indexsearch").setTypes("mysearch")
                .setQuery(QueryBuilders.wildcardQuery("say", "hel*"))
                .get();
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit documentFields : hits1) {
            System.out.println(documentFields.getSourceAsString());
        }
        client.close();
    }

    /**
     * 多条件组合查询 boolQuery
     * 查询年龄是18到28范围内且性别是男性的，或者id范围在10到13范围内的
     *
     */
    @Test
    public void boolQueryTest(){
        RangeQueryBuilder age = QueryBuilders.rangeQuery("age").gt(17).lt(29);
        TermQueryBuilder sex = QueryBuilders.termQuery("sex", "1");
        RangeQueryBuilder id = QueryBuilders.rangeQuery("id").gt("9").lt("15");

        SearchResponse searchResponse = client.prepareSearch("indexsearch").setTypes("mysearch")
                .setQuery(
                        QueryBuilders.boolQuery().should(id)
                                .should(QueryBuilders.boolQuery().must(sex).must(age)))
                .get();
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit documentFields : hits1) {
            System.out.println(documentFields.getSourceAsString());
        }
        client.close();
    }



    /*分页查询
     */
    @Test
    public void getPageIndex(){
        int  pageSize = 5;
        int pageNum = 2;
        int startNum = (pageNum-1)*5;
        SearchResponse searchResponse = client.prepareSearch("indexsearch")
                .setTypes("mysearch")
                .setQuery(QueryBuilders.matchAllQuery())
                .addSort("id", SortOrder.ASC)
                .setFrom(startNum)
                .setSize(pageSize)
                .get();
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit documentFields : hits1) {
            System.out.println(documentFields.getSourceAsString());
        }
        client.close();
    }

    /**
     * 高亮查询
     */
    @Test
    public  void  highLight(){
        //设置我们的查询高亮字段
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch("indexsearch")
                .setTypes("mysearch")
                .setQuery(QueryBuilders.termQuery("say", "hello"));

        //设置我们字段高亮的前缀与后缀
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("say").preTags("<font style='color:red'>").postTags("</font>");

        //通过高亮来进行我们的数据查询
        SearchResponse searchResponse = searchRequestBuilder.highlighter(highlightBuilder).get();
        SearchHits hits = searchResponse.getHits();
        System.out.println("查询出来一共"+ hits.totalHits+"条数据");
        for (SearchHit hit : hits) {
            //打印没有高亮显示的数据
            System.out.println(hit.getSourceAsString());
            System.out.println("=========================");
            //打印我们经过高亮显示之后的数据
            Text[] says = hit.getHighlightFields().get("say").getFragments();
            for (Text say : says) {
                System.out.println(say);
            }

     /*   Map<String, HighlightField> highlightFields = hit.getHighlightFields();
        System.out.println(highlightFields);*/
        }
        client.close();
    }

//-----------------------------------------------------------------------------todo java api 高级操作
    @Test
    public void groupAndCount() {
        //1：构建查询提交
        SearchRequestBuilder builder = client.prepareSearch("player").setTypes("player");
        //2：指定聚合条件
        TermsAggregationBuilder team = AggregationBuilders.terms("player_count").field("team");
        //3:将聚合条件放入查询条件中
        builder.addAggregation(team);
        //4:执行action，返回searchResponse
        SearchResponse searchResponse = builder.get();
        Aggregations aggregations = searchResponse.getAggregations();
        for (Aggregation aggregation : aggregations) {
            StringTerms stringTerms = (StringTerms) aggregation;
            List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
            for (StringTerms.Bucket bucket : buckets) {
                System.out.println(bucket.getKey());
                System.out.println(bucket.getDocCount());
            }
        }
    }


    @Test
    public void teamAndPosition(){
        SearchRequestBuilder builder = client.prepareSearch("player").setTypes("player");
        TermsAggregationBuilder team = AggregationBuilders.terms("player_count").field("team");
        TermsAggregationBuilder position = AggregationBuilders.terms("posititon_count").field("position");
        team.subAggregation(position);
        SearchResponse searchResponse = builder.addAggregation(team).addAggregation(position).get();
        Aggregations aggregations =
                searchResponse.getAggregations();
        for (Aggregation aggregation : aggregations) {
            // System.out.println(aggregation.toString());
            StringTerms stringTerms = (StringTerms) aggregation;
            List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
            for (StringTerms.Bucket bucket : buckets) {
                long docCount = bucket.getDocCount();
                Object key = bucket.getKey();
                System.out.println("当前队伍名称为" +  key + "该队伍下有"+docCount + "个球员");

                Aggregation posititon_count = bucket.getAggregations().get("posititon_count");
                if(null != posititon_count){
                    StringTerms positionTrem = (StringTerms) posititon_count;
                    List<StringTerms.Bucket> buckets1 = positionTrem.getBuckets();
                    for (StringTerms.Bucket bucket1 : buckets1) {
                        Object key1 = bucket1.getKey();
                        long docCount1 = bucket1.getDocCount();
                        System.out.println("该队伍下面的位置为" +  key1+"该位置下有" +  docCount1 +"人");
                    }
                }
            }
        }
    }







    /**
     * 批量添加数据
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void addIndexDatas() throws IOException, ExecutionException, InterruptedException {
        //获取settings
        //配置es集群的名字
        Settings settings = Settings.builder().put("cluster.name", "myes").build();
        //获取客户端
        TransportAddress transportAddress = new TransportAddress(InetAddress.getByName("node01"), 9300);

        TransportAddress transportAddress2 = new TransportAddress(InetAddress.getByName("node02"), 9300);

        TransportAddress transportAddress3 = new TransportAddress(InetAddress.getByName("node03"), 9300);
        //获取client客户端
        TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(transportAddress).addTransportAddress(transportAddress2).addTransportAddress(transportAddress3);

        /**
         * 创建索引
         * */
        client.admin().indices().prepareCreate("player").get();
        //构建json的数据格式，创建映射
        XContentBuilder mappingBuilder =new XContentFactory().jsonBuilder()
                .startObject()
                .startObject("player")
                .startObject("properties")
                .startObject("name").field("type","text").field("index", "true").field("fielddata","true").endObject()
                .startObject("age").field("type","integer").endObject()
                .startObject("salary").field("type","integer").endObject()
                .startObject("team").field("type","text").field("index", "true").field("fielddata","true").endObject()
                .startObject("position").field("type","text").field("index", "true").field("fielddata","true").endObject()
                .endObject()
                .endObject()
                .endObject();
        PutMappingRequest request = Requests.putMappingRequest("player")
                .type("player")
                .source(mappingBuilder);
        client.admin().indices().putMapping(request).get();


        //批量添加数据开始

        BulkRequestBuilder bulkRequest = client.prepareBulk();

// either use client#prepare, or use Requests# to directly build index/delete requests
        bulkRequest.add(client.prepareIndex("player", "player", "1")
                .setSource(new XContentFactory().jsonBuilder()
                        .startObject()
                        .field("name", "郭德纲")
                        .field("age", 33)
                        .field("salary",3000)
                        .field("team" , "cav")
                        .field("position" , "sf")
                        .endObject()
                )
        );
        bulkRequest.add(client.prepareIndex("player", "player", "2")
                .setSource(new  XContentFactory().jsonBuilder()
                        .startObject()
                        .field("name", "于谦")
                        .field("age", 25)
                        .field("salary",2000)
                        .field("team" , "cav")
                        .field("position" , "pg")
                        .endObject()
                )
        );
        bulkRequest.add(client.prepareIndex("player", "player", "3")
                .setSource(new XContentFactory().jsonBuilder()
                        .startObject()
                        .field("name", "岳云鹏")
                        .field("age", 29)
                        .field("salary",1000)
                        .field("team" , "war")
                        .field("position" , "pg")
                        .endObject()
                )
        );

        bulkRequest.add(client.prepareIndex("player", "player", "4")
                .setSource(new XContentFactory().jsonBuilder()
                        .startObject()
                        .field("name", "爱因斯坦")
                        .field("age", 21)
                        .field("salary",300)
                        .field("team" , "tim")
                        .field("position" , "sg")
                        .endObject()
                )
        );

        bulkRequest.add(client.prepareIndex("player", "player", "5")
                .setSource(new XContentFactory().jsonBuilder()
                        .startObject()
                        .field("name", "张云雷")
                        .field("age", 26)
                        .field("salary",2000)
                        .field("team" , "war")
                        .field("position" , "pf")
                        .endObject()
                )
        );
        bulkRequest.add(client.prepareIndex("player", "player", "6")
                .setSource(new XContentFactory().jsonBuilder()
                        .startObject()
                        .field("name", "爱迪生")
                        .field("age", 40)
                        .field("salary",1000)
                        .field("team" , "tim")
                        .field("position" , "pf")
                        .endObject()
                )
        );
        bulkRequest.add(client.prepareIndex("player", "player", "7")
                .setSource(new XContentFactory().jsonBuilder()
                        .startObject()
                        .field("name", "牛顿")
                        .field("age", 21)
                        .field("salary",500)
                        .field("team" , "tim")
                        .field("position" , "c")
                        .endObject()
                )
        );

        bulkRequest.add(client.prepareIndex("player", "player", "8")
                .setSource(new XContentFactory().jsonBuilder()
                        .startObject()
                        .field("name", "特斯拉")
                        .field("age", 20)
                        .field("salary",500)
                        .field("team" , "tim")
                        .field("position" , "sf")
                        .endObject()
                )
        );
        BulkResponse bulkResponse = bulkRequest.get();
        client.close();
    }
    @AfterTest
    public void closeClient(){
        client.close();
    }
}


