package com.example.mongodbspringboot.repository;

import java.util.Date;

public interface UserRepositoryCustom {

    public long getMaxEmpId();

    public long updateUser(String empNo, String fullName, Date hireDate);

}
