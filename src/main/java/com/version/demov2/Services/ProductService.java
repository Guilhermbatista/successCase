package com.version.demov2.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.version.demov2.DTO.CategoryDTO;
import com.version.demov2.DTO.ProductDTO;
import com.version.demov2.DTO.ProductMinDTO;
import com.version.demov2.Entities.Category;
import com.version.demov2.Entities.Product;
import com.version.demov2.Repositories.ProductRepository;
import com.version.demov2.Services.Exceptions.DatabaseException;
import com.version.demov2.Services.Exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Product product = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
		return new ProductDTO(product);
	}

	@Transactional(readOnly = true)
	public Page<ProductMinDTO> findAll(String name, Pageable pageable) {
		return repository.searchByName(name, pageable).map(x -> new ProductMinDTO(x));
	}

	@Transactional
	public ProductDTO insert(ProductDTO entity) {
		Product product = new Product();
		copyDtoToEntity(entity, product);
		return new ProductDTO(repository.save(product));

	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO entity) {
		try {
			Product product = new Product();
			copyDtoToEntity(entity, product);
			return new ProductDTO(repository.save(product));
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Product not found");
		}

	}

	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Product not found");
		}
		try {
			repository.deleteById(id);

		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Falha de integridade referencial");
		}
	}
	@Transactional(propagation = Propagation.SUPPORTS)
	private void copyDtoToEntity(ProductDTO dto, Product entity) {
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setPrice(dto.getPrice());

		entity.getCategories().clear();
		for (CategoryDTO catDto : dto.getCategories()) {
			Category cat = new Category();
			cat.setId(catDto.getId());
			entity.getCategories().add(cat);
		}
	}
}
