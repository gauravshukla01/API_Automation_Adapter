package org.cts.hybrid;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.cognizant.framework.ExcelDataAccess;

import businesscomponents.helpers.HelperMethods;

public class WriteResponsesToFile {

	public static void main(String[] args) {
		 writeResponsesFromExcelToText();
		 writeRequestsFromExcelToText();
//		writeToExcelFromText();
	}

	public static void writeResponsesFromExcelToText() {
		ExcelDataAccess payloadDataFile = new ExcelDataAccess("D:\\Users\\gawandejyostna\\Desktop", "TestingFile");
		payloadDataFile.setDatasheetName("Sheet1");
		int lastRowNum = payloadDataFile.getLastRowNum();
		// INPUT FOLDER = inputrequests AND EXPECTED FOLDER = exptresponse
		String filePath = "D:\\Users\\gawandejyostna\\Desktop\\WebServicesWorkspace\\GitRepo\\test-automation\\Bajaj_Automation\\Datatables\\api\\exptresponse";
		for (int i = 1; i <= lastRowNum; i++) {
			HelperMethods.writeStringToTxtfile(payloadDataFile.getValue(i, "Response Payload"),
					payloadDataFile.getValue(i, "Test Case"), filePath, "uat");
		}
	}

	public static void writeRequestsFromExcelToText() {
		ExcelDataAccess payloadDataFile = new ExcelDataAccess("D:\\Users\\gawandejyostna\\Desktop", "TestingFile");
		payloadDataFile.setDatasheetName("Sheet1");
		int lastRowNum = payloadDataFile.getLastRowNum();
		// INPUT FOLDER = inputrequests AND EXPECTED FOLDER = exptresponse
		String filePath = "D:\\Users\\gawandejyostna\\Desktop\\WebServicesWorkspace\\GitRepo\\test-automation\\Bajaj_Automation\\Datatables\\api\\inputrequests";
		for (int i = 1; i <= lastRowNum; i++) {
			HelperMethods.writeStringToTxtfile(payloadDataFile.getValue(i, "Request Payload"),
					payloadDataFile.getValue(i, "Test Case"), filePath, "");
		}
	}

	/*
	 * This method will consolidate all input payloads and responses into excel
	 * respective to each test case
	 */
	public static void writeToExcelFromText() {
		ExcelDataAccess payloadDataFile = new ExcelDataAccess(
				"D:\\Users\\gawandejyostna\\Desktop\\WebServicesWorkspace\\GitRepo\\test-automation\\Bajaj_Automation\\Datatables",
				"CorrectPayloads");
		payloadDataFile.setDatasheetName("payloads");
		int lastRowNum = payloadDataFile.getLastRowNum();
		final String TEXT_FILE_EXTENSION = ".txt";
		File[] packageDirectories = {
				new File(
						"D:\\Users\\gawandejyostna\\Desktop\\WebServicesWorkspace\\GitRepo\\test-automation\\Bajaj_Automation\\Datatables\\api\\inputrequests"),
				new File(
						"D:\\Users\\gawandejyostna\\Desktop\\WebServicesWorkspace\\GitRepo\\test-automation\\Bajaj_Automation\\Datatables\\api\\exptresponse\\uat") };
		for (File packageDirectory : packageDirectories) {
			File[] packageFiles = packageDirectory.listFiles();
			String packageName = packageDirectory.getName();
			String payloadColumn = "Request Payload";
			if ("uat".equals(packageName)) {
				payloadColumn = "Response Payload";
			}
			for (int i = 0; i < packageFiles.length; i++) {
				File packageFile = packageFiles[i];
				String fileName = packageFile.getName();
				String fileNameWithoutExt = fileName.substring(0, fileName.indexOf(TEXT_FILE_EXTENSION));
				// We only want the .class files
				if (fileName.endsWith(TEXT_FILE_EXTENSION)) {
					String payload = readFromTextFile(packageFile);
					int rowNum = payloadDataFile.getRowNum(fileNameWithoutExt, 0);
					if (rowNum == -1) {
						lastRowNum = payloadDataFile.getLastRowNum();
						rowNum = payloadDataFile.addRow();
						payloadDataFile.setValue(rowNum, "Test Case", fileNameWithoutExt);
					}
					payloadDataFile.setValue(rowNum, payloadColumn,
							payload.length() > 32000 ? payload.substring(0, 32000) : payload);
				}
			}
		}
	}

	public static String readFromTextFile(File packageFile) {

		String requestpayload = null;

		try {

			FileReader fileReader = new FileReader(packageFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuilder stringBuilder = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line);
			} // end of while
			fileReader.close();
			requestpayload = stringBuilder.toString();
		} catch (IOException e) {
			System.out.println(" Error while reading :" + packageFile.getAbsolutePath());
		}
		return requestpayload;
	}

}