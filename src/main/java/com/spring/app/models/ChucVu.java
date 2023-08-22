package com.spring.app.models;

import lombok.Data;

@Data
public class ChucVu extends BaseEntity{

	private static final long serialVersionUID = 1L;
	
	private String tenChucVu;
	private String thamQuyen;
}
