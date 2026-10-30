
package com.ecommerce.service;

import com.ecommerce.event.OrderCreatedEvent;
import com.ecommerce.model.*;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final UserService userService;
    private final OrderEventProducer orderEventProducer;
    private final ProductRepository productRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found with id: " + id));
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Transactional
    public Order createOrder(
            Long userId,
            String shippingAddress,
            String paymentMethod) {

        User user = userService.getUserById(userId);
        Cart cart = cartService.getCartByUserId(userId);

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(Order.OrderStatus.PENDING);

        double totalAmount = 0.0;

        // Trừ tồn kho có điều kiện trong cùng transaction.
        // Nếu bất kỳ sản phẩm nào không đủ, ném exception để rollback
        // tất cả thao tác trừ kho trước đó.
        for (CartItem cartItem : cart.getItems()) {
            Long productId = cartItem.getProduct().getId();
            int quantity = cartItem.getQuantity();

            if (quantity <= 0) {
                throw new RuntimeException(
                        "Invalid quantity for product id: " + productId);
            }

            int updatedRows = productRepository.decreaseStockIfAvailable(
                    productId,
                    quantity
            );

            if (updatedRows != 1) {
                throw new RuntimeException(
                        "Insufficient stock for product id: " + productId);
            }
        }

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());

            order.getItems().add(orderItem);
            totalAmount += orderItem.getSubtotal();
        }

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.saveAndFlush(order);

        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                userId,
                savedOrder.getTotalAmount(),
                savedOrder.getShippingAddress(),
                savedOrder.getPaymentMethod()
        );

        orderEventProducer.publishOrderCreated(event);

        cartService.clearCart(userId);

        return savedOrder;
    }

    public Order updateOrderStatus(
            Long orderId,
            Order.OrderStatus status) {

        Order order = getOrderById(orderId);
        order.setStatus(status);

        return orderRepository.save(order);
    }

    public Order updatePaymentId(Long orderId, String paymentId) {
        Order order = getOrderById(orderId);
        order.setPaymentId(paymentId);
        order.setStatus(Order.OrderStatus.CONFIRMED);

        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        orderRepository.delete(order);
    }
}
