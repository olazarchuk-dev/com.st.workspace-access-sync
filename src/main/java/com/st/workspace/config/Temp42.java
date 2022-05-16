package com.st.workspace.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.st.workspace.entity.Workspace;
import com.st.workspace.utils.GzipUtil;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.st.workspace.utils.GzipUtil.UTF_8;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.String.valueOf;

/**
 * @see https://localcoder.org/deserializing-jackson-dynamic-key-value
 *      https://www.baeldung.com/java-map-key-from-value
 */
public class Temp42 {

    static ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().build();

    public static void main(String[] args) throws IOException {
        var payload = "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";
//        var payload = "H4sIAAAAAAAA/4yMPW+zQBCE/wqa+nhfSAg229lyvqqgSInSLtwSn7Qc1t1h2bL47xFdykw1M3r03FA2tqiqTZ33DZd5UTDn3f1dmQ+y3Qy1fWhqW4NucBb0N9jAeSsXULm2mMI8ik+ro5/GE/srCPvgkovHrJ1mb7Nz/Jft5pgCq2OfHSZVDjCQS39k/y0gPL29P37BIF7HblIQnvft/93HAYuBi61yLy+TWgmggTXK+n666DoVUAqzGMTESUB+VjVIMp7093ajvPok4cwK2tZVsWZZfgIAAP//aD14ESEBAAA=";
        var decompressPayload = GzipUtil.toDecompress(payload, UTF_8);

        Map workspaces = objectMapper.readValue(decompressPayload, Map.class);

        List<Workspace.Chart> charts = workspaces.keySet().stream()
                .map(objWorkspace -> {
                    var workspaceItems = (Map<String, Object>) workspaces.get(objWorkspace);
                    return toChart(workspaceItems);
                }).toList();
        System.out.println(objectMapper.writeValueAsString(charts));
    }

    static Workspace.Chart toChart(Map<String, Object> workspaceItems) {
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

    static String toString(Object obj) {
        return valueOf(obj);
    }

    static Long toLong(Object obj) {
        return  (obj != null)
                ? parseLong(obj.toString())
                : null;
    }

    static Boolean toBoolean(Object obj) {
        return  (obj != null)
                ? parseBoolean(obj.toString())
                : null;
    }

    static Integer toInteger(Object obj) {
        return  (obj != null)
                ? parseInt(obj.toString())
                : null;
    }

    static Workspace.Chart.Instrument toInstrument(Map<String, String> instrumentItems) {
        return (instrumentItems!=null && instrumentItems.isEmpty())
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
}
