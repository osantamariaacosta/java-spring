package com.project.demo.rest.order;

import com.project.demo.logic.entity.order.Order;
import com.project.demo.logic.entity.order.OrderRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.logic.exceptions.ErrorResponse;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderRestController {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    // /orders/user/{userId}
    // /orders/user/20
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getAllByUser (@PathVariable Long userId) {
        Optional<User> foundUser = userRepository.findById(userId);
        if(foundUser.isPresent()) {
            List<Order> orders = orderRepository.getOrderByUserId(userId);
            return  new ResponseEntity<>(orders, HttpStatus.OK);
        } else {
            throw new UsernameNotFoundException("User id " + userId + " not found");
        }
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<Order> addOrderToUser(@PathVariable Long userId, @RequestBody Order order) {
        Optional<User> foundUser = userRepository.findById(userId);
        if(foundUser.isPresent()) {
            order.setUser(foundUser.get());
            Order savedOrder = orderRepository.save(order);
            return new ResponseEntity<>(savedOrder, HttpStatus.CREATED);
        } else {
            throw new UsernameNotFoundException("User id " + userId + " not found");
        }
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable Long orderId, @RequestBody Order order) {
        Optional<Order> foundOrder = orderRepository.findById(orderId);
        if(foundOrder.isPresent()) {
            order.setId(foundOrder.get().getId());
            order.setUser(foundOrder.get().getUser());
            orderRepository.save(order);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } else {
            ErrorResponse error =  new ErrorResponse("Order with ID " + orderId + " not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order with ID " + orderId + " not found");
        }
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<?> pathOrder(@PathVariable Long orderId, @RequestBody Order order) {
        Optional<Order> foundOrder = orderRepository.findById(orderId);
        if(foundOrder.isPresent()) {
            if(order.getTotal() != null) foundOrder.get().setTotal(order.getTotal());
            if(order.getDescription() != null) foundOrder.get().setDescription(order.getDescription());
            orderRepository.save(foundOrder.get());
            return new ResponseEntity<>(foundOrder.get(), HttpStatus.OK);
        } else {
            ErrorResponse error =  new ErrorResponse("Order with ID " + orderId + " not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
        Optional<Order> foundOrder = orderRepository.findById(orderId);
        if (foundOrder.isPresent()) {
            Optional<User> user = userRepository.findById(foundOrder.get().getUser().getId());
            user.get().getOrders().remove(foundOrder.get());
            orderRepository.deleteById(orderId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            ErrorResponse error =  new ErrorResponse("Order with ID " + orderId + " not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

}
