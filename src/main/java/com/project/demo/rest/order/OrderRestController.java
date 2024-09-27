package com.project.demo.rest.order;

import com.project.demo.logic.entity.order.Order;
import com.project.demo.logic.entity.order.OrderRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.logic.exceptions.ErrorResponse;
import com.project.demo.logic.http.GlobalResponseHandler;
import com.project.demo.logic.http.HttpResponse;
import com.project.demo.logic.http.Meta;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/user/{userId}/orders")
    public ResponseEntity<?> getAllByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page, // Número de página, por defecto 0
            @RequestParam(defaultValue = "10") int size, // Tamaño de página, por defecto 10
            HttpServletRequest request) {

        Optional<User> foundUser = userRepository.findById(userId);

        if (foundUser.isPresent()) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Order> ordersPage = orderRepository.getOrderByUserId(userId, pageable);

            Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
            meta.setTotalPages(ordersPage.getTotalPages());
            meta.setTotalElements(ordersPage.getTotalElements());
            meta.setPageNumber(ordersPage.getNumber());
            meta.setPageSize(ordersPage.getSize());

            return new GlobalResponseHandler().handleResponse(
                    "Orders retrieved successfully",
                    ordersPage.getContent(), // El contenido de la página
                    HttpStatus.OK,
                    meta
            );
        } else {
            return new GlobalResponseHandler().handleResponse(
                    "User not found",
                    null,
                    HttpStatus.NOT_FOUND,
                    request
            );
        }
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<?> addOrderToUser(@PathVariable Long userId, @RequestBody Order order, HttpServletRequest request) {
        Optional<User> foundUser = userRepository.findById(userId);
        if(foundUser.isPresent()) {
            order.setUser(foundUser.get());
            Order savedOrder = orderRepository.save(order);
            return new GlobalResponseHandler().handleResponse(
                    "Order created successfully",
                    savedOrder,
                    HttpStatus.CREATED,
                    request
            );
        } else {
            return new GlobalResponseHandler().handleResponse(
                    "User not found",
                    null,
                    HttpStatus.NOT_FOUND,
                    request
            );
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
            return new ResponseEntity<>(new HttpResponse("Order deleted"), HttpStatus.OK);
        } else {
            ErrorResponse error =  new ErrorResponse("Order with ID " + orderId + " not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

}
