package com.spring.app.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.spring.app.dao.NhanVienDao;
import com.spring.app.utils.JsonUtils;

@Repository
public class NhanVienDaoImpl implements NhanVienDao {


	@Autowired
	private MongoTemplate mongoTemplate;

	private MongoCollection<Document> getCollection() {
		return this.mongoTemplate.getCollection("nhan_vien");
	}

	@Override
	public Document findOne(String id) {
		try {
			Document filter = new Document("_id", new ObjectId(id));
			filter.append("deletedAt", new Document("$exists", false));
			Document res = this.getCollection().find(filter).first();
			if (res != null && !res.isEmpty()) {
				return res;
			}

			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public List<Document> findAll(Map<String, Object> filter) {

		try {

			long limit = (long) filter.get("limit");
			long offset = (long) filter.get("offset");

			Document match = new Document();

			List<Document> and = new ArrayList<>();
			and.add(new Document("deletedAt", new Document("$exists", false)));

			if (!ObjectUtils.isEmpty(filter.get("search"))) {
				String search = filter.get("search").toString();

				Document regex = new Document("$regex", search).append("$options", "i");

				Document or = new Document("$or",
						Arrays.asList(new Document("hoTen", regex), new Document("queQuan", regex),
								new Document("phongban.tenPhongBan", regex), new Document("chucvu.tenChucVu", regex)));

				and.add(or);

			}

			if (!ObjectUtils.isEmpty(filter.get("phong"))) {
				String phongBanId = filter.get("phong").toString();
				and.add(new Document("phongBanId", new ObjectId(phongBanId)));
			}

			if (!ObjectUtils.isEmpty(filter.get("chucvu"))) {
				String chucVuId = filter.get("chucvu").toString();
				and.add(new Document("chucVuId", new ObjectId(chucVuId)));
			}

			if (!ObjectUtils.isEmpty(filter.get("trangthai"))) {
				int trangThai = (int) filter.get("trangthai");
				and.add(new Document("trangThai", trangThai));
			}

			match.put("$match", new Document("$and", and));

			// add fields
			Document addFiels = new Document("$addFields",
					new Document("phongBanId", new Document("$toObjectId", "$phongBanId")).append("chucVuId",
							new Document("$toObjectId", "$chucVuId")));

			// join phong ban
			Document joinPB = new Document("$lookup", new Document("from", "phong_ban")
					.append("localField", "phongBanId").append("foreignField", "_id").append("as", "phongban"));
			Document unwindPB = new Document("$unwind", "$phongban");

			// join chuc vu
			Document joinCV = new Document("$lookup", new Document("from", "chuc_vu").append("localField", "chucVuId")
					.append("foreignField", "_id").append("as", "chucvu"));
			Document unwindCV = new Document("$unwind", "$chucvu");

			// projection
			Document project = new Document("$project",
					new Document("id", new Document("$toString", "$_id")).append("_id", 0).append("hoTen", 1)
							.append("queQuan", 1).append("maNV", 1).append("sdt", 1).append("email", 1)
							.append("trangThai", 1).append("phongBan", "$phongban.tenPhongBan")
							.append("chucVu", "$chucvu.tenChucVu"));

			// sort
			Document sort = new Document("$sort", new Document("_id", -1));

			// limit & offset
			Document limitDoc = new Document("$limit", limit);
			Document offsetDoc = new Document("$skip", offset);

			List<Document> stages = new ArrayList<>();
			stages.add(addFiels);
			stages.add(joinPB);
			stages.add(unwindPB);
			stages.add(joinCV);
			stages.add(unwindCV);
			stages.add(match);
			stages.add(sort);
			stages.add(project);
			stages.add(offsetDoc);
			stages.add(limitDoc);

			System.out.println(JsonUtils.toJson(stages));

			List<Document> result = this.getCollection().aggregate(stages).into(new ArrayList<>());
			if (result != null && !result.isEmpty()) {
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();

	}

	@Override
	public boolean create(Document nv) {
		try {
			InsertOneResult res = this.getCollection().insertOne(nv);
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
	public boolean update(Document nv, String userId) {
		try {
			
			Document filter = new Document();
			filter.append("_id", new ObjectId(userId))
			.append("deletedAt", new Document("$exists", false));
			
			Document update = new Document();
			update.put("$set", nv);
			
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

	@Override
	public long countByMail(String email, String userId) {
		try {

			Document filter = new Document();
			filter.put("email", email);
			if (userId != null) {
				filter.put("_id", new Document("$ne", new ObjectId(userId)));
			}

			return this.getCollection().countDocuments(filter);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public long countByPhone(String phone, String userId) {
		try {

			Document filter = new Document();
			filter.put("sdt", phone);
			if (userId != null) {
				filter.put("_id", new Document("$ne", new ObjectId(userId)));
			}

			return this.getCollection().countDocuments(filter);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public List<Document> findAll() {
		try {
			Document filter = new Document();
			filter.put("deletedAt", new Document("$eq", null));

			List<Document> res = this.getCollection()
					.find(filter)
					.into(new ArrayList<>());

			if (res != null || res.size() > 0) {
				return  res;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
