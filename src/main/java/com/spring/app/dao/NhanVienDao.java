package com.spring.app.dao;

import java.util.List;
import java.util.Map;

import org.bson.Document;

public interface NhanVienDao {

	Document findOne(String id);

	List<Document> findAll(Map<String, Object> filter);

	List<Document> findAll();

	boolean create(Document nv);

	boolean update(Document nv, String userId);

	boolean delete(String id);

	long countByMail(String email, String userId);

	long countByPhone(String phone, String userId);


}
