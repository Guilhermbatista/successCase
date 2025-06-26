package com.version.demov2.Services;

import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.version.demov2.DTO.OrderDTO;
import com.version.demov2.Entities.Order;
import com.version.demov2.Entities.OrderItem;
import com.version.demov2.Entities.Product;
import com.version.demov2.Entities.User;
import com.version.demov2.Repositories.OrderItemRepository;
import com.version.demov2.Repositories.OrderRepository;
import com.version.demov2.Repositories.ProductRepository;
import com.version.demov2.Services.Exceptions.ForbiddenException;
import com.version.demov2.Services.Exceptions.ResourceNotFoundException;
import com.version.demov2.Tests.OrderFactory;
import com.version.demov2.Tests.ProductFactory;
import com.version.demov2.Tests.UserFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class OrderServiceTests {

	@InjectMocks
	private OrderService service;

	@Mock
	private OrderRepository repository;

	@Mock
	private AuthService authService;

	@Mock
	private ProductRepository productRepository;
	@Mock
	private OrderItemRepository orderItemRepository;
	@Mock
	private UserService userService;

	private Long existingOrderId, nonExistingOrderId;
	private Order order;
	private OrderDTO orderDTO;
	private User admin, client;

	private Product product;
	private long existingId, nonExistingId;

	@BeforeEach
	void setUp() throws Exception {
		existingOrderId = 1L;
		nonExistingOrderId = 2L;
		existingId = 1L;
		nonExistingId = 2L;

		product = ProductFactory.createProduct();

		admin = UserFactory.createCustomAdminUser(1L, "Jef");
		client = UserFactory.createCustomClientUser(2L, "Bob");

		order = OrderFactory.createOrder(client);
		orderDTO = new OrderDTO(order);

		Mockito.when(repository.findById(existingOrderId)).thenReturn(Optional.of(order));
		Mockito.when(repository.findById(nonExistingOrderId)).thenReturn(Optional.empty());

		Mockito.when(productRepository.getReferenceById(existingId)).thenReturn(product);
		Mockito.when(productRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
		Mockito.when(productRepository.save(any())).thenReturn(order);
		Mockito.when(orderItemRepository.saveAll(any())).thenReturn(new ArrayList<>(order.getItems()));

	}

	@Test
	public void findByIdShouldReturnOrderDTOWhenIdExistsAndAdminLogged() {

		Mockito.doNothing().when(authService).validateSelfOrAdmin(any());
		OrderDTO result = service.findById(existingOrderId);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingOrderId);
	}

	@Test
	public void findByIdShouldReturnOrderDTOWhenIdExistsAndSelfClientLogged() {

		Mockito.doNothing().when(authService).validateSelfOrAdmin(any());
		OrderDTO result = service.findById(existingOrderId);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingOrderId);
	}

	@Test
	public void findByIdShouldThrowsForbiddenExceptionWhenIdExistsAndOtherClientLogged() {

		Mockito.doThrow(ForbiddenException.class).when(authService).validateSelfOrAdmin(any());

		Assertions.assertThrows(ForbiddenException.class, () -> {
			@SuppressWarnings("unused")
			OrderDTO result = service.findById(existingOrderId);
		});
	}

	@Test
	public void findByIdShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExists() {

		Mockito.doNothing().when(authService).validateSelfOrAdmin(any());

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			@SuppressWarnings("unused")
			OrderDTO result = service.findById(nonExistingOrderId);
		});
	}

	@Test
	public void insertShouldReturnOrderDTOWhenAdminLogged() {

		Mockito.when(userService.authenticated()).thenReturn(admin);

		OrderDTO result = service.insert(orderDTO);

		Assertions.assertNotNull(result);
	}

	@Test
	public void insertShouldReturnOrderDTOWhenClientLogged() {

		Mockito.when(userService.authenticated()).thenReturn(client);

		OrderDTO result = service.insert(orderDTO);

		Assertions.assertNotNull(result);
	}

	@Test
	public void insertShouldThrowsUsernameNotFoundExceptionWhenUserNotLogged() {

		Mockito.doThrow(UsernameNotFoundException.class).when(userService).authenticated();

		order.setClient(new User());
		orderDTO = new OrderDTO(order);

		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			@SuppressWarnings("unused")
			OrderDTO result = service.insert(orderDTO);
		});
	}

	@Test
	public void insertShouldThrowsEntityNotfoundExceptionWhenOrderProductIdDoesNotExist() {

		Mockito.when(userService.authenticated()).thenReturn(client);

		product.setId(nonExistingOrderId);
		OrderItem orderItem = new OrderItem(order, product, 2, 10.0);
		order.getItems().add(orderItem);

		orderDTO = new OrderDTO(order);

		Assertions.assertThrows(EntityNotFoundException.class, () -> {
			@SuppressWarnings("unused")
			OrderDTO result = service.insert(orderDTO);
		});
	}
}
