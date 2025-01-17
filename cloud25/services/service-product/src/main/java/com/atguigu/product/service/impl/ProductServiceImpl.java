package com.atguigu.product.service.impl;

import com.atguigu.model.Product;
import com.atguigu.product.service.ProductService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ProductServiceImpl implements ProductService {

    @Override
    public Product getProductById(Long productId) {
        Product product = new Product();
        product.setId(productId);
        product.setPrice(new BigDecimal("12"));
        product.setProductName("尚硅谷 Mysql从入门到精通课程");
        product.setNum(2);

        return product;
    }
}
