package org.cts.hybrid;



import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import businesscomponents.constants.WebServiceConstants;

/**
 * @author sayali.shah
 *
 */
public class RESTClient {

	private RESTClient() {

	}

	public static Map<String, String> getWebServiceResponseForGET(String url) throws IOException {
		return getWebServiceResponseForGET(url, null);
	}

	/**
	 * Use this method to call REST service using GET method
	 * 
	 * @param url
	 * @param headerAttrs
	 * @return Map with value of gateway status, response, execution time
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, String> getWebServiceResponseForGET(String url, Map<String, String> headerAttrs)
			throws IOException {

		Client client = Client.create();
		WebResource webResource = client.resource(url);
		WebResource.Builder builder = webResource.accept(WebServiceConstants.JSON);
		// Addding headers to request
		addHeaders(builder, (HashMap) headerAttrs);
		long a = System.nanoTime();
		// Making API call
		ClientResponse response = builder.get(ClientResponse.class);
		long b = System.nanoTime() - a;
		HashMap<String, String> responseMap = new HashMap<>();
		responseMap.put(WebServiceConstants.GATEWAYSTATUS, String.valueOf(response.getStatus()));
		responseMap.put(WebServiceConstants.RESPONSE_STRING,
				response.hasEntity() ? response.getEntity(String.class) : "");
		responseMap.put(WebServiceConstants.EXECUTION_TIME, String.valueOf(b / 1000000));
		return responseMap;
	}

	/**
	 * Use this method to call REST service with POST method
	 * 
	 * @param url
	 * @param requestPaylod
	 * @return Map with value of gateway status, response, execution time
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, String> getWebServiceResponseForPOST(String url, String requestPaylod, Map<String, String> headerAttrs)
			throws IOException {

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();

		try {

			HttpPost request = new HttpPost(url);
			request.addHeader(WebServiceConstants.CONTENTTYPE, WebServiceConstants.JSON);
			addHeaders(request, (HashMap) headerAttrs);
			if (requestPaylod == null) {
				request.setEntity(null);
			}else if(!requestPaylod.isEmpty()){
				StringEntity params = new StringEntity(requestPaylod);
				request.setEntity(params);				
			}
			long a = System.nanoTime();
			CloseableHttpResponse response = httpClient.execute(request);
			long b = System.nanoTime();
			HashMap<String, String> responseMap = new HashMap<>();
			responseMap.put(WebServiceConstants.GATEWAYSTATUS,
					String.valueOf(response.getStatusLine().getStatusCode()));

			HttpEntity respEntity = response.getEntity();
			responseMap.put(WebServiceConstants.RESPONSE_STRING,
					respEntity != null ? EntityUtils.toString(respEntity) : "");
			responseMap.put(WebServiceConstants.EXECUTION_TIME, String.valueOf((b - a) / 1000000));
			responseMap.put(WebServiceConstants.PAYLOAD, requestPaylod);
			return responseMap;
		} finally {
			httpClient.close();
		}
	}

	public static Map<String, String> getWebServiceResponseForPUT(String url, String requestPaylod) throws IOException {
		return getWebServiceResponseForPUT(url,requestPaylod,null);
	}

	/**
	 * Use this method to call REST service with PUT method
	 * 
	 * @param url
	 * @param headerAttrs
	 * @param requestPaylod
	 * @return Map with value of gateway status, response, execution time
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, String> getWebServiceResponseForPUT(String url, String requestPaylod,Map<String, String> headerAttrs) throws IOException {

		Client client = Client.create();
		WebResource webResource = client.resource(url);
		WebResource.Builder builder = webResource.accept(WebServiceConstants.JSON);
		builder.header(WebServiceConstants.CONTENTTYPE, WebServiceConstants.JSON);
		HashMap<String, String> responseMap = new HashMap<>();
		addHeaders(builder, (HashMap) headerAttrs);
		if(requestPaylod == null || !requestPaylod.isEmpty()){
			long a = System.nanoTime();
			ClientResponse response = builder.put(ClientResponse.class, requestPaylod);
			long b = System.nanoTime() - a;
			responseMap.put(WebServiceConstants.GATEWAYSTATUS, String.valueOf(response.getStatus()));
			responseMap.put(WebServiceConstants.RESPONSE_STRING,
					response.hasEntity() ? response.getEntity(String.class) : "");
			responseMap.put(WebServiceConstants.EXECUTION_TIME, String.valueOf(b / 1000000));
		}else{
			long a = System.nanoTime();
			ClientResponse response = builder.put(ClientResponse.class);
			long b = System.nanoTime() - a;
			responseMap.put(WebServiceConstants.GATEWAYSTATUS, String.valueOf(response.getStatus()));
			responseMap.put(WebServiceConstants.RESPONSE_STRING,
					response.hasEntity() ? response.getEntity(String.class) : "");
			responseMap.put(WebServiceConstants.EXECUTION_TIME, String.valueOf(b / 1000000));
			responseMap.put(WebServiceConstants.PAYLOAD, requestPaylod);
		}
		
		return responseMap;
	}

	private static void addHeaders(WebResource.Builder builder, HashMap<String, String> headerAttrs) {
		if (headerAttrs != null && !headerAttrs.isEmpty()) {
			Iterator<String> keySet = headerAttrs.keySet().iterator();
			while (keySet.hasNext()) {
				String key = keySet.next();
				builder.header((key != null) ? key : "", headerAttrs.get(key));
			}
		}
	}
	
	private static void addHeaders(HttpPost request, HashMap<String, String> headerAttrs) {
		if (headerAttrs != null && !headerAttrs.isEmpty()) {
			Iterator<String> keySet = headerAttrs.keySet().iterator();
			while (keySet.hasNext()) {
				String key = keySet.next();
				request.addHeader((key != null) ? key : "", headerAttrs.get(key));
			}
		}
		
	}
}
