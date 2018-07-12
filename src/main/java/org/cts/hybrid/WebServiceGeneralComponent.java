package org.cts.hybrid;


import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.json.JSONArray;
import org.json.JSONObject;

import com.cognizant.framework.FrameworkException;
import com.cognizant.framework.Status;
import com.cognizant.framework.Util;
import com.cognizant.framework.selenium.SeleniumTestParameters;

import businesscomponents.constants.WebServiceConstants;
import businesscomponents.helpers.ExcelReadWriteHelper;
import businesscomponents.helpers.HelperMethods;
import businesscomponents.helpers.ResponseValidationHelper;
import supportlibraries.ReusableLibrary;
import supportlibraries.ScriptHelper;

/**
 * @author testinguser2
 *
 */
public class WebServiceGeneralComponent extends ReusableLibrary {

	private String webServicePort = dataTable
			.getData(WebServiceConstants.INPUTDATASHEET, WebServiceConstants.WEBSERVICEPORT).trim();

	private String webServiceUrlPoint = dataTable
			.getData(WebServiceConstants.INPUTDATASHEET, WebServiceConstants.WEBSERVICEURL).trim()
			+ (webServicePort.isEmpty() ? "/" : ":" + webServicePort + "/");
	private String webServiceEndPoint = dataTable
			.getData(WebServiceConstants.INPUTDATASHEET, WebServiceConstants.WEBSERVICEENDPOINT).trim();

	private boolean addSecurityHeader = "TRUE".equalsIgnoreCase(
			dataTable.getData(WebServiceConstants.INPUTDATASHEET, WebServiceConstants.ADD_SECURITY_HEADER)) ? true
					: false;
	String headerExtra = dataTable.getData(WebServiceConstants.INPUTDATASHEET, WebServiceConstants.EXTRA_HEADER);

	private SeleniumTestParameters stp = report.getTestParameters();
	private static final Logger LOGGER = Logger.getLogger(WebServiceGeneralComponent.class.getName());

	private ResponseValidationHelper responseValidator = ResponseValidationHelper.getInstance();

	private static ExcelReadWriteHelper excelReadWriteHelper = ExcelReadWriteHelper.getInstance();

	private String dataTablePath = dataTable.getDataTablePath();

	private String dbSheetName = properties.getProperty("DB_SheetName");
	private String environment = stp.getEnvironment();
	private String testRunStatus = "UNKNOWN";

	public WebServiceGeneralComponent(ScriptHelper scriptHelper) {
		super(scriptHelper);
		// Add code here if needed
	}

