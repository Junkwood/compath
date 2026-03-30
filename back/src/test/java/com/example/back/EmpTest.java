package com.example.back;

import com.example.emp.dto.EmpDTOSJW;
import com.example.emp.entity.EmpVOSJW;
import com.example.emp.mapper.EmpMapperSJW;
import com.example.group.dto.GroupDTOSJW;
import com.example.group.entity.GroupVOSJW;
import com.example.group.mapper.GroupMapperSJW;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class EmpTest {
    @Autowired
    EmpMapperSJW empMapperSJW;
    @Autowired
    GroupMapperSJW groupMapperSJW;

//    @Test
    public void empList(){
        List<EmpDTOSJW> list = empMapperSJW.getAll();
        for(EmpDTOSJW e:list){
            System.out.println(e);
        }
    }
    //@Test
    public void empInfo(){
            System.out.println(empMapperSJW.getById(1001));
    }

    //@Test
    public void groupTest(){
        List<GroupDTOSJW> list = groupMapperSJW.getAllGroupForEmpRegister();
        for(GroupDTOSJW e:list){
            System.out.println(e);
        }
    }
    @Test
    public void randomNum(){
        Integer randval =(int) (Math.random()*1000000);
        String result = String.format("%06d",(int)Math.floor(randval));
        System.out.println(result);
    }
}
