package com.st.workspace.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.st.workspace.entity.Workspace;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.String.valueOf;

public class WorkspaceConvertor {
    private static ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().build();

    public static List<Workspace.Chart> toCharts(String json) throws IOException {
        var workspaces = objectMapper.readValue(json, Map.class);

        return workspaces.keySet().stream()
                .map(obj -> toChart((Map<String, Object>) workspaces.get(obj))).toList();
    }

    private static Workspace.Chart toChart(Map<String, Object> workspaceItems) {
        var instrumentItems = (Map<String, String>) workspaceItems.get("instrument");

        return Workspace.Chart.builder()
                .id(
                        toString(workspaceItems.get("id")))
                .timeInterval(
                        toLong(workspaceItems.get("timeInterval")))
                .isVisible(
                        toBoolean(workspaceItems.get("isVisible")))
                .index(
                        toInteger(workspaceItems.get("index")))
                .isPlaceHolder(
                        toBoolean(workspaceItems.get("isPlaceHolder")))
                .symbol(
                        toString(workspaceItems.get("symbol")))
                .timestamp(
                        toLong(workspaceItems.get("timestamp")))
                .barType(
                        toString(workspaceItems.get("barType")))
                .instrument(
                        toInstrument(instrumentItems))
                .build();
    }

    private static Workspace.Chart.Instrument toInstrument(Map<String, String> instrumentItems) {
        return (instrumentItems!=null && !instrumentItems.isEmpty())
                ? Workspace.Chart.Instrument.builder()
                .symbol(
                        toString(instrumentItems.get("symbol")))
                .company(
                        toString(instrumentItems.get("company")))
                .exchange(
                        toString(instrumentItems.get("exchange")))
                .build()
                : null;
    }

    private static String toString(Object obj) {
        return valueOf(obj);
    }

    private static Long toLong(Object obj) {
        return  (obj != null)
                ? parseLong(obj.toString())
                : null;
    }

    private static Boolean toBoolean(Object obj) {
        return  (obj != null)
                ? parseBoolean(obj.toString())
                : null;
    }

    private static Integer toInteger(Object obj) {
        return  (obj != null)
                ? parseInt(obj.toString())
                : null;
    }
}
