package com.spring.app.services;

import org.bson.Document;

import java.util.List;
import java.util.Map;

public interface NhanVienService {
    List<Document> findAll(Map<String, Object> filter);

    List<Document> findAll();

    boolean checkMailExists(String email, String userId);

    boolean checkPhoneExists(String phone, String userId);

    boolean create(Document nhanVien);

    boolean delete(String id);

    Document findOne(String id);

    boolean update(Document nhanvien, String userId);
}
