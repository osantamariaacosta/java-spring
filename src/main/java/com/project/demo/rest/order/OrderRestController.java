package com.project.demo.rest.order;

import com.project.demo.logic.entity.order.Order;
import com.project.demo.logic.entity.order.OrderRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
