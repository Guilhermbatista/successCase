package com.version.demov2.DTO;

import com.version.demov2.Entities.Product;

public class ProductMinDTO {

	private Long id;
	private String name;
	private Double price;

	public ProductMinDTO(Long id, String name, Double price) {
		this.id = id;
		this.name = name;
		this.price = price;
	}

	public ProductMinDTO(Product entity) {
		id = entity.getId();
		name = entity.getName();
		price = entity.getPrice();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Double getPrice() {
		return price;
	}

	
	
}
