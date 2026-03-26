package com.example.common.config.security;

/*
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.emp.entity.EmpVOSJW;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
*/

//@Slf4j
public class CustomLoginSuccessHandler {//implements AuthenticationSuccessHandler {

//	@Override
//	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//			Authentication authentication) throws IOException {
//
//		//로그인된 사용자 이름.
//		log.info("login username : " +authentication.getName());
//		log.info("login type : " +((EmpVOSJW)authentication.getPrincipal()).getUserType());
//
//		//session
//		request.getSession().setAttribute("userType", ((EmpVOSJW)authentication.getPrincipal()).getUserType());
//		//role
//		List<String> roleNames = new ArrayList<>();
//		authentication.getAuthorities().forEach( authority -> {
//			roleNames.add( authority.getAuthority());
//		}  );
//		System.out.println("roleName:" + roleNames);
//
//		if ( roleNames.contains("ROLE_ADMIN") ) {
//			response.sendRedirect("/admin");
//			return;
//		} else if ( roleNames.contains("ROLE_USER") ) {
//			response.sendRedirect("/hello");
//			return;
//		}
//		response.sendRedirect("/");
//	}

	}