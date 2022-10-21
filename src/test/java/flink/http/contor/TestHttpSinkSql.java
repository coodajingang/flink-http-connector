package flink.http.contor;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class TestHttpSinkSql {
    public static void main(String[] args) throws Exception {
        ParameterTool parameters = ParameterTool.fromSystemProperties();
        parameters = parameters.mergeWith(ParameterTool.fromArgs(args));

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // env.enableCheckpointing(5000);
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(1000, 1000));
        env.setParallelism(1);
        env.disableOperatorChaining();
        env.getConfig().setGlobalJobParameters(parameters);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        tableEnv.executeSql(
                "CREATE TABLE Orders (id STRING, id2 STRING, proc_time AS PROCTIME())"
                        + " WITH ("
                        + "'connector' = 'datagen', 'rows-per-second' = '1', 'fields.id.kind' = 'sequence',"
                        + " 'fields.id.start' = '1', 'fields.id.end' = '120',"
                        + " 'fields.id2.kind' = 'sequence', 'fields.id2.start' = '2',"
                        + " 'fields.id2.end' = '120')"
        );

        tableEnv.executeSql(
                "create table sinktable (id STRING, id2 STRING)"
                        + " WITH ("
                        + "'connector' = 'http-sink',"
                        + "'format' = 'json',"
                        + "'method' = 'POST',"
                        + "'http.log.success' = 'true',"
                        + "'http.log.fail' = 'true',"
                        + "'http.header.Content-Type' = 'application/json',"
                        + " 'url' = 'http://127.0.0.1:8000/post' "
                        + ")"
        );
        /* DataStream<Row> rowDataStream = tableEnv.toDataStream(resultTable);
        rowDataStream.print();*/

        tableEnv.executeSql("insert into sinktable select id,id2 from Orders");
        // Table result = tableEnv.sqlQuery("SELECT * FROM Customers");
        // Table result = tableEnv.sqlQuery("SELECT * FROM T WHERE T.id > 10");

        env.execute();
    }
}
