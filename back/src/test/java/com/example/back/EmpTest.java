package com.example.back;

import com.example.emp.entity.EmpVOSJW;
import com.example.emp.mapper.EmpMapperSJW;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class EmpTest {
    @Autowired
    EmpMapperSJW empMapperSJW;
    //@Test
    public void empList(){
        List<EmpVOSJW> list = empMapperSJW.findAll();
        for(EmpVOSJW e:list){
            System.out.println(e);
        }
    }
    @Test
    public void empInfo(){
            System.out.println(empMapperSJW.findById(1001));
    }
}
