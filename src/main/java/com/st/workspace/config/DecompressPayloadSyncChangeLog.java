package com.st.workspace.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.st.workspace.entity.Workspace;
import com.st.workspace.utils.GzipUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.st.workspace.utils.GzipUtil.UTF_8;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@ChangeLog
@Slf4j
public class DecompressPayloadSyncChangeLog {
    public final int PER_QUERY_LIMIT = 5;
    public final int UPDATE_EXPIRATION_SECONDS = 30;

    private ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().build();

    private final AtomicInteger allCounter = new AtomicInteger(0);
    private final AtomicInteger successfulUpdatesCounter = new AtomicInteger(0);
    private final AtomicInteger warningUpdatesCounter = new AtomicInteger(0);

    @ChangeSet(order = "001", id = "Sync script Payload decompress on workspace.Stage", author = "admin", runAlways = true)
    public void syncPayloadDecodeOnStage(MongockTemplate mongockTemplate) {
        log.info("Start sync Payload decode script to Database");

        var query = getWorkspaceQuery();
        List<Workspace> workspaces = mongockTemplate.find(query, Workspace.class);

        while (!workspaces.isEmpty()) {
            workspaces.stream()
                    .map(this::setWorkspaceUpdate)
                    .forEach(workspace -> {
                        var criteriaUpdate = where("_id").is(workspace.getId());
                        Update stageUpdate = new Update()
                                .set("chart", workspace.getChart())
                                .set("updatedAt", workspace.getUpdatedAt());
                        mongockTemplate.findAndModify(new Query(criteriaUpdate), stageUpdate, Workspace.class);
                    });

            workspaces = mongockTemplate.find(query, Workspace.class);
        }

        log.info("Found by criteria of sync = {} stage(s)", allCounter);
        if (successfulUpdatesCounter.get() != 0)
            log.info("Successful sync updated = {} stage(s)", successfulUpdatesCounter);
        if (warningUpdatesCounter.get() != 0)
            log.warn("Error sync update = {} stage(s)", warningUpdatesCounter);
        log.info("Finish sync Payload decode script to Database");
    }

    private Query getWorkspaceQuery() {
        var ltExpirationDate = Instant.now().minusSeconds(UPDATE_EXPIRATION_SECONDS).toEpochMilli();
        var criteriaExpirationUpdate = Criteria.where("updatedAt").lt(ltExpirationDate);
        var query = Query.query(new Criteria().andOperator(criteriaExpirationUpdate));
        query.fields().include("_id", "payload");
        query.limit(PER_QUERY_LIMIT);

        return query;
    }

    private Workspace setWorkspaceUpdate(Workspace workspace) {
        allCounter.getAndIncrement();
        try {
            var decompressPayload = GzipUtil.toDecompress(workspace.getPayload(), UTF_8);
            var chart = payloadToChart(decompressPayload);
            workspace.setChart(chart);
            workspace.setUpdatedAt(Instant.now().toEpochMilli());
            successfulUpdatesCounter.getAndIncrement();
        } catch (Exception ex) {
            ex.printStackTrace();
            warningUpdatesCounter.getAndIncrement();
        }

        return workspace;
    }

    private Workspace.Chart payloadToChart(String payload) throws JsonProcessingException {
        var payloadChartSubstrStart = 40;
        var payloadChartSubstrEnd = payload.length() - 1;
        var payloadChart = payload.substring(payloadChartSubstrStart, payloadChartSubstrEnd);

        return objectMapper.readValue(payloadChart, Workspace.Chart.class);
    }
}
