
package com.jeremyhaberman.raingauge.rest;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jeremyhaberman.raingauge.util.Logger;

public class DefaultRestClient implements RestClient {

    private static final String TAG = DefaultRestClient.class.getSimpleName();

    // Timeout while reading input stream from resource
    private static final int READ_TIMEOUT = 30000;

    // Timeout for establishing connection to resource
    private static final int CONNECT_TIMEOUT = 15000;

    private DefaultRestClient() {
        System.setProperty("http.keepAlive", "false");
    }

    public static RestClient newInstance() {
        return new DefaultRestClient();
    }

    @Override
    public Response execute(Request request) {

        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }

        HttpURLConnection conn = null;
        Response response = null;
        int responseCode = -1;
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        byte[] body = new byte[] {};

        try {

            URL url = request.getUri().toURL();
            conn = (HttpURLConnection) url.openConnection();
            if (request.getHeaders() != null) {
                for (String header : request.getHeaders().keySet()) {
                    for (String value : request.getHeaders().get(header)) {
                        conn.addRequestProperty(header, value);
                    }
                }
            }

            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);

            conn.setDoOutput(false);

            BufferedInputStream in = null;
            try {
                in = new BufferedInputStream(conn.getInputStream());
            } catch (IOException e) {
                in = new BufferedInputStream(conn.getErrorStream());
            }
            body = readStream(in);

            responseCode = conn.getResponseCode();
            headers = conn.getHeaderFields();

        } catch (IOException e) {
            if (Logger.isEnabled(Logger.DEBUG)) {
                Logger.error(TAG, "Error executing request", e);
            }
            responseCode = RestClient.STATUS_CODE_IO_ERROR;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        response = new Response(responseCode, headers, body);

        return response;
    }

    private static byte[] readStream(InputStream in) throws IOException {
        byte[] buf = new byte[1024];
        int count = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        while ((count = in.read(buf)) != -1)
            out.write(buf, 0, count);
        return out.toByteArray();
    }
}
