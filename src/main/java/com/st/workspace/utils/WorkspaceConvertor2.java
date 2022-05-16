package com.st.workspace.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.st.workspace.entity.Workspace;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.String.valueOf;

public class WorkspaceConvertor2 {
    private static ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().build();

    public static List<Workspace.Chart> toCharts(String json) throws IOException {
        var workspace = objectMapper.readValue(json, Map.class);
        var charts = (Map<String, Object>) workspace.get("charts");

        return (charts!=null)
                ? charts.keySet().stream()
                     .map(obj -> toChart((Map<String, Object>) charts.get(obj))).toList()
                : new ArrayList<>();

//        List<Workspace.Chart> chartList = new ArrayList<>();
//        if (charts!=null) {
//            for (Object objChart : charts.keySet()) {
//                Map<String, Object> chartItems = (Map<String, Object>) charts.get(objChart);
//                Workspace.Chart chart = toChart(chartItems);
//                System.out.println(chart);
//                chartList.add(chart);
//            }
//        }
//        return chartList;
    }

    private static Workspace.Chart toChart(Map<String, Object> chartItems) {
        var instrumentItems = (Map<String, String>) chartItems.get("instrument");

        return Workspace.Chart.builder()
                .id(
                        toString(chartItems.get("id")))
                .timeInterval(
                        toLong(chartItems.get("timeInterval")))
                .isVisible(
                        toBoolean(chartItems.get("isVisible")))
                .index(
                        toInteger(chartItems.get("index")))
                .isPlaceHolder(
                        toBoolean(chartItems.get("isPlaceHolder")))
                .symbol(
                        toString(chartItems.get("symbol")))
                .timestamp(
                        toLong(chartItems.get("timestamp")))
                .barType(
                        toString(chartItems.get("barType")))
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
