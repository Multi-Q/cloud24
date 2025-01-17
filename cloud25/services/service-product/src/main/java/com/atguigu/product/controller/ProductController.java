package com.atguigu.product.controller;

import com.atguigu.model.Product;
import com.atguigu.product.service.ProductService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
    @Resource
    private ProductService productService;

    //查询商品
    @GetMapping("/product/{productId}")
    public Product getProductById(@PathVariable Long productId){
        return productService.getProductById(productId);
    }
}
