package com.st.workspace.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "private")
@ToString
@Builder
public class Workspace {

    @Id
    private String id;
    private Boolean active;
    private String backgroundColor;
    private Long date;
    private String name;
    private String oldid;
    private String owner;
    private String payload;
    private List<Chart> charts;
    @Version
    private Integer revision;
    private String screenshot;
    private Integer layout;
    private List<String> tags;
    private String title;
    private Long createdAt;
    private Long updatedAt;

    @Data
    @Builder
    public static class Chart {
        private String id;
        private Instrument instrument;
        private Long timeInterval;
        private Boolean isVisible;
        private Integer index;
        private Boolean isPlaceHolder;
        private String symbol;
        private Long timestamp;
        private String barType;

        @Data
        @Builder
        public static class Instrument {
            private String symbol;
            private String company;
            private String exchange;
        }
    }
}
