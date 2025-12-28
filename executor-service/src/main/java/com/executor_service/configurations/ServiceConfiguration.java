package com.executor_service.configurations;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "configs")
@Data
public class ServiceConfiguration {

    private JobDb jobDb;
    private Kafka kafka;

    @Data
    public static class JobDb {
        private String name;
        private String url;
    }

    @Data
    public static class Kafka {
        private String bootstrapServers;
        private Topic topic;
        private Consumer consumer;

        @Data
        public static class Topic {
            private String jobEvents;
        }

        @Data
        public static class Consumer {
            private String groupId;
            private String autoOffsetReset;
            private boolean enableAutoCommit;
            private int maxPollRecords;
            private int sessionTimeoutMs;
            private int maxPollIntervalMs;
            private int concurrency;
        }
    }
}

