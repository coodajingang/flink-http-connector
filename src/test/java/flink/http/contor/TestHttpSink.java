package flink.http.contor;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.formats.json.JsonRowDataSerializationSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class TestHttpSink {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        environment.setParallelism(1);
        List<String> strings = Arrays.asList("apache", "flink", "http");
        DataStreamSource<String> ds = environment.fromCollection(strings);

        ds.print();

        HttpConnectorConfig config = new HttpConnectorConfig();
        config.setUrl("http://localhost:8000/post");
        config.setMethod("POST");
        config.setLogSuccess(true);

        SimpleStringSchema simpleStringSchema = new SimpleStringSchema(Charset.forName("UTF-8"));
        HttpSinkFunction httpSink = new HttpSinkFunction<String>(config, simpleStringSchema);
        ds.addSink(httpSink);

        environment.execute();
    }
}
