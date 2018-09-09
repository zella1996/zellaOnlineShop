package com.zella.service;

import com.zella.dao.AdminDao;
import com.zella.domain.Category;
import com.zella.domain.Order;
import com.zella.domain.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AdminService {

	public List<Category> findAllCategory() {

		AdminDao dao = new AdminDao();
		List<Category> categoryList = null;
		try {
			categoryList = dao.findAllCategory();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return categoryList;
	}

	public void saveProduct(Product product) {

		AdminDao dao = new AdminDao();
		try {
			dao.saveProduct(product);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public List<Order> findAllOrder() {

		AdminDao dao = new AdminDao();
		List<Order> orderList = null;
		try {
            orderList = dao.findAllOrder();
        } catch (SQLException e) {
			e.printStackTrace();
		}

		return orderList;
	}

	public List<Map<String,Object>> findOrderInfoByOid(String oid) {

		AdminDao dao = new AdminDao();
		List<Map<String,Object>> orderInfo = null;
		try {
			orderInfo = dao.findOrderInfoByOid(oid);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return orderInfo;
	}
}