	/**
	 * Use this method to validate web service response when valid payload is
	 * passed for type POST
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void validateResponseForValidPayloadPOST() {
		HashMap<String, String> responseMap;
		try {
			// building URL and getting correct payload
			String[] values = buildWebServiceURL();
			// updating log with Web Service URL details and request payload
			// details
			report.updateTestLog(WebServiceConstants.TEST_RUN_INFO,
					WebServiceConstants.URL + values[0] + WebServiceConstants.PAYLOAD_VALUE + values[1], Status.DONE);
			Map<String, String> headerMap = getAllHeaders();
			// Making REST API call
			responseMap = (HashMap) RESTClient.getWebServiceResponseForPOST(values[0], values[1],
					(headerMap != null && !headerMap.isEmpty()) ? headerMap : null);
			// Storing gate way status and response in variables
			String gatewayStatus = responseMap.get(WebServiceConstants.GATEWAYSTATUS);
			String respStr = responseMap.get(WebServiceConstants.RESPONSE_STRING);
			// adding REST API call details to payload excel
			addDetailsOfPayloadToExcel(responseMap, values[0], values[1], "POST");
			// updating log with Web Service call status and response payload
			// details
			report.updateTestLog(WebServiceConstants.TEST_RUN_INFO,
					WebServiceConstants.GATEWAY_STATUS + gatewayStatus + WebServiceConstants.RESPONSE_RCVD + respStr,
					Status.DONE);
			// validating response
			testRunStatus = responseValidator.validateResponse(gatewayStatus, respStr, dataTable, report, stp.getCurrentTestcase(),
					stp.getEnvironment());
			updateStatusToPayloadExcel();

		} catch (IOException e) {
			frameworkParameters.setStopExecution(false);
			testRunStatus = "FAILED";
			report.updateTestLog(stp.getCurrentTestcase(), WebServiceConstants.UNEXPECTED_ERROR + e.getMessage(),
					Status.FAIL);
			LOGGER.log(Level.ALL, e.getMessage());
		}

	}

	/**
	 * Use this method to validate web service response when valid parameters
	 * passed for type GET
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void validateResponseForValidInputGET() {
		HashMap<String, String> responseMap;
		try {
			// building URL and getting correct payload
			String values[] = buildWebServiceURL();
			// updating log with Web Service URL details and request payload
			// details
			report.updateTestLog(WebServiceConstants.TEST_RUN_INFO,
					WebServiceConstants.URL + values[0] + WebServiceConstants.PAYLOAD_VALUE + values[1], Status.DONE);
			Map<String, String> headerMap = getAllHeaders();
			// Making REST API call
			responseMap = (HashMap) RESTClient.getWebServiceResponseForGET(values[0],
					(headerMap != null && !headerMap.isEmpty()) ? headerMap : null);

			// Storing gatewaystatus and response in variables
			String gatewayStatus = responseMap.get(WebServiceConstants.GATEWAYSTATUS);
			String respStr = responseMap.get(WebServiceConstants.RESPONSE_STRING);
			// adding REST API call details to payload excel
			addDetailsOfPayloadToExcel(responseMap, values[0], values[1], "GET");
			// updating log with Web Service call status and response payload
			// details
			report.updateTestLog(WebServiceConstants.TEST_RUN_INFO,
					WebServiceConstants.GATEWAY_STATUS + gatewayStatus + WebServiceConstants.RESPONSE_RCVD + respStr,
					Status.DONE);
			// validating response
			testRunStatus = responseValidator.validateResponse(gatewayStatus, respStr, dataTable, report, stp.getCurrentTestcase(),
					stp.getEnvironment());
			updateStatusToPayloadExcel();
		} catch (IOException e) {
			frameworkParameters.setStopExecution(false);
			testRunStatus = "FAILED";
			report.updateTestLog(stp.getCurrentTestcase(), WebServiceConstants.UNEXPECTED_ERROR + e.getMessage(),
					Status.FAIL);
		}
		
	}

	/**
	 * Use this method to validate web service response when valid payload is
	 * passed for type PUT
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void validateResponseForValidPayloadPUT() {
		HashMap<String, String> responseMap;
		try {
			// building URL and getting correct payload
			String[] values = buildWebServiceURL();
			// updating log with Web Service URL details and request payload
			// details
			report.updateTestLog(WebServiceConstants.TEST_RUN_INFO,
					WebServiceConstants.URL + values[0] + WebServiceConstants.PAYLOAD_VALUE + values[1], Status.DONE);
			Map<String, String> headerMap = getAllHeaders();
			// Making REST API call
			responseMap = (HashMap) RESTClient.getWebServiceResponseForPUT(values[0], values[1],
					(headerMap != null && !headerMap.isEmpty()) ? headerMap : null);

			// Storing gatewaystatus and response in variables
			String gatewayStatus = responseMap.get(WebServiceConstants.GATEWAYSTATUS);
			String respStr = responseMap.get(WebServiceConstants.RESPONSE_STRING);
			// adding REST API call details to payload excel
			addDetailsOfPayloadToExcel(responseMap, values[0], values[1], "PUT");
			// updating log with Web Service call status and response payload
			// details
			report.updateTestLog(WebServiceConstants.TEST_RUN_INFO,
					WebServiceConstants.GATEWAY_STATUS + gatewayStatus + WebServiceConstants.RESPONSE_RCVD + respStr,
					Status.DONE);
			// validating response
			testRunStatus = responseValidator.validateResponse(gatewayStatus, respStr, dataTable, report, stp.getCurrentTestcase(),
					stp.getEnvironment());
			updateStatusToPayloadExcel();
		} catch (IOException e) {
			frameworkParameters.setStopExecution(false);
			testRunStatus = "FAILED";
			report.updateTestLog(stp.getCurrentTestcase(), WebServiceConstants.UNEXPECTED_ERROR + e.getMessage(),
					Status.FAIL);
			LOGGER.log(Level.ALL, e.getMessage());
		}

	}

	private String appendWebEndPOintToWebServiceURI(String webServiceUrlPoint, String webServiceEndPoint) {
		StringBuilder webServiceUrl = new StringBuilder(webServiceUrlPoint);
		webServiceUrl.append(webServiceEndPoint);
		return webServiceUrl.toString();
	}

	/*
	 * This method adds values to Service UR and payloads.
	 */
	private String[] buildWebServiceURL() {
		// Getting request file name from general data sheet
		String requestFileName = dataTable.getData(WebServiceConstants.INPUTDATASHEET,
				WebServiceConstants.REQUEST_FILE_NAME);
		// Getting payloadString if requestfilename is not empty
		String payloadAsString = requestFileName.isEmpty() ? ""
				: HelperMethods.readFromTextFile(requestFileName,
						dataTablePath + Util.getFileSeparator() + "api" + Util.getFileSeparator() + "inputrequests",
						report);
		return new String[] {
				// this is Web End point replaces with values
				appendWebEndPOintToWebServiceURI(webServiceUrlPoint,
						payloadAsString.isEmpty() ? webServiceEndPoint
								: HelperMethods.addPayloadValuesToWebServiceEndPoint(webServiceEndPoint,
										HelperMethods.getPayloadValueMapFromPayloadStr(payloadAsString, dataTablePath,
												report, dbSheetName, environment),
										report)),
				// this is payload String with values
				HelperMethods.getPayloadWithValuesAsString(payloadAsString, dataTablePath, report, dbSheetName,
						environment) };
	}

