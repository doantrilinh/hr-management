package com.spring.app.dao.impl;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.spring.app.dao.PhongBanDao;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class PhongBanDaoImpl implements PhongBanDao {

    @Autowired
    private MongoTemplate mongoTemplate;
    private MongoCollection<Document> getCollection() {
        return this.mongoTemplate.getCollection("phong_ban");
    }
    @Override
    public boolean createPb(Document pb) {
        try {
            InsertOneResult res = this.getCollection().insertOne(pb);
            if (res != null && res.getInsertedId() != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(String id) {
        try {
            Document filter = new Document();
            filter.put("_id", new ObjectId(id));

            Document update = new Document();
            update.put("$set", new Document("deletedAt", new Date()));
            UpdateResult res = this.getCollection().updateOne(filter, update);
            if (res != null && res.getModifiedCount() > 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
