package com.spring.app.dao;

import org.bson.Document;

public interface PhongBanDao {
    boolean createPb(Document pb);

    boolean delete(String id);
}
