package com.rohit.aop.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.rohit.aop.entity.Product;
import com.rohit.aop.service.ProductService;

@RestController
public class ProductController {

	@Autowired
	private ProductService service;


	@PostMapping("/save")
	public List<Product> saveProducts(@RequestBody List<Product> products) {

		List<Product> pList= new ArrayList<>();
		try {
			pList = service.addProduct(products);
		} catch (Exception e) {
			//Exception
		}

		return pList;
	}

	@GetMapping("/findProducts")
	public List<Product> getProducts() {
		
		List<Product> products = service.findAllProducts();
	
		return products;
	}

}
