package com.spring.app.services;

import org.bson.Document;

public interface ChucVuService {
    boolean create(Document cv);
    boolean delete(String id);
}
