package com.spring.app.rests;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.spring.app.models.ChucVu;
import com.spring.app.models.PhongBan;
import com.spring.app.services.NhanVienService;
import com.spring.app.services.PhongBanService;
import com.spring.app.services.impl.PhongBanServiceImpl;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.spring.app.ensure.EnsureParam;
import com.spring.app.models.FileUploadResponse;
import com.spring.app.models.NhanVien;
import com.spring.app.services.impl.NhanVienServicesImpl;
import com.spring.app.utils.FileUploadUtil;
import com.spring.app.utils.HttpStatusUtils;
import com.spring.app.utils.JsonUtils;
import com.spring.app.utils.ResponseUtils;
import com.spring.app.utils.StringUtils;

@RestController
@RequestMapping(value = "employee", produces = MediaType.APPLICATION_JSON_VALUE)
public class NhanVienRest {

	@Autowired
	private NhanVienService nhanVienService;

	@Autowired
	private PhongBanService phongBanService;

	@Autowired
	private ResponseUtils responseUtils;

	/**
	 * Danh sach nhan vien
	 * 
	 * @param limit   so record moi trang
	 * @param offset  so trang (trong mongo default bat dau tu 0)
	 * @param hoten
	 * @param quequan
	 * @param phong
	 * @param chucvu
	 * @return
	 */
	@GetMapping("/all")
	public ResponseEntity<?> list(@RequestParam(value = "limit", defaultValue = "10", required = false) Long limit,
			@RequestParam(value = "offset", defaultValue = "0", required = false) Long offset,
			@RequestParam(value = "search", required = false) String search,
			@RequestParam(value = "trangthai", defaultValue = "0", required = false) Integer trangthai,
			@RequestParam(value = "phong", required = false) String phong,
			@RequestParam(value = "chucvu", required = false) String chucvu) {

		Map<String, Object> filter = new LinkedHashMap<>();
		filter.put("limit", limit);
		filter.put("offset", offset);
		filter.put("search", search);
		filter.put("chucvu", chucvu);
		filter.put("phong", phong);
		filter.put("trangthai", trangthai);

		List<Document> list = this.nhanVienService.findAll(filter);
		if (list == null) {
			return this.responseUtils.Error(HttpStatusUtils.NO_CONTENT.getMessage(),
					HttpStatusUtils.NO_CONTENT.getCode());
		}
		return this.responseUtils.Success(list);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> findOne(@PathVariable(name = "id", required = true) String id) {
		if (id == null || !ObjectId.isValid(id)) {
			return this.responseUtils.Error("Thiếu id nhân viên!", HttpStatusUtils.NO_PARAM.getCode());
		}

		Document nhanVien = this.nhanVienService.findOne(id);
		if (nhanVien == null || nhanVien.isEmpty()) {
			return this.responseUtils.Error(HttpStatusUtils.NO_CONTENT.getMessage(),
					HttpStatusUtils.NO_CONTENT.getCode());
		}
		nhanVien.put("id", id);
		nhanVien.remove("_id");
		return this.responseUtils.Success(nhanVien);
	}

	/**
	 * Them moi nhan vien
	 * 
	 * @param req
	 * @return
	 */
	@PostMapping("/create")
	public ResponseEntity<?> create(@RequestBody NhanVien req) {

		if (ObjectUtils.isEmpty(req)) {
			return ResponseEntity.badRequest().body(req);
		}

		String ensure = EnsureParam.ensureNhanVienCreate(req);
		if (!ensure.equals("")) {
			return this.responseUtils.Error(ensure, HttpStatusUtils.NO_PARAM.getCode());
		}

		if (!StringUtils.isValidMail(req.getEmail())) {
			return this.responseUtils.Error(HttpStatusUtils.INVALID_EMAIL.getMessage(),
					HttpStatusUtils.INVALID_EMAIL.getCode());
		}

		if (req.getLuong() <= 0) {
			return this.responseUtils.Error(HttpStatusUtils.INVALID_SALARY.getMessage(),
					HttpStatusUtils.INVALID_SALARY.getCode());
		}

		ExecutorService executor = Executors.newFixedThreadPool(3);

		CompletableFuture<Boolean> checkMail = CompletableFuture.supplyAsync(() -> {
			return this.nhanVienService.checkMailExists(req.getEmail(), null);
		}, executor);

		CompletableFuture<Boolean> checkPhone = CompletableFuture.supplyAsync(() -> {
			return this.nhanVienService.checkPhoneExists(req.getSdt(), null);
		}, executor);

		List<CompletableFuture<Boolean>> listFt = Arrays.asList(checkMail, checkPhone);

		CompletableFuture<?> all = CompletableFuture.allOf(listFt.toArray(new CompletableFuture[listFt.size()]));

		try {
			CompletableFuture<ResponseEntity<?>> response = all.thenApplyAsync(it -> {
				if (Boolean.TRUE.equals(checkMail.join())) {
					return this.responseUtils.Error(HttpStatusUtils.EMAIL_EXISTS.getMessage(),
							HttpStatusUtils.EMAIL_EXISTS.getCode());
				}

				if (Boolean.TRUE.equals(checkPhone.join())) {
					return this.responseUtils.Error(HttpStatusUtils.PHONE_EXISTS.getMessage(),
							HttpStatusUtils.PHONE_EXISTS.getCode());
				}

				ObjectId id = new ObjectId();

				Document nhanVien = JsonUtils.convertObject(req, Document.class);
				nhanVien.put("maNV", StringUtils.generateEmployeeID());
				nhanVien.put("createdAt", new Date());
				nhanVien.put("_id", id);

				boolean rs = this.nhanVienService.create(nhanVien);
				if (rs) {
					req.setId(id.toHexString());
					return this.responseUtils.Success(req);
				}

				return this.responseUtils.Error(HttpStatusUtils.NO_CONTENT.getMessage(),
						HttpStatusUtils.NO_CONTENT.getCode());
			}, executor);

			return response.join();
		} catch (Exception e) {
			return this.responseUtils.Error(HttpStatusUtils.NO_CONTENT.getMessage(),
					HttpStatusUtils.NO_CONTENT.getCode());
		} finally {
			executor.shutdown();
		}
	}

	/**
	 * Cap nhat thong tin nhan vien
	 * 
	 * @param req
	 * @return
	 */
	@PostMapping("/update")
	public ResponseEntity<?> update(@RequestBody NhanVien req) {
		if (req.getId() == null || !ObjectId.isValid(req.getId())) {
			return this.responseUtils.Error("Thiếu id nhân viên!", HttpStatusUtils.NO_PARAM.getCode());
		}
		Document nhanvien = new Document();
		if (req.getQueQuan() != null) {
			nhanvien.put("queQuan", req.getQueQuan());
		}

		if (req.getNgaySinh() != null) {
			nhanvien.put("ngaySinh", req.getNgaySinh());
		}

		if (req.getSdt() != null) {
			boolean isPhoneExists = this.nhanVienService.checkPhoneExists(req.getSdt(), req.getId());
			if (isPhoneExists) {
				return this.responseUtils.Error(HttpStatusUtils.PHONE_EXISTS.getMessage(),
						HttpStatusUtils.PHONE_EXISTS.getCode());
			}
			nhanvien.put("sdt", req.getSdt());
		}

		if (req.getEmail() != null) {
			if (!StringUtils.isValidMail(req.getEmail())) {
				return this.responseUtils.Error(HttpStatusUtils.INVALID_EMAIL.getMessage(),
						HttpStatusUtils.INVALID_EMAIL.getCode());
			}

			boolean isEmailExists = this.nhanVienService.checkMailExists(req.getEmail(), req.getId());
			if (isEmailExists) {
				return this.responseUtils.Error(HttpStatusUtils.EMAIL_EXISTS.getMessage(),
						HttpStatusUtils.EMAIL_EXISTS.getCode());
			}
			nhanvien.put("email", req.getEmail());
		}

		if (req.getLuong() != null) {
			if (req.getLuong() <= 0) {
				return this.responseUtils.Error(HttpStatusUtils.INVALID_SALARY.getMessage(),
						HttpStatusUtils.INVALID_SALARY.getCode());
			}
			nhanvien.put("luong", req.getLuong());
		}

		if (req.getChucVuId() != null && ObjectId.isValid(req.getChucVuId())) {
			nhanvien.put("chucVuId", req.getChucVuId());
		}

		if (req.getPhongBanId() != null && ObjectId.isValid(req.getPhongBanId())) {
			nhanvien.put("phongBanId", req.getPhongBanId());
		}

		boolean rs = this.nhanVienService.update(nhanvien, req.getId());
		if (rs) {
			return this.responseUtils.Success(req);
		}

		return this.responseUtils.Error(HttpStatusUtils.NO_CONTENT.getMessage(), HttpStatusUtils.NO_CONTENT.getCode());
	}

	/**
	 * Xoa nhan vien
	 * 
	 * @param req
	 * @return
	 */
	@PostMapping("/delete")
	public ResponseEntity<?> delete(@RequestBody NhanVien req) {
		if (req.getId() == null || !ObjectId.isValid(req.getId())) {
			return this.responseUtils.Error("Thiếu id nhân viên!", HttpStatusUtils.NO_PARAM.getCode());
		}

		boolean rs = this.nhanVienService.delete(req.getId());
		if (rs) {
			return this.responseUtils.Success(req);
		}
		return this.responseUtils.Error(HttpStatusUtils.NO_CONTENT.getMessage(), HttpStatusUtils.NO_CONTENT.getCode());
	}

	@PostMapping("/uploadExcelFile")
	public ResponseEntity<?> upload(@RequestParam(value = "file", required = true) MultipartFile multipartFile) {
		try {
			String fileName = org.springframework.util.StringUtils.cleanPath(multipartFile.getOriginalFilename());
	        long size = multipartFile.getSize();
	         
	        String filecode = FileUploadUtil.saveFile(fileName, multipartFile);
	        FileUploadResponse response = new FileUploadResponse();
	        response.setFileName(fileName);
	        response.setSize(size);
	        response.setDownloadUri("/downloadFile/" + filecode);
	        return this.responseUtils.Success(response);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.internalServerError().build();
	}

	/**
	 * Them moi phong ban
	 *
	 * @param req
	 * @return
	 */
	@PostMapping("/createPb")
	public ResponseEntity<?> create(@RequestBody PhongBan req) {

		if (ObjectUtils.isEmpty(req)) {
			return ResponseEntity.badRequest().body(req);
		}

		String ensure = EnsureParam.ensurePhongBanCreate(req);
		if (!ensure.equals("")) {
			return this.responseUtils.Error(ensure, HttpStatusUtils.NO_PARAM.getCode());
		}

		ObjectId id = new ObjectId();

		Document phongBan = JsonUtils.convertObject(req, Document.class);
		phongBan.put("createdAt", new Date());
		phongBan.put("_id", id);

		boolean rs = this.phongBanService.create(phongBan);
		if (rs) {
			req.setId(id.toHexString());
			return this.responseUtils.Success(req);
		}
		return this.responseUtils.Error(HttpStatusUtils.NO_CONTENT.getMessage(),
				HttpStatusUtils.NO_CONTENT.getCode());
	}

	/**
	 * Xoa phong ban
	 *
	 * @param req
	 * @return
	 */
	@PostMapping("/deletePb")
	public ResponseEntity<?> deletePb(@RequestBody PhongBan req) {
		if (req.getId() == null || !ObjectId.isValid(req.getId())) {
			return this.responseUtils.Error("Thiếu id phong ban!", HttpStatusUtils.NO_PARAM.getCode());
		}

		boolean rs = this.phongBanService.delete(req.getId());
		if (rs) {
			return this.responseUtils.Success(req);
		}
		return this.responseUtils.Error(HttpStatusUtils.NO_CONTENT.getMessage(), HttpStatusUtils.NO_CONTENT.getCode());
	}

	/**
	 * Them moi chuc vu
	 *
	 * @param req
	 * @return
	 */
	@PostMapping("/createCv")
	public ResponseEntity<?> createChucVu(@RequestBody PhongBan req) {

		if (ObjectUtils.isEmpty(req)) {
			return ResponseEntity.badRequest().body(req);
		}

		String ensure = EnsureParam.ensurePhongBanCreate(req);
		if (!ensure.equals("")) {
			return this.responseUtils.Error(ensure, HttpStatusUtils.NO_PARAM.getCode());
		}

		ObjectId id = new ObjectId();

		Document phongBan = JsonUtils.convertObject(req, Document.class);
		phongBan.put("createdAt", new Date());
		phongBan.put("_id", id);

		boolean rs = this.phongBanService.create(phongBan);
		if (rs) {
			req.setId(id.toHexString());
			return this.responseUtils.Success(req);
		}
		return this.responseUtils.Error(HttpStatusUtils.NO_CONTENT.getMessage(),
				HttpStatusUtils.NO_CONTENT.getCode());
	}

	/**
	 * Xoa chức vụ
	 *
	 * @param req
	 * @return
	 */
	@PostMapping("/deleteCv")
	public ResponseEntity<?> deleteChucVu(@RequestBody ChucVu req) {
		if (req.getId() == null || !ObjectId.isValid(req.getId())) {
			return this.responseUtils.Error("Thiếu id chức vụ!", HttpStatusUtils.NO_PARAM.getCode());
		}

		boolean rs = this.phongBanService.delete(req.getId());
		if (rs) {
			return this.responseUtils.Success(req);
		}
		return this.responseUtils.Error(HttpStatusUtils.NO_CONTENT.getMessage(), HttpStatusUtils.NO_CONTENT.getCode());
	}


}
