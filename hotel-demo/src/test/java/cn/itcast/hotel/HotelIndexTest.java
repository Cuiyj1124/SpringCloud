package cn.itcast.hotel;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static cn.itcast.hotel.constants.HotelConstants.mapping_index;

public class HotelIndexTest {

    private RestHighLevelClient restHighLevelClient;

    @Test
    void testInit(){
        System.out.println(restHighLevelClient);
    }

    @Test
    void createHotelIndex() throws IOException {
        //1.创建request对象
        CreateIndexRequest request = new CreateIndexRequest("hotel");
        //2.准备请求的参数：DSl语句
        request.source(mapping_index, XContentType.JSON);
        ///3.发送请求
        restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
    }

    @BeforeEach
    void setUp(){
        this.restHighLevelClient = new RestHighLevelClient(RestClient.builder(
           HttpHost.create("http://192.168.220.128:9200")
        ));
    }

    @AfterEach
    void tearDown() throws IOException {
        this.restHighLevelClient.close();
    }

    @Test
    void testDeleteIndex() throws IOException {

        //1.创建request对象
        DeleteIndexRequest request = new DeleteIndexRequest("hotel");
        //2.发送请求
        restHighLevelClient.indices().delete(request,RequestOptions.DEFAULT);
    }

    //判断是否存在
    @Test
    void testExistsIndex() throws IOException {

        //1.创建request对象
        GetIndexRequest request = new GetIndexRequest("hotel");
        //2.发送请求
        boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);

        System.out.println(exists ? "索引库存在" : "没有找到");
    }
}
