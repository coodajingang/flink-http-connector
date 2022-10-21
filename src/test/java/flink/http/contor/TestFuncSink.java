package flink.http.contor;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TestFuncSink<IN> extends RichSinkFunction<IN> {
    private final String url;
    private final String method;
    private CloseableHttpClient httpClient;
    private final SerializationSchema<IN> serializer;

    public TestFuncSink(String url, String method, SerializationSchema<IN> serializer) {
        log.info("new HttpSinkFunction");
        this.url = url;
        this.method = method;
        this.serializer = serializer;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        log.info("open HttpSinkFunction");
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString()));
        this.httpClient = HttpClients.custom()
                .setDefaultHeaders(headers)
                .build();
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void invoke(IN value, SinkFunction.Context context) throws Exception {
        log.info("invoke HttpSinkFunction");
        if ("GET".equals(method)) {
            HttpGet get = new HttpGet(url);

            CloseableHttpResponse response = this.httpClient.execute(get);
            log.info("RESPONESE: {}", response);
        } else {
            HttpPost post = new HttpPost(url);
            //ObjectMapper objectMapper = new ObjectMapper();
            //String s = objectMapper.writeValueAsString(new String(serializer.serialize(value)));
            post.setEntity(new StringEntity(new String(serializer.serialize(value))));
            CloseableHttpResponse response = this.httpClient.execute(post);
            log.info("RESPONESE: {}", EntityUtils.toString(response.getEntity()));

            //String data = new String(serializer.serialize(value));
            // log.info("RESPONSE: {}", data);
        }

    }
}
