package com.spring.app.services;

import org.bson.Document;

public interface PhongBanService {
    boolean create(Document pb);
    boolean delete(String id);
}
