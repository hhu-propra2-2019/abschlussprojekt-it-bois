package mops.gruppen2.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.javafaker.Faker;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mops.gruppen2.domain.ProductSwaggerExample;
import mops.gruppen2.domain.event.AddUserEvent;
import mops.gruppen2.service.SerializationService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Ein Beispiel für eine API mit Swagger.
 */
@RestController
@RequestMapping("/products")
public class SwaggerAPIControllerExample {

    private final Faker faker = new Faker();
    private final List<ProductSwaggerExample> products = new ArrayList<>();
    private final SerializationService serializationService;

    public SwaggerAPIControllerExample(SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    @GetMapping("/get/all")
    @ApiOperation(value = "Erzeugt eine Liste mit allen gespeicherten Produkten")
    public List<ProductSwaggerExample> getProducts() {
        return products;
    }

    @GetMapping("/get/{index}")
    public ProductSwaggerExample getProduct(@ApiParam("Produkt Index")  @PathVariable int index) {
        return products.get(index);
    }

    @PostMapping("/save")
    public String saveProduct(@RequestBody  ProductSwaggerExample product) {
        products.add(product);

        return "Product saved successfully";
    }

    @PostMapping("/random")
    public String saveRandomProduct() {
        products.add(new ProductSwaggerExample(faker.food().ingredient(), "Empty"));

        return "Product saved successfully";
    }

}
