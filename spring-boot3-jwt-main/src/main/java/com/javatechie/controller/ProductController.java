package com.javatechie.controller;

import com.javatechie.dto.AuthRequest;
import com.javatechie.dto.Product;
import com.javatechie.entity.UserInfo;
import com.javatechie.service.JwtService;
import com.javatechie.service.ProductService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService service;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    @PostMapping("/new")
    public String addNewUser(@RequestBody UserInfo userInfo) {
        return service.addUser(userInfo);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<Product> getAllTheProducts(HttpServletRequest request) {
    	System.out.println(jwtService.extractUsername(request.getHeader("Authorization").substring(7)));//decode username from token
//    	jwtService.
    	
    	Map<String, Object> extractClaims = jwtService.extractClaims(request.getHeader("Authorization").substring(7));
    	System.out.println("controll in all");
    	System.out.println((String)extractClaims.get("lname"));
        return service.getProducts();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    public Product getProductById(@PathVariable int id) {
    	System.out.println("controll in id");
        return service.getProduct(id);
    }


    @PostMapping("/authenticate")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
    	System.out.println(authRequest.getUsername()+"    "+authRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        
        System.out.println(authRequest.getUsername()+"    "+authRequest.getPassword());
        
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(authRequest.getUsername());
        } else {
        	System.out.println("Thowed exception");
            throw new UsernameNotFoundException("invalid user request !");
        }


    }
}
