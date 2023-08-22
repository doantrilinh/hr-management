package com.spring.app.schedule;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.spring.app.services.impl.NhanVienServicesImpl;
import org.apache.poi.ss.usermodel.Workbook;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


@Service
public class TinhLuongSchedule {

	@Autowired
	private NhanVienServicesImpl nhanVienServicesImpl;

	@Scheduled(cron = "0 * * * * ?")
	public void salary() {

		List<Document> documents = nhanVienServicesImpl.findAll();
		File excelFile = new File("Files-Upload/ipIqOl11-BangLuongThang_10_2023.xlsx");
		try (Workbook workbook = excelFile.exists() ? WorkbookFactory.create(excelFile) : new XSSFWorkbook()) {
			Sheet sheet = workbook.getSheet("NhanVien");
			if (sheet == null) {
				sheet = workbook.createSheet("NhanVien");
			}
			Row headerRow = sheet.createRow(0);
			headerRow.createCell(0).setCellValue("Mã NV");
			headerRow.createCell(1).setCellValue("Họ Tên");
			headerRow.createCell(2).setCellValue("Lương");


			int rowNum = sheet.getLastRowNum() + 1;
			for (Document nhanVien : documents) {
				Row row = sheet.createRow(rowNum++);
				// Set cell values based on NhanVien properties
				row.createCell(0).setCellValue(nhanVien.get("maNV").toString());
				row.createCell(1).setCellValue(nhanVien.get("hoTen").toString());
				row.createCell(2).setCellValue(nhanVien.get("luong").toString());

			}
			// Save the workbook to a file
			try (FileOutputStream fileOut = new FileOutputStream("ipIqOl11-BangLuongThang_10_2023.xlsx")) {
				workbook.write(fileOut);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
