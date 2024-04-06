package com.atguigu.cloud.controller;

import com.atguigu.cloud.apis.StorageFeignApi;
import com.atguigu.cloud.resp.ResultData;
import com.atguigu.cloud.service.StorageService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author QRH
 * @date 2024/4/5 23:21
 * @description TODO
 */
@RestController
public class StorageController {
    @Resource
    private StorageService storageService;


    @PostMapping(value = "/storage/decrease")
    public ResultData decrease(Long productId, Integer count) {
        storageService.decrease(productId, count);
        return ResultData.success("扣减库存成功");
    }


}
