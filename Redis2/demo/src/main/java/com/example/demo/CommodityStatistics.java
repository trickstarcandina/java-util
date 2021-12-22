package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("CommodityStatistics")
public class CommodityStatistics implements Serializable {
    @Id
    private String uuID;
    private Long cusId;
    private String name;
    private Integer value;

}