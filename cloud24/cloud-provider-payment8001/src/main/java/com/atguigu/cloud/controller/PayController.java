package com.atguigu.cloud.controller;

import com.atguigu.cloud.entities.Pay;
import com.atguigu.cloud.entities.PayDTO;
import com.atguigu.cloud.service.PayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author QRH
 * @date 2024/3/14 14:31
 * @description TODO
 */
@RestController
@RequestMapping("/pay")
@Slf4j
@Tag(name="支付微服务模块" ,description = "支付crud")
public class PayController {
    @Resource
    private PayService payService;

    @Operation(summary = "新增",description = "新增支付流水方法，json字符串做参数")
    @PostMapping("/add")
    public String addPay(@RequestBody PayDTO payDTO) {
        Pay pay = new Pay();
        BeanUtils.copyProperties(payDTO, pay);

        return "成功插入记录，返回值 " + payService.add(pay);
    }

    @Operation(summary = "删除",description = "删除支付流水方法",parameters = {@Parameter(name="id",required = true,description = "支付流水的id")})
    @DeleteMapping("/del/{id}")
    public int deletePay(@PathVariable("id") Integer id) {
        return payService.delete(id);
    }

    @Operation(summary = "修改",description = "修改支付流水",parameters = {@Parameter(name = "payDTO",required = true,description = "修改后的表单数据")})
    @PutMapping(value = "/update")
    public String updatePay(@RequestBody PayDTO payDTO) {
        Pay pay = new Pay();
        BeanUtils.copyProperties(payDTO, pay);
        return "更新了一条记录，返回值 " + payService.update(pay);
    }

    @Operation(summary = "获取",description = "根据id获取支付流水",parameters = {@Parameter(name = "id",required = true,description = "支付流水的id")})
    @GetMapping(value = "/get/{id}")
    public PayDTO getById(@PathVariable("id") Integer id) {
        PayDTO payDTO = new PayDTO();
        Pay pay = payService.getById(id);
        BeanUtils.copyProperties(pay, payDTO);
        return payDTO;
    }

    @Operation(summary = "获取",description = "获取所有支付流水")
    @GetMapping(value = "/get")
    public List<PayDTO> getAll() {
        List<PayDTO> payDTOS = new ArrayList<>();
        List<Pay> pays = payService.getAll();
        for (int i = 0; i < pays.size(); i++) {
            PayDTO payDTO = new PayDTO();
            BeanUtils.copyProperties(pays.get(i), payDTO);
            payDTOS.add(payDTO);
        }
        return payDTOS;
    }


}
