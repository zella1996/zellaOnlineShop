package com.zella.web.servlet;

import com.zella.domain.User;
import com.zella.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

public class UserServlet extends BaseServlet {

	// 用户注销
	public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		// 从Session域中将user移除user对象
		session.removeAttribute("user");
		// ！并清除cookie以取消自动登录
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {

			Cookie usernameCookie = new Cookie("cookie_username", "");
			Cookie passwordCookie = new Cookie("cookie_password", "");
			// 设置Cookie的持久化时间
			usernameCookie.setMaxAge(0);
			passwordCookie.setMaxAge(0);

			response.addCookie(usernameCookie);
			response.addCookie(passwordCookie);

		}

		// 重定向到首页
		response.sendRedirect(request.getContextPath() + "/login.jsp");

	}

	// 用户登录
	public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();

		// 获得输入的用户名和密码
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		// 对密码进行加密
		// password = MD5Utils.md5(password);

		// 将用户名和密码传递给service层
		UserService service = new UserService();
		User user = null;
		try {
			user = service.login(username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 判断用户是否登录成功 user是否是null
		if (user != null) {
			// 登录成功
			// ***************判断用户是否勾选了自动登录*****************
			String autoLogin = request.getParameter("autoLogin");
			if (autoLogin != null) {
				Cookie usernameCookie = new Cookie("cookie_username", user.getUsername());
				Cookie passwordCookie = new Cookie("cookie_password", user.getPassword());
				// 设置Cookie的持久化时间
				usernameCookie.setMaxAge(60 * 60);
				passwordCookie.setMaxAge(60 * 60);
				// 设置Cookie的携带路径
				usernameCookie.setPath(request.getContextPath());
				passwordCookie.setPath(request.getContextPath());
				// 发送Cookie
				response.addCookie(usernameCookie);
				response.addCookie(passwordCookie);

			}

			// ***************************************************
			// 将user对象存到session中
			session.setAttribute("user", user);

			// 重定向到首页
			response.sendRedirect(request.getContextPath() + "/index.jsp");
		} else {
			request.setAttribute("loginError", "用户名或密码错误");
			request.getRequestDispatcher("/login.jsp").forward(request, response);
		}
	}
}
