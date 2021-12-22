package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCachingModel {
    private String from;
    private String to;
    private long exp;
    private String cusId;
    private Object data;
}
