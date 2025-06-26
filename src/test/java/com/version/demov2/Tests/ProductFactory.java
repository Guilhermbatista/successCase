package com.version.demov2.Tests;

import com.version.demov2.Entities.Category;
import com.version.demov2.Entities.Product;

public class ProductFactory {

	public static Product createProduct() {
		Category category = CategoryFactory.createCategory();
		Product product = new Product(1L, "Playstation",
				"Teste aleatorio do game e descricaoo game e descricaoo game e descricaoo game e descricao", 3999.0);
		product.getCategories().add(category);
		return product;
	}
	public static Product createProduct(String name) {
		Product product = createProduct();
		product.setName(name);
		return product;
	}

}
