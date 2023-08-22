package com.spring.app.services.impl;

import com.spring.app.dao.PhongBanDao;
import com.spring.app.services.PhongBanService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PhongBanServiceImpl implements PhongBanService {

    @Autowired
    private PhongBanDao phongBanDao;

    public boolean create(Document pb){
        return  this.phongBanDao.createPb( pb);
    }
    public boolean delete(String id) {
        return this.phongBanDao.delete(id);
    }
}
