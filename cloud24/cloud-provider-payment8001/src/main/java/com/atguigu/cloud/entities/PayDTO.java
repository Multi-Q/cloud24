package com.atguigu.cloud.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author QRH
 * @date 2024/3/14 14:22
 * @description 给前端h5工程师使用的接口数据
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PayDTO implements Serializable {

    private Integer id;

    private String payNo;

    private String orderNo;

    private Integer userId;

    private BigDecimal amount;
}
