package mops.gruppen2.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SwaggerAPIController {

    /**
     * Ein Beispiel für eine API mit Swagger.
     *
     * @return Eine Liste von Produkten, repräsentiert durch Strings
     */
    @RequestMapping(value = "/products", method = RequestMethod.GET)
    public List<String> getProducts() {
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
