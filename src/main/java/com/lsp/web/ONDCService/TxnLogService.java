package com.lsp.web.ONDCService;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TxnLogService {

    private static final String URL = "https://analytics-api-pre-prod.aws.ondc.org/v1/api/push-txn-logs";

    private static final String AUTH_TOKEN =
            "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1YXQuZ2V0cnlzYS5jb21AYnV5ZXIiLCJleHAiOjE4MjI5MDAwMDAsImZyZXNoIjpmYWxzZSwiaWF0IjoxNjU5MTUxOTU2LCJqdGkiOiJlZjhiMWJiODA4MWE0NTU2OGE0MjdkZjU4YzkyM2VjMiIsIm5iZiI6MTY1OTE1MTk1NiwidHlwZSI6ImFjY2VzcyIsImVtYWlsIjoidGVjaEBvbmRjLm9yZyIsInB1cnBvc2UiOiJkYXRhc2hhcmluZyIsInBob25lX251bWJlciI6bnVsbCwicm9sZXMiOlsiYWRtaW5pc3RyYXRvciJdLCJmaXJzdF9uYW1lIjoibmV0d29yayIsImxhc3RfbmFtZSI6Im9ic2VydmFiaWxpdHkifQ.VtfgescvxOVI43o4paD7hSj4QM2HJgXwYGq2uNjQ0Hs";

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Push transaction logs to ONDC Analytics API
     *
     * @param type the API type (e.g. "confirm", "on_confirm", "search")
     * @param data the JSON body (payload) to send
     * @return Response body as String
     */
    public String pushTxnLogs(String type, Object data) {
        // Wrap into required format { "type": "xxx", "data": { ... } }
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", type);
        payload.put("data", data);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", AUTH_TOKEN);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(URL, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }
}
