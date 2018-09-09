package com.zella.web.servlet;

import com.google.gson.Gson;
import com.zella.domain.Category;
import com.zella.domain.Order;
import com.zella.service.AdminService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AdminServlet extends BaseServlet {

	public void findOrderInfoByOid(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		String oid = request.getParameter("oid");

		AdminService service = new AdminService();
		List<Map<String,Object>> orderInfo = service.findOrderInfoByOid(oid);

		Gson gson = new Gson();
		String orderInfoJson = gson.toJson(orderInfo);

		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(orderInfoJson);

	}

	public void findAllOrder(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		AdminService service = new AdminService();
		List<Order> orderList = service.findAllOrder();

		request.setAttribute("orderList", orderList);
		request.getRequestDispatcher("admin/order/list.jsp").forward(request, response);

	}

	public void findAllCategory(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// 此方法用于查找所有分类信息，获取一个List<Category>并转换成json返回到前端页面
		AdminService service = new AdminService();
		List<Category> categoryList = service.findAllCategory();

		Gson gson = new Gson();
		String categoryListJson = gson.toJson(categoryList);

		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(categoryListJson);

	}

}
