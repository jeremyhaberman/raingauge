package com.jeremyhaberman.raingauge.rest.method;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import com.jeremyhaberman.raingauge.R;
import com.jeremyhaberman.raingauge.mock.MockRestClient;
import com.jeremyhaberman.raingauge.rest.Request;
import com.jeremyhaberman.raingauge.rest.resource.Observations;
import com.jeremyhaberman.raingauge.util.TestUtil;

import java.net.URI;

public class GetObservationsRestMethodTest extends InstrumentationTestCase {

	private static final String TAG = GetObservationsRestMethod.class.getSimpleName();
	private String mApiKey;
	private static final String ZIP = "55408";
	private GetObservationsRestMethod mMethod;
	private MockRestClient mRestClient;

	protected void setUp() throws Exception {
		super.setUp();

		mMethod =
				GetObservationsRestMethod.newInstance(getInstrumentation().getTargetContext(), ZIP);
		mRestClient = MockRestClient.newInstance(getInstrumentation().getContext());
		mMethod.setRestClient(mRestClient);

		mApiKey = getInstrumentation().getTargetContext().getString(R.string.api_key);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@SmallTest
	public void testPreconditions() {
		assertNotNull(mMethod);
		assertNotNull(mRestClient);
	}

	@LargeTest
	public void testExecute() {
		RestMethod<Observations> method =
				GetObservationsRestMethod.newInstance(getInstrumentation().getTargetContext(),
						Integer.toString(55408));
		RestMethodResult<Observations> result = method.execute();
		assertEquals(200, result.getStatusCode());
		Observations observations = result.getResource();
		assertNotNull(observations);
		double rainfall = observations.getRainfall();
	}

	@SmallTest
	public void testGetURI() {

		String zip = "55408";
		String apiKey = getInstrumentation().getTargetContext().getString(R.string.api_key);

		GetObservationsRestMethod method =
				GetObservationsRestMethod.newInstance(getInstrumentation().getTargetContext(), zip);

		String expectedUri =
				"http://i.wxbug.net/REST/Direct/GetObs.ashx?zip=" + zip + "&units=0&ic=1&api_key=" +
						apiKey;

		assertEquals(URI.create(expectedUri).toString(), method.getURI().toString());
	}

	@SmallTest
	public void testGetLogTag() {
		GetObservationsRestMethod method =
				GetObservationsRestMethod
						.newInstance(getInstrumentation().getTargetContext(), "55408");
		assertEquals("GetObservationsRestMethod", method.getLogTag());
	}

	public void testBuildRequest() {
		String zip = "55408";
		GetObservationsRestMethod method =
				GetObservationsRestMethod.newInstance(getInstrumentation().getTargetContext(), zip);
		Request request = method.buildRequest();

		URI expectedURI = URI.create(
				"http://i.wxbug.net/REST/Direct/GetObs.ashx?zip=" + zip + "&units=0&ic=1&api_key=" +
						mApiKey);

		assertEquals(expectedURI.toString(), method.getURI().toString());
	}

	@SmallTest
	public void testNewInstance() {
		RestMethod<Observations> method =
				GetObservationsRestMethod.newInstance(getInstrumentation().getTargetContext(),
						Integer.toString(55408));
		assertNotNull(method);
	}

	@MediumTest
	public void testParseResponseBodyString() throws Exception {

		String responseBody = TestUtil.getJson(getInstrumentation().getContext(),
				com.jeremyhaberman.raingauge.tests.R.raw.get_observations_200);

		Observations actualObservations = mMethod.parseResponseBody(responseBody);

		Observations expectedObservations = Observations.createObservations(2.1);

		assertEquals(expectedObservations, actualObservations);
	}
}
