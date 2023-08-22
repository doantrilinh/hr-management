package com.spring.app.models;

import lombok.Data;

@Data
public class NhanVien extends BaseEntity {
	private static final long serialVersionUID = 1L;
	
	private String maNV;
	private String hoTen;
	private String ngaySinh;
	private String queQuan;
	private String email;
	private String sdt;
	private String phongBanId;
	private String chucVuId;
	private Double luong;
	/**
	 * 0: hoat dong, 1: nghi phep, 2: da nghi viec
	 */
	private int trangThai;
	
}
