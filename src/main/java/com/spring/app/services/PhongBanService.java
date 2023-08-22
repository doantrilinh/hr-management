package com.spring.app.services;

import com.spring.app.dao.PhongBanDao;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PhongBanService {

    @Autowired
    private PhongBanDao phongBanDao;

    public boolean create(Document pb){
        return  this.phongBanDao.createPb( pb);
    }
    public boolean delete(String id) {
        return this.phongBanDao.delete(id);
    }
}
