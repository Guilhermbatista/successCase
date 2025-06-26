package com.version.demov2.Services;

import static org.mockito.ArgumentMatchers.any;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.version.demov2.DTO.ProductDTO;
import com.version.demov2.Entities.Product;
import com.version.demov2.Repositories.ProductRepository;
import com.version.demov2.Services.Exceptions.DatabaseException;
import com.version.demov2.Services.Exceptions.ResourceNotFoundException;
import com.version.demov2.Tests.ProductFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServicesTests {

	@InjectMocks
	private ProductService service;
	@Mock
	private ProductRepository repository;

	private Product product;
	private ProductDTO productDTO;
	private long existingId, nonExistingId, dependentProductId;
	private PageImpl<Product> page;

	@BeforeEach
	void setUp() throws Exception {

		existingId = 1L;
		nonExistingId = 10000L;
		dependentProductId = 3L;
		product = ProductFactory.createProduct("Panacota");
		productDTO = new ProductDTO(product);
		page = new PageImpl<>(List.of(product));

		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

		Mockito.when(repository.searchByName(any(), (Pageable) any())).thenReturn(page);

		Mockito.when(repository.save(any())).thenReturn(product);

		Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
		Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

		Mockito.when(repository.existsById(existingId)).thenReturn(true);
		Mockito.when(repository.existsById(dependentProductId)).thenReturn(true);
		Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);

		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentProductId);

	}

	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() {
		ProductDTO result = service.findById(existingId);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingId);
		Assertions.assertEquals(result.getName(), product.getName());
	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
	}

	@Test
	public void findAllShouldReturnPagedProductMinDTO() {
		Pageable pageable = PageRequest.of(0, 12);
		Assertions.assertNotNull(service.findAll("Panacota", pageable));
	}

	@Test
	public void insertShouldReturnProductDTO() {
		ProductDTO result = service.insert(productDTO);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), product.getId());
	}

	@Test
	public void updateShouldReturnProductWhenIdExists() {
		ProductDTO result = service.update(existingId, productDTO);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingId);
		Assertions.assertEquals(result.getName(), productDTO.getName());
	}

	@Test
	public void updateShouldThrowEntityNotFoundExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, productDTO);
		});
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhendependentProductId() {

		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentProductId);
		});
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExisting() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
	}

}
