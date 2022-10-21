# flink-http-connector

This is a flink connector for http/https, can running with java 1.8 and flink 1.12.0.

Use HttpURLConnection/HttpsURLConnection to connect to the web server, Note: ignore ssl verification when using HttpsURLConnection.
  
If you use java 11 and flink 1.15+, check out [getindata/flink-http-connector](https://github.com/getindata/flink-http-connector).

Now only supports sink!
## Prerequisites
* Java 8
* Maven 3
* Flink 1.12+
* java.net.HttpURLConnection

## Sample

```java
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
```

## Connector Options
### HTTP Sink
| Option                       | Required | Description/Value                                                                                                   |
|------------------------------|----------|---------------------------------------------------------------------------------------------------------------------|
| connector                    | required | Specify what connector to use. Can only be _'http-sink'_.                                                           |
| url                          | required | The base URL that should be use for HTTP requests. For example _http(s)://localhost:8080/client_.                   |
| format                       | required | Can only be _'json'_.                                                                                               |
| method                       | required | Specify which HTTP method to use in the request. The value should be set either to `GET`,`DELETE`, `POST` or `PUT`. |
| use-https                    | optional | `true` or `false`, When true, will use https protocol. default `false` .                                            |
| http.log.fail                | optional | `true` or `false`, default be `true`, When response status != 200, log error msg.                                   |
| http.log.success             | optional | `true` or `false`, default be `true`, When response status == 200, log response body.                               |
