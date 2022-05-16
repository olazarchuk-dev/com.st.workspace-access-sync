package com.st.workspace.config;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.st.workspace.entity.Workspace;
import com.st.workspace.utils.GzipUtil;
import com.st.workspace.utils.WorkspaceConvertor2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.st.workspace.utils.GzipUtil.UTF_8;
import static java.time.Instant.now;
import static org.springframework.data.mongodb.core.query.Criteria.*;

@ChangeLog
@Slf4j
public class DecompressPayloadSyncChangeLog {
    public final int PER_QUERY_LIMIT = 5;
    public final int UPDATE_EXPIRATION_SECONDS = 30;

    private final AtomicInteger allCounter = new AtomicInteger(0);
    private final AtomicInteger successfulUpdatesCounter = new AtomicInteger(0);
    private final AtomicInteger warningUpdatesCounter = new AtomicInteger(0);

    @ChangeSet(order = "001", id = "Sync script Payload decompress on workspace.private", author = "administrator", runAlways = true)
    public void syncPayloadDecodeOnStage(MongockTemplate mongockTemplate) {
        log.info("Start sync Payload decode script to Database");

        var query = getWorkspaceQuery();
        List<Workspace> workspaces = mongockTemplate.find(query, Workspace.class);

        while (!workspaces.isEmpty()) {
            workspaces.stream()
                    .map(this::setWorkspaceUpdate)
                    .forEach(workspace -> {
                        var criteriaUpdate = where("_id").is(workspace.getId());
                        Update chartUpdate = new Update()
                                .set("charts", workspace.getCharts())
                                .set("updatedAt", workspace.getUpdatedAt());
                        mongockTemplate.findAndModify(new Query(criteriaUpdate), chartUpdate, Workspace.class);
                    });

            workspaces = mongockTemplate.find(query, Workspace.class);
        }

        log.info("Found by criteria of sync = {} chart(s)", allCounter);
        if (successfulUpdatesCounter.get() != 0)
            log.info("Successful sync updated = {} chart(s)", successfulUpdatesCounter);
        if (warningUpdatesCounter.get() != 0)
            log.warn("Error sync update = {} chart(s)", warningUpdatesCounter);
        log.info("Finish sync Payload decode script to Database");
    }

    private Query getWorkspaceQuery() {
        var ltExpirationDate = now().minusSeconds(UPDATE_EXPIRATION_SECONDS).toEpochMilli();

        var nullExpirationDateCriteria = where("updatedAt").is(null);
        var ltExpirationDateCriteria = where("updatedAt").lt(ltExpirationDate);
        var query = new Query(new Criteria().orOperator(nullExpirationDateCriteria, ltExpirationDateCriteria));

        query.fields().include("_id", "payload");
        query.limit(PER_QUERY_LIMIT);

        return query;
    }

    private Workspace setWorkspaceUpdate(Workspace workspace) {
        allCounter.getAndIncrement();
        workspace.setUpdatedAt(now().toEpochMilli());
        try {
            var json = GzipUtil.toDecompress(workspace.getPayload(), UTF_8);
            var charts = WorkspaceConvertor2.toCharts(json);
            workspace.setCharts(charts);
            successfulUpdatesCounter.getAndIncrement();
        } catch (Exception ex) {
            ex.printStackTrace();
            warningUpdatesCounter.getAndIncrement();
        }

        return workspace;
    }
}
