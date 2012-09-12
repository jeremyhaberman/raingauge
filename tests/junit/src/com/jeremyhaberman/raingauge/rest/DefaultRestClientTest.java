
package com.jeremyhaberman.raingauge.rest;

import android.content.Context;
import android.os.Environment;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.Suppress;
import com.jeremyhaberman.raingauge.test.NanoHTTPD;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DefaultRestClientTest extends InstrumentationTestCase {

    private static final int PORT = 4242;
    private NanoHTTPD mNano;
    private File mWwwRoot;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mWwwRoot = setUpWWWRoot();
        mNano = new NanoHTTPD(PORT, mWwwRoot);
    }

    @Override
    protected void tearDown() throws Exception {
        mNano.stop();
        deleteWwwRoot();
        super.tearDown();
    }

    @MediumTest
    public void testExecuteShouldThrowExceptionOnNullRequest() {
        RestClient client = DefaultRestClient.newInstance();
        try {
            client.execute(null);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @MediumTest
    public void testRequestHeaders() {
        URI uri = buildURI("localhost", 4242, "/observations.json");

        Request request = new Request(uri);
        List<String> contentTypeValue = new ArrayList<String>();
        String expectedValue = "application/json";
        contentTypeValue.add(expectedValue);
        request.addHeader("Content-Type", contentTypeValue);

        RestClient client = DefaultRestClient.newInstance();
        Response response = client.execute(request);
        assertNotNull(response);
        System.out.println("Response: " + response);
        Properties header = mNano.getLastRequestHeader();
        assertNotNull(header);
        String actualValue = (String) header.get("content-type");
        assertEquals(expectedValue, actualValue);

    }

    @MediumTest
    public void testExecuteShouldReturn200() throws Exception {

        URI uri = buildURI("localhost", 4242, "/observations.json");

        Request request = new Request(uri);

        RestClient client = DefaultRestClient.newInstance();
        Response response = client.execute(request);
        assertNotNull(response);
        System.out.println("Response: " + response);

        InputStream in = getInstrumentation().getContext().getAssets().open("observations.json");
        byte[] expectedBody = IOUtils.toByteArray(in);

        assertEquals(200, response.getStatus());
        assertTrue(Arrays.equals(expectedBody, response.getBody()));
    }

    @MediumTest
    public void test500Response() throws IOException {
        mNano.stop();

        // initializing NanoHTTPD with an invalid directory will cause it to
        // return 500
        NanoHTTPD badNano = new NanoHTTPD(PORT, new File("/invaliddirectory"));

        try {

            URI uri = buildURI("localhost", 4242, "/observations.json");

            Request request = new Request(uri);

            RestClient client = DefaultRestClient.newInstance();
            Response response = client.execute(request);
            assertNotNull(response);
            System.out.println("Response: " + response);

            assertEquals(500, response.getStatus());
            assertNotNull(response.getBody());

        } finally {
            badNano.stop();
        }
    }

    @MediumTest
    public void testIOException() throws IOException {
        mNano.stop();

        // DefaultRestClient should encounter an IOException while making the
        // request because the web server is not running

        URI uri = buildURI("localhost", 4242, "/observations.json");

        Request request = new Request(uri);

        RestClient client = DefaultRestClient.newInstance();
        Response response = client.execute(request);
        assertNotNull(response);
        System.out.println("Response: " + response);

        assertEquals(RestClient.STATUS_CODE_IO_ERROR, response.getStatus());
        assertTrue(response.getBody().length == 0);

    }

    private URI buildURI(String host, int port, String path) {
        return URI.create("http://" + host + ":" + port + path);
    }

    @SuppressWarnings("unused")
    private class MockURLConnection extends HttpURLConnection {

        private int mResponseCode;
        private Map<String, List<String>> mResponseHeaders;
        private byte[] mResponseBody;

        protected MockURLConnection(URL url) {
            super(url);
        }

        public MockURLConnection(URL url, int responseCode,
                Map<String, List<String>> responseHeaders,
                byte[] responseBody) {
            super(url);
            mResponseCode = responseCode;
            mResponseHeaders = responseHeaders;
            mResponseBody = responseBody;
        }

        @Override
        public void disconnect() {
        }

        @Override
        public boolean usingProxy() {
            return false;
        }

        @Override
        public void connect() throws IOException {

        }

        @Override
        public int getResponseCode() throws IOException {
            return mResponseCode;
        }

        @Override
        public int getContentLength() {
            if (mResponseBody == null) {
                return 0;
            } else {
                return mResponseBody.length;
            }
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(mResponseBody);
        }
    }

    private File setUpWWWRoot() throws IOException {
        Context context = getInstrumentation().getContext();

        InputStream in = context.getAssets().open("observations.json");
        File externalStorage = Environment.getExternalStorageDirectory();

        File wwwRoot = new File(externalStorage, "wwwroot");
        wwwRoot.mkdir();

        File observations = new File(wwwRoot, "observations.json");
        if (observations.exists()) {
            observations.delete();
        }

        OutputStream out =
                new FileOutputStream(observations.getAbsolutePath());

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();

        return wwwRoot;
    }

    private void deleteWwwRoot() {
        String[] children = mWwwRoot.list();
        for (int i = 0; i < children.length; i++) {
            new File(mWwwRoot, children[i]).delete();
        }
        mWwwRoot.delete();
    }
}
