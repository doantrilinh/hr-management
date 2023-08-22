package com.spring.app.models;

import lombok.Data;

@Data
public class PhongBan extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	private String tenPhongBan;
	private String chucNang;
}
