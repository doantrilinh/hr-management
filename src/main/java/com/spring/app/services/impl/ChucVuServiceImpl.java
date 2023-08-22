package com.spring.app.services.impl;

import com.spring.app.dao.ChucVuDao;
import com.spring.app.services.ChucVuService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ChucVuServiceImpl implements ChucVuService {

    @Autowired
    private ChucVuDao chucVuDao;
    public boolean create(Document pb){
        return  this.chucVuDao.createCv( pb);
    }
    public boolean delete(String id) {
        return this.chucVuDao.delete(id);
    }
}
