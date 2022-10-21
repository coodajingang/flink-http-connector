package flink.http.contor.internal.config;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;

public final class HttpConfigOptions {
    public static final ConfigOption<String> URL =
            ConfigOptions.key(HttpConfigConstants.CONFIG_KEY_URL)
                    .stringType()
                    .noDefaultValue()
                    .withDescription("The HTTP endpoint URL.");

    public static final ConfigOption<String> METHOD =
            ConfigOptions.key(HttpConfigConstants.CONFIG_KEY_METHOD)
                    .stringType()
                    .defaultValue("POST")
                    .withDescription("Method used for requests built from SQL's INSERT.");

    public static final ConfigOption<Integer> CONNECT_TIMEOUT =
            ConfigOptions.key(HttpConfigConstants.CONFIG_KEY_CONN_TIMEOUT)
                    .intType()
                    .defaultValue(HttpConfigConstants.DEFAULT_TIMEOUT)
                    .withDescription("Http connection timeout.");

    public static final ConfigOption<Integer> READ_TIMEOUT =
            ConfigOptions.key(HttpConfigConstants.CONFIG_KEY_READ_TIMEOUT)
                    .intType()
                    .defaultValue(HttpConfigConstants.DEFAULT_TIMEOUT)
                    .withDescription("Http read timeout.");

    public static final ConfigOption<Boolean> USE_HTTPS =
            ConfigOptions.key(HttpConfigConstants.CONFIG_KEY_USER_HTTPS)
                    .booleanType()
                    .defaultValue(Boolean.FALSE)
                    .withDescription("Use https instead of http.");

    public static final ConfigOption<String> HTTPS_CERT_SERVER =
            ConfigOptions.key(HttpConfigConstants.CONFIG_KEY_CERT_SERVER)
                    .stringType()
                    .noDefaultValue()
                    .withDescription("Https security cert server.");

    public static final ConfigOption<String> HTTPS_CERT_CLIENT =
            ConfigOptions.key(HttpConfigConstants.CONFIG_KEY_CERT_CLIENT)
                    .stringType()
                    .noDefaultValue()
                    .withDescription("Https security cert client.");

    public static final ConfigOption<String> HTTPS_KEY_CLIENT =
            ConfigOptions.key(HttpConfigConstants.CONFIG_KEY_KEY_CLIENT)
                    .stringType()
                    .noDefaultValue()
                    .withDescription("Https security key client.");

    public static final ConfigOption<Boolean> CERT_ALLOW_SELF_SIGN =
            ConfigOptions.key(HttpConfigConstants.CONFIG_KEY_CERT_ALLOW_SELFSIGN)
                    .booleanType()
                    .defaultValue(Boolean.TRUE)
                    .withDescription("Https security allow self sign.");

    public static final ConfigOption<Boolean> LOG_FAIL =
            ConfigOptions.key(HttpConfigConstants.CONFIG_KEY_LOG_FAIL)
                    .booleanType()
                    .defaultValue(Boolean.TRUE)
                    .withDescription("Do log when http response fail.");

    public static final ConfigOption<Boolean> LOG_SUCCESS =
            ConfigOptions.key(HttpConfigConstants.CONFIG_KEY_LOG_SUCCESS)
                    .booleanType()
                    .defaultValue(Boolean.FALSE)
                    .withDescription("Do log when http response success.");
}