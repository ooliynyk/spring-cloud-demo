package com.plexteq.cloud.gatewayservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@EnableZuulProxy
@EnableDiscoveryClient
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@EnableFeignClients
@SpringBootApplication
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

}

@FeignClient(serviceId = "product-service")
interface ProductsClient {

    @GetMapping(value = "products")
    Resources<Product> fetchProducts();

}

@RefreshScope
@RestController
@RequestMapping("/api")
class ApiController {

    @Value("${message}")
    private String message;

    @Autowired
    private ProductsClient productsClient;

    @GetMapping("/product-titles")
    public Collection<String> getProductTitles() {
        return productsClient.fetchProducts().getContent().stream().map(Product::getTitle).collect(Collectors.toSet());
    }

    @GetMapping("/message")
    public String getMessage() {
        return message;
    }

}

class Product {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
