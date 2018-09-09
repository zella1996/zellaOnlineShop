package com.zella.service;

import com.zella.dao.UserDao;
import com.zella.domain.User;

import java.sql.SQLException;

public class UserService {

	public boolean regist(User user) {

		UserDao dao = new UserDao();
		int row = 0;
		try {
			row = dao.regist(user);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return row > 0 ? true : false;
	}

	// 激活
	public boolean active(String activeCode) {
		UserDao dao = new UserDao();
		int active = 0;
		try {
			active = dao.active(activeCode);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return active > 0 ? true : false;
	}

	// 校验用户名是否存在
	public boolean checkUsername(String username) {
		UserDao dao = new UserDao();
		Long isExist = 0L;
		try {
			isExist = dao.checkUsername(username);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isExist > 0 ? true : false;
	}

	// 用户登录的方法
	public User login(String username, String password) throws SQLException {
		UserDao dao = new UserDao();
		return dao.login(username, password);
	}

}
