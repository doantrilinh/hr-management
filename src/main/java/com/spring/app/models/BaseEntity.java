package com.spring.app.models;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	private Date createdAt;
	private Date updatedAt;
	private Date deletedAt;
}
