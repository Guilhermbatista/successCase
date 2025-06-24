package com.version.demov2.Services;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.version.demov2.DTO.OrderDTO;
import com.version.demov2.DTO.OrderItemDTO;
import com.version.demov2.Entities.Order;
import com.version.demov2.Entities.OrderItem;
import com.version.demov2.Entities.OrderStatus;
import com.version.demov2.Entities.Product;
import com.version.demov2.Entities.User;
import com.version.demov2.Repositories.OrderItemRepository;
import com.version.demov2.Repositories.OrderRepository;
import com.version.demov2.Repositories.ProductRepository;
import com.version.demov2.Services.Exceptions.ResourceNotFoundException;

@Service
public class OrderService {

	@Autowired
	private OrderRepository repository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private UserService userService;
	@Autowired
	private AuthService authService;

	@Transactional(readOnly = true)
	public OrderDTO findById(Long id) {
		Order order = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found"));
		authService.validateSerlfOrAdmin(order.getClient().getId());
		return new OrderDTO(order);
	}

	public OrderDTO insert(OrderDTO dto) {

		Order order = new Order();

		order.setMoment(Instant.now());
		order.setStatus(OrderStatus.WAITING_PAYMENT);

		User user = userService.authenticated();
		order.setClient(user);

		for (OrderItemDTO itemDTO : dto.getItems()) {
			Product product = productRepository.getReferenceById(itemDTO.getProductId());
			OrderItem item = new OrderItem(order, product, itemDTO.getQuantity(), product.getPrice());
			order.getItems().add(item);
		}

		repository.save(order);
		orderItemRepository.saveAll(order.getItems());
		return new OrderDTO(order);

	}

}
