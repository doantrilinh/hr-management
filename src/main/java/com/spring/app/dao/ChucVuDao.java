package com.spring.app.dao;

import org.bson.Document;

public interface ChucVuDao {
    boolean createCv(Document pb);

    boolean delete(String id);
}
