package com.spring.app.ensure;

import com.spring.app.models.ChucVu;
import com.spring.app.models.PhongBan;
import org.springframework.util.ObjectUtils;

import com.spring.app.models.NhanVien;

public class EnsureParam {

	public static String ensureNhanVienCreate(NhanVien nhanVien) {
		
		if (ObjectUtils.isEmpty(nhanVien.getHoTen()) || nhanVien.getHoTen() == null) {
			return "Họ tên không được bỏ trống !!!";
		}
		
		if (ObjectUtils.isEmpty(nhanVien.getEmail()) || nhanVien.getEmail() == null) {
			return "Email không được bỏ trống !!!";
		}
		
		if (ObjectUtils.isEmpty(nhanVien.getSdt()) || nhanVien.getSdt() == null) {
			return "Số điện thoại không được bỏ trống !!!";
		}
		
		if (ObjectUtils.isEmpty(nhanVien.getNgaySinh()) || nhanVien.getNgaySinh() == null) {
			return "Ngày sinh không được bỏ trống !!!";
		}
		
		if (ObjectUtils.isEmpty(nhanVien.getPhongBanId()) || nhanVien.getPhongBanId() == null) {
			return "Vui lòng chọn phòng ban !!!";
		}
		
		if (ObjectUtils.isEmpty(nhanVien.getChucVuId()) || nhanVien.getChucVuId() == null) {
			return "Vui lòng chọn chức vụ !!!";
		}
		
		return "";
	}

	public static String ensurePhongBanCreate(PhongBan phongBan) {

		if (ObjectUtils.isEmpty(phongBan.getTenPhongBan()) || phongBan.getTenPhongBan() == null) {
			return "Tên phòng ban không được bỏ trống !!!";
		}

		if (ObjectUtils.isEmpty(phongBan.getChucNang()) || phongBan.getChucNang() == null) {
			return "Chức năng không được bỏ trống !!!";
		}

		return "";
	}

	public static String ensureChucVuCreate(ChucVu chucVu) {

		if (ObjectUtils.isEmpty(chucVu.getTenChucVu()) || chucVu.getTenChucVu() == null) {
			return "Tên chức vụ không được bỏ trống !!!";
		}

		if (ObjectUtils.isEmpty(chucVu.getThamQuyen()) || chucVu.getThamQuyen() == null) {
			return "Tham quyen không được bỏ trống !!!";
		}

		return "";
	}
	
}
