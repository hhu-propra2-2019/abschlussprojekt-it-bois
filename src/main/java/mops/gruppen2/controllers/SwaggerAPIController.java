package mops.gruppen2.controllers;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SwaggerAPIController {
	@RequestMapping(value = "/products", method = RequestMethod.GET)
	public List<String> getProducts(){
		List<String> productList = new ArrayList<>();
		productList.add("Honey");
		productList.add("Almond");
		return productList;
	}
	@RequestMapping(value = "/products", method = RequestMethod.POST)
	public String createProduct() {
		return "Product is saved successfully";
	}
}
