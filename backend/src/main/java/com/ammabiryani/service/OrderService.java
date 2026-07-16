package com.ammabiryani.service;

import com.ammabiryani.dto.OrderRequest;
import com.ammabiryani.model.MenuItem;
import com.ammabiryani.model.Order;
import com.ammabiryani.model.OrderItem;
import com.ammabiryani.model.OrderStatus;
import com.ammabiryani.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuService menuService;

    @Autowired
    public OrderService(OrderRepository orderRepository, MenuService menuService) {
        this.orderRepository = orderRepository;
        this.menuService = menuService;
    }

    @Transactional
    public Order placeOrder(OrderRequest request) {
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setPhone(request.getPhone());
        order.setAddress(request.getAddress());
        order.setStatus(OrderStatus.PENDING);

        double total = 0.0;

        for (OrderRequest.OrderItemRequest itemReq : request.getItems()) {
            MenuItem menuItem = menuService.getById(itemReq.getMenuItemId());

            if (!Boolean.TRUE.equals(menuItem.getAvailable())) {
                throw new IllegalStateException(menuItem.getName() + " is currently unavailable");
            }
            if (itemReq.getQuantity() == null || itemReq.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero for " + menuItem.getName());
            }

            double lineTotal = menuItem.getPrice() * itemReq.getQuantity();
            total += lineTotal;

            OrderItem orderItem = new OrderItem(menuItem, itemReq.getQuantity(), menuItem.getPrice());
            order.addItem(orderItem);
        }

        order.setTotalAmount(total);
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + id));
    }

    public Order updateStatus(Long id, OrderStatus status) {
        Order order = getById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