	/**
	 * This method creates Authtoken and Guard key using userId, userType and
	 * loginId with Generated guard key
	 * 
	 * @param headerType
	 * 
	 * @return hash map of authtoken and guardtoken
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private HashMap<String, String> addSecurityHeaders(String headerType) {

		if (headerType == null || headerType.isEmpty()) {
			report.updateTestLog("Add Security Header",
					"Incorrect Security Header type. Please correct value in DB Sheet", Status.FAIL);
			throw new FrameworkException("Security Header Error",
					"Incorrect Security Header type. Please correct value in DB Sheet");
		}
		// Getting header type i.e. row name/ Test case Id in Common Test data for token details
		String addHeaderType = getHeaderType(headerType);
		// Making calls according to token type
		if (WebServiceConstants.ADD_SECURITY_HEADER_B2B.equalsIgnoreCase(addHeaderType)) {
			return getHeadersMapForB2BToken(addHeaderType);
		} else {
			return getHeadersMapForOthers(addHeaderType);
		}
	}

	private HashMap<String, String> getHeadersMapForOthers(String addHeaderType) {

		HashMap<String, String> responseMap;
		HashMap<String, String> headers = new HashMap<>();
		try {
			// Getting Token URL for token creation
			String url = dataTable.getCommonData(WebServiceConstants.TOKEN_URL,
					WebServiceConstants.ADD_SECURITY_HEADER);
			// Building JSON input for token creation
			StringBuilder requestPayload = new StringBuilder("{\"userId\":");
			try {
				// Checking if token details present in Headers tab of datatable
				requestPayload.append(dataTable.getData(WebServiceConstants.HEADERS, WebServiceConstants.USERID));
				requestPayload.append(",\"userType\":")
						.append(dataTable.getData(WebServiceConstants.HEADERS, WebServiceConstants.USERTYPE));
				requestPayload.append(",\"loginId\": \"")
						.append(dataTable.getData(WebServiceConstants.HEADERS, WebServiceConstants.LOGINID))
						.append("\"}");
			} catch (FrameworkException fe) {
				// if not present in headers tab of datatable, geting token
				// values from common test data according to header type
				requestPayload.append(dataTable.getCommonData(WebServiceConstants.USERID, addHeaderType));
				requestPayload.append(",\"userType\":")
						.append(dataTable.getCommonData(WebServiceConstants.USERTYPE, addHeaderType));
				requestPayload.append(",\"loginId\": \"")
						.append(dataTable.getCommonData(WebServiceConstants.LOGINID, addHeaderType)).append("\"}");
			}
			// Making Token creation api call
			responseMap = (HashMap) RESTClient.getWebServiceResponseForPOST(url, requestPayload.toString(), null);
			// Getting AuthToken and Guard Token from response
			String respStr = responseMap.get(WebServiceConstants.RESPONSE_STRING);
			JSONObject jsonObj = new JSONObject(respStr);
			JSONObject paylodObj = jsonObj.getJSONObject(WebServiceConstants.PAYLOAD);
			JSONArray tokens = paylodObj.getJSONArray(WebServiceConstants.TOKENS);
			JSONObject tokenObj = tokens.getJSONObject(0);
			String authToken = tokenObj.getString(WebServiceConstants.TOKEN);
			String guardKey = tokenObj.getString(WebServiceConstants.GUARDKEY);
			String guardToken = Base64.getEncoder()
					.encodeToString(("B!&1j|" + System.currentTimeMillis() + "|" + guardKey).getBytes("utf-8"));
			// Adding same to headers map
			headers.put(WebServiceConstants.AUTHTOKEN, authToken);
			headers.put(WebServiceConstants.GUARDTOKEN, guardToken);
			report.updateTestLog(stp.getCurrentTestcase(), WebServiceConstants.AUTHTOKEN + ":" + authToken + "\n"
					+ WebServiceConstants.GUARDTOKEN + ":" + guardToken, Status.DONE);

		} catch (IOException e) {
			frameworkParameters.setStopExecution(false);
			report.updateTestLog(stp.getCurrentTestcase(), WebServiceConstants.UNEXPECTED_ERROR + e.getMessage(),
					Status.FAIL);
			LOGGER.log(Level.ALL, e.getMessage());
		}
		return headers;
	}

	private HashMap<String, String> getHeadersMapForB2BToken(String addHeaderType) {
		HashMap<String, String> responseMap;
		HashMap<String, String> headers = new HashMap<>();
		try {
			// Getting Token URL for token creation
			String url = dataTable.getCommonData(WebServiceConstants.TOKEN_URL, addHeaderType);
			// Building JSON input for token creation
			StringBuilder requestPayload = new StringBuilder("{\"partnerKey\":\"");
			try {
				requestPayload.append(dataTable.getData(WebServiceConstants.HEADERS, WebServiceConstants.PARTNERKEY));
				requestPayload.append("\",\"secretKey\":\"")
						.append(dataTable.getData(WebServiceConstants.HEADERS, WebServiceConstants.SECRETKEY));
			} catch (FrameworkException fe) {
				requestPayload.append(dataTable.getCommonData(WebServiceConstants.PARTNERKEY, addHeaderType));
				requestPayload.append("\",\"secretKey\":\"")
						.append(dataTable.getCommonData(WebServiceConstants.SECRETKEY, addHeaderType));
			}
			requestPayload.append("\"}");
			// Making Token creationg api call
			responseMap = (HashMap) RESTClient.getWebServiceResponseForPOST(url, requestPayload.toString(), null);
			// Getting AuthToken and Guard Token from response
			String respStr = responseMap.get(WebServiceConstants.RESPONSE_STRING);
			JSONObject jsonObj = new JSONObject(respStr);
			JSONObject paylodObj = jsonObj.getJSONObject(WebServiceConstants.PAYLOAD);
			JSONArray tokens = paylodObj.getJSONArray(WebServiceConstants.TOKENS);
			JSONObject tokenObj = tokens.getJSONObject(0);
			String authToken = tokenObj.getString(WebServiceConstants.TOKEN);
			headers.put(WebServiceConstants.AUTHTOKEN, authToken);
			// String guardKey =
			// tokenObj.getString(WebServiceConstants.GUARDKEY);
			// String guardToken = Base64.getEncoder()
			// .encodeToString(("B!&1j|" + System.currentTimeMillis() + "|" +
			// guardKey).getBytes("utf-8"));
			// Adding same to headers map
			// headers.put(WebServiceConstants.GUARDTOKEN, guardToken);
			report.updateTestLog(stp.getCurrentTestcase(), WebServiceConstants.AUTHTOKEN + ":" + authToken,
					Status.DONE);
		} catch (IOException e) {
			frameworkParameters.setStopExecution(false);
			report.updateTestLog(stp.getCurrentTestcase(), WebServiceConstants.UNEXPECTED_ERROR + e.getMessage(),
					Status.FAIL);
			LOGGER.log(Level.ALL, e.getMessage());
		}
		return headers;

	}

	private String getHeaderType(String headerType) {
		if (WebServiceConstants.SECURITY_HEADER_SYSTEM.equalsIgnoreCase(headerType)) {
			return WebServiceConstants.ADD_SECURITY_HEADER;
		} else if (WebServiceConstants.SECURITY_HEADER_EP.equalsIgnoreCase(headerType)) {
			return WebServiceConstants.ADD_SECURITY_HEADER_EP;
		} else if (WebServiceConstants.SECURITY_HEADER_B2B.equalsIgnoreCase(headerType)) {
			return WebServiceConstants.ADD_SECURITY_HEADER_B2B;
		} else if (WebServiceConstants.SECURITY_HEADER_SECURED.equalsIgnoreCase(headerType)) {
			return WebServiceConstants.ADD_SECURITY_HEADER_SECURED;
		}else if (WebServiceConstants.SECURITY_HEADER_SECURED_EP.equalsIgnoreCase(headerType)) {
			return WebServiceConstants.ADD_SECURITY_HEADER_SECURED_EP;
		} 
		else {
			return WebServiceConstants.ADD_SECURITY_HEADER_CP;
		}
	}

	public void addDetailsOfPayloadToExcel(Map<String, String> responseMap, String url, String payload, String method) {
		frameworkParameters.getRunConfiguration();

		String payloadFileName = "Payloads_" + stp.getEnvironment().toLowerCase();
		HSSFWorkbook payloadWorkBook = excelReadWriteHelper.createPayloadsFile(report.getReportPath(), payloadFileName);
		HSSFSheet worksheet = payloadWorkBook.getSheet("Payloads");
		if (worksheet == null) {
			throw new FrameworkException("The specified sheet \"" + "Payloads" + "\""
					+ "does not exist within the workbook \"" + "Payloads" + ".xls\"");
		}

		int rowNum = worksheet.getLastRowNum() + 1;
		worksheet.createRow(rowNum);

		excelReadWriteHelper.setValue(rowNum, "Test Case", stp.getCurrentTestcase(), payloadWorkBook, worksheet);
		excelReadWriteHelper.setValue(rowNum, "Method", method, payloadWorkBook, worksheet);
		excelReadWriteHelper.setValue(rowNum, "URL", url, payloadWorkBook, worksheet);
		excelReadWriteHelper.setValue(rowNum, "Request Payload", payload, payloadWorkBook, worksheet);
		excelReadWriteHelper.setValue(rowNum, "Gateway Status", responseMap.get(WebServiceConstants.GATEWAYSTATUS),
				payloadWorkBook, worksheet);
		excelReadWriteHelper.setValue(rowNum, "Response Payload",
				responseMap.get(WebServiceConstants.RESPONSE_STRING).length() > 32000
						? responseMap.get(WebServiceConstants.RESPONSE_STRING).substring(0, 30000)
						: responseMap.get(WebServiceConstants.RESPONSE_STRING),
				payloadWorkBook, worksheet);
		excelReadWriteHelper.setValue(rowNum, "Response_Time", responseMap.get(WebServiceConstants.EXECUTION_TIME),
				payloadWorkBook, worksheet);
		excelReadWriteHelper.writeIntoFile(payloadWorkBook, report.getReportPath(), payloadFileName);

	}
	
	private void updateStatusToPayloadExcel(){
		String payloadFileName = "Payloads_" + stp.getEnvironment().toLowerCase();
		HSSFWorkbook payloadWorkBook = excelReadWriteHelper.createPayloadsFile(report.getReportPath(), payloadFileName);
		HSSFSheet worksheet = payloadWorkBook.getSheet("Payloads");
		if (worksheet == null) {
			throw new FrameworkException("The specified sheet \"" + "Payloads" + "\""
					+ "does not exist within the workbook \"" + "Payloads" + ".xls\"");
		}
		FormulaEvaluator formulaEvaluator = payloadWorkBook.getCreationHelper().createFormulaEvaluator();
		int column  = excelReadWriteHelper.getColumnNumber("Test Case", payloadWorkBook, worksheet);
		if(column == -1){
			return;
		}
		int rowNum = excelReadWriteHelper.getRowNum(column, stp.getCurrentTestcase(), payloadWorkBook, worksheet);
		if(rowNum == -1){
			return;
		}
		excelReadWriteHelper.setValue(rowNum, "Test Status", testRunStatus, payloadWorkBook, worksheet);
		excelReadWriteHelper.writeIntoFile(payloadWorkBook, report.getReportPath(), payloadFileName);
	}

	private HashMap<String, String> getAllHeaders() {

		HashMap<String, String> headerMap = null;
		if (addSecurityHeader) {
			// getting header type from General Data and passing to addSecurityHeaders() method
			headerMap = addSecurityHeaders(
					dataTable.getData(WebServiceConstants.INPUTDATASHEET, WebServiceConstants.SECURITY_HEADER_TYPE));
		}
		if (null != headerExtra && !headerExtra.isEmpty()) {
			return addExtraHeaderParameters(headerMap);
		}
		return headerMap;
	}

	private HashMap<String, String> addExtraHeaderParameters(HashMap<String, String> headerMap) {

		if (headerExtra.length() > 0) {
			if (headerMap == null) {
				headerMap = new HashMap<String, String>();
			}
			HelperMethods.addKeyValuePairToMap(headerExtra, headerMap, dataTablePath, report, dbSheetName, environment);
		}
		return headerMap;
	}
}
