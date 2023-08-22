package com.spring.app.services.impl;

import java.util.List;
import java.util.Map;

import com.spring.app.services.NhanVienService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.app.dao.NhanVienDao;

@Service
public class NhanVienServicesImpl implements NhanVienService {

	@Autowired
	private NhanVienDao nhanVienDao;
	
	public List<Document> findAll(Map<String, Object> filter) {
		return this.nhanVienDao.findAll(filter);
	}
	public List<Document> findAll() {
		return this.nhanVienDao.findAll();
	}

	public boolean checkMailExists(String email, String userId) {
		return this.nhanVienDao.countByMail(email, userId) > 0;
	}

	public boolean checkPhoneExists(String phone, String userId) {
		return this.nhanVienDao.countByPhone(phone, userId) > 0;
	}

	public boolean create(Document nhanVien) {
		return this.nhanVienDao.create(nhanVien);
	}

	public boolean delete(String id) {
		return this.nhanVienDao.delete(id);
	}

	public Document findOne(String id) {
		return this.nhanVienDao.findOne(id);
	}

	public boolean update(Document nhanvien, String userId) {
		return this.nhanVienDao.update(nhanvien, userId);
	}
	
}
