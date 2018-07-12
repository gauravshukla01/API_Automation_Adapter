package org.cts.hybrid;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.cognizant.framework.Status;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import businesscomponents.constants.WebServiceConstants;
import businesscomponents.helpers.HelperMethods;
import businesscomponents.helpers.JsonCreateRequestHelper;
import businesscomponents.helpers.JsonParseResponseHelper;
import supportlibraries.ReusableLibrary;
import supportlibraries.ScriptHelper;

public class JsonRequestResponseParser extends ReusableLibrary {

	public JsonRequestResponseParser(ScriptHelper scriptHelper) {
		super(scriptHelper);
	}

//	public void createJsonRequest() {
//		String payload = JsonCreateRequestHelper.getPayloadAsString(dataTable,
//				dataTable.getData(WebServiceConstants.INPUTDATASHEET, WebServiceConstants.PAYLOAD));
//
//		System.out.println(payload);
//	}

//	public void parseJsonResponse() {
//		String payloadName = dataTable.getData(WebServiceConstants.INPUTDATASHEET, WebServiceConstants.PAYLOAD);
//
//		String payload = JsonCreateRequestHelper.getPayloadAsString(dataTable, payloadName);
//
//		if (!(payloadName.equalsIgnoreCase("SimpleObject") || payloadName.equalsIgnoreCase("ObjectWithArray")
//				|| payloadName.equalsIgnoreCase("ObjectWithObjectArray"))) {
//			payload = "{\"" + payloadName + "\":" + payload + "}";
//		}
//		report.updateTestLog("payload", payload, Status.DONE);
//
//		try {
//			JsonFactory factory = new JsonFactory();
//			ObjectMapper mapper = new ObjectMapper(factory);
//			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
//			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//			HashMap<String, Object> map = (HashMap<String, Object>) HelperMethods.createMapFromJsonResponse(payload,
//					mapper);
//			JsonParseResponseHelper.validatePaylod(map.get(payloadName), mapper, report, dataTable, payloadName);
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	public static void main(String[] args) {
		// String endPoint = "/accountloans/{loanAcctNumber}/fees/{assdd}";
		// StringBuilder value = new StringBuilder(endPoint);
		// while(endPoint.contains("{")){
		// int beingInd = endPoint.indexOf("{");
		// int endInd = endPoint.indexOf("}");
		// endPoint = endPoint.substring(beingInd+1, endInd);
		// value.replace(beingInd, endInd+1, "12345");
		// endPoint = value.toString();
		// System.out.println(endPoint);
		// System.out.println(value);
		// }

		// StringBuilder param = new StringBuilder("asdb/");
		// System.out.println(param.substring(0, param.length()-1));

		/** TOKEN TEST **/
		addAuthTokenAndGuardTokenToHeaders();
	}

	public static void addAuthTokenAndGuardTokenToHeaders() {
		HashMap<String, String> responseMap;
		String respStr = "{\"payload\":{\"tokens\":[{\"token\":\"eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJCYWphakZpblNlcnYiLCJleHAiOjE1MTEyNzEwMDYsImlhdCI6MTQ5NTcxOTAwNiwic3ViIjoiYXV0aGVudGljYXRpb24iLCJsb2dpbiI6InNheWFsaS5zaGFoQGNvZ25pemFudC5jb20iLCJ1c2VyVHlwZSI6Mn0.stx8DWHpYKH7ROUaFOKOwL5U4CINc3l367rdu1TXSCg\",\"guardKey\":\"2JzyMm2gIptU\",\"type\":\"authtoken\"}]},\"status\":\"SUCCESS\",\"errorBean\":null}";
		System.out.println(respStr);
//			JsonFactory factory = new JsonFactory();
//			ObjectMapper mapper = new ObjectMapper(factory);
//			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
//			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//			Map<String,Object> resp = HelperMethods.createMapFromJsonResponse(respStr, mapper);
//			Map<String,Object> payload = (Map<String,Object>)resp.get("payload");
//			ArrayList<Object> tokens = (ArrayList<Object>)payload.get("tokens");
//			
//			System.out.println(tokens.get(0));
		
		 JSONObject jsonObj = new JSONObject(respStr);
		 JSONObject paylodObj = jsonObj.getJSONObject("payload");
		 JSONArray tokens = paylodObj.getJSONArray("tokens");
		 JSONObject tokenObj = tokens.getJSONObject(0);
		 String authToken = tokenObj.getString("token");
		 String guardKey = tokenObj.getString("guardKey");
		 System.out.println("\n authToken = " + authToken);
		 System.out.println("\n guardKey = " + guardKey);
		try {
			String guardToken = Base64.getEncoder().encodeToString(("B!&1j|123456|"+guardKey).getBytes("utf-8"));
			System.out.println(guardToken);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 

	}

}
