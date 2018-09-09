package com.zella.web.servlet;

import com.google.gson.Gson;
import com.zella.domain.*;
import com.zella.service.ProductService;
import com.zella.utils.CommonUtils;
import com.zella.utils.JedisPoolUtils;
import com.zella.utils.PaymentUtil;
import org.apache.commons.beanutils.BeanUtils;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ProductServlet extends BaseServlet {
//	protected void doGet(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//
//		// 获得请求的哪个方法的method
//		String methodName = request.getParameter("method");
//		if ("productListByCid".equals(methodName)) {
//			productListByCid(request, response);
//		} else if ("categoryList".equals(methodName)) {
//			categoryList(request, response);
//		} else if ("index".equals(methodName)) {
//			index(request, response);
//		} else if ("productInfo".equals(methodName)) {
//			productInfo(request, response);
//		} else if ("addProductToCart".equals(methodName)) {
//			addProductToCart(request, response);
//		} else if ("submitOrder".equals(methodName)) {
//			submitOrder(request, response);
//		} else if ("myOrders".equals(methodName)) {
//			myOrders(request, response);
//		}
//	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	// 显示商品的类别的的功能
	public void categoryList(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ProductService service = new ProductService();

		// 先从缓存中查询categoryList 如果有直接使用 没有在从数据库中查询 存到缓存中
		// 1、获得jedis对象 连接redis数据库
		Jedis jedis = JedisPoolUtils.getJedis();
		String categoryListJson = jedis.get("categoryListJson");
		// 2、判断categoryListJson是否为空
		if (categoryListJson == null) {
			System.out.println("缓存没有数据 查询数据库");
			// 准备分类数据
			List<Category> categoryList = service.findAllCategory();
			Gson gson = new Gson();
			categoryListJson = gson.toJson(categoryList);
			jedis.set("categoryListJson", categoryListJson);
		}

		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(categoryListJson);
	}

	// 显示首页的功能
	// 显示商品的类别的的功能
	public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ProductService service = new ProductService();

		// 准备热门商品---List<Product>
		List<Product> hotProductList = service.findHotProductList();

		// 准备最新商品---List<Product>
		List<Product> newProductList = service.findNewProductList();

		// 准备分类数据
		// List<Category> categoryList = service.findAllCategory();

		// request.setAttribute("categoryList", categoryList);
		request.setAttribute("hotProductList", hotProductList);
		request.setAttribute("newProductList", newProductList);

		request.getRequestDispatcher("/index.jsp").forward(request, response);

	}

	// 显示商品的详细信息功能
	public void productInfo(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 获得当前页
		String currentPage = request.getParameter("currentPage");
		// 获得商品类别
		String cid = request.getParameter("cid");

		// 获得要查询的商品的pid
		String pid = request.getParameter("pid");

		ProductService service = new ProductService();
		Product product = service.findProductByPid(pid);

		request.setAttribute("product", product);
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("cid", cid);

		// 获得客户端携带cookie---获得名字是pids的cookie
		String pids = pid;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("pids".equals(cookie.getName())) {
					pids = cookie.getValue();
					// 1-3-2 本次访问商品pid是8----->8-1-3-2
					// 1-3-2 本次访问商品pid是3----->3-1-2
					// 1-3-2 本次访问商品pid是2----->2-1-3
					// 将pids拆成一个数组
					String[] split = pids.split("_");// {3,1,2}
					List<String> asList = Arrays.asList(split);// [3,1,2]
					LinkedList<String> list = new LinkedList<String>(asList);// [3,1,2]
					// 判断集合中是否存在当前pid
					if (list.contains(pid)) {
						// 包含当前查看商品的pid
						list.remove(pid);
						list.addFirst(pid);
					} else {
						// 不包含当前查看商品的pid 直接将该pid放到头上
						list.addFirst(pid);
					}
					// 将[3,1,2]转成3-1-2字符串
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < list.size() && i < 7; i++) {
						sb.append(list.get(i));
						sb.append("_");// 3_1_2_
					}
					// 去掉3-1-2-后的-
					pids = sb.substring(0, sb.length() - 1);
				}
			}
		}

		Cookie cookie_pids = new Cookie("pids", pids);
		response.addCookie(cookie_pids);

		request.getRequestDispatcher("/product_info.jsp").forward(request, response);

	}

	// 根据商品的类别获得商品的列表
	public void productListByCid(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 获得cid
		String cid = request.getParameter("cid");

		String currentPageStr = request.getParameter("currentPage");
		if (currentPageStr == null)
			currentPageStr = "1";
		int currentPage = Integer.parseInt(currentPageStr);
		int currentCount = 12;

		ProductService service = new ProductService();
		PageBean pageBean = service.findProductListByCid(cid, currentPage, currentCount);

		request.setAttribute("pageBean", pageBean);
		request.setAttribute("cid", cid);

		// 定义一个记录历史商品信息的集合
		List<Product> historyProductList = new ArrayList<Product>();

		// 获得客户端携带名字叫pids的cookie
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("pids".equals(cookie.getName())) {
					String pids = cookie.getValue();// 3_2_1
					String[] split = pids.split("_");
					for (String pid : split) {
						Product pro = service.findProductByPid(pid);
						historyProductList.add(pro);
					}
				}
			}
		}

		// 将历史记录的集合放到域中
		request.setAttribute("historyProductList", historyProductList);

		request.getRequestDispatcher("/product_list.jsp").forward(request, response);

	}

	// 将商品添加到购物车
	public void addProductToCart(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// 从session域中获取Cart购物车对象
		HttpSession session = request.getSession();
		Cart cart = (Cart) session.getAttribute("cart");

		// 判断session中是否存在购物车
		Map<String, CartItem> cartItems;
		if (cart == null) {
			cart = new Cart();
		}
		cartItems = cart.getCartItems();

		// 从request域获取pid，buyNum
		String pid = request.getParameter("pid");
		String buyNumStr = request.getParameter("buyNum");
		int buyNum = Integer.parseInt(buyNumStr);

		// 通过pid获取添加到购物车的商品对象
		ProductService service = new ProductService();
		Product product = service.findProductByPid(pid);

		// 判断cartItems中是否存在同类商品
		// 约定key是pid
		CartItem cartItem;
		if (cartItems.containsKey(pid)) {// 存在则修改原有cartItem对象，只相加数量与结算小计
			cartItem = cartItems.get(pid);
			cartItem.setBuyNum(cartItem.getBuyNum() + buyNum);// 原来的buyNum加上新的buyNum
			cartItem.setSubtotal(cartItem.getSubtotal() + product.getShop_price() * buyNum);
			// 原来的subTotal加上新的subTotal（即商城价乘新的buyNum）
		} else {// 不存在则创建新CartItem对象，存入数据
			cartItem = new CartItem();
			cartItem.setProduct(product);
			cartItem.setBuyNum(buyNum);
			cartItem.setSubtotal(buyNum * product.getShop_price());
		}

		// 往cartItems中存入cartItem对象，key为pid
		cartItems.put(pid, cartItem);

		// 修改cart中的total值
		double amount = 0;
		for (Map.Entry<String, CartItem> entry : cartItems.entrySet()) {
			amount += entry.getValue().getSubtotal();
		}
		cart.setTotal(amount);

		// 往session中存入cart对象
		session.setAttribute("cart", cart);

		response.sendRedirect(request.getContextPath() + "/cart.jsp");

	}

	// 从购物车删除单一商品
	public void delProFromCart(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String pid = request.getParameter("pid");

		HttpSession session = request.getSession();
		Cart cart = (Cart) session.getAttribute("cart");
		if (cart != null) {
			Map<String, CartItem> cartItems = cart.getCartItems();

			// 修改总价
			cart.setTotal(cart.getTotal() - cartItems.get(pid).getSubtotal());
			// 移除该项商品
			cartItems.remove(pid);
		}

		response.sendRedirect(request.getContextPath() + "/cart.jsp");

	}

	// 清空当前购物车
	public void clearCart(HttpServletRequest request, HttpServletResponse response) throws IOException {

		HttpSession session = request.getSession();

		session.removeAttribute("cart");

		response.sendRedirect(request.getContextPath() + "/cart.jsp");

	}

	// 提交订单
	public void submitOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {

		HttpSession session = request.getSession();
		// 判断用户是否已经登录 未登录下面代码不执行
		User user = (User) session.getAttribute("user");
		if (user == null) {
			// 没有登录
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			return;
		}

		Cart cart = (Cart) session.getAttribute("cart");

		// 封装Order和OrderItem对象
		Order order = new Order();

		String oid = CommonUtils.getUUID();
		order.setOid(oid);

		order.setUser(user);
		order.setOrdertime(new Date());
		order.setState(0);
		order.setAddress(null);
		order.setName(null);
		order.setTelephone(null);

		if (cart != null) {
			order.setTotal(cart.getTotal());

			// 遍历购物车对象中的cartItems集合，封装到订单对象中的orderItems列表中
			for (Map.Entry<String, CartItem> entry : cart.getCartItems().entrySet()) {
				CartItem cartItem = entry.getValue();

				OrderItem orderItem = new OrderItem();
				orderItem.setItemid(CommonUtils.getUUID());
				orderItem.setCount(cartItem.getBuyNum());
				orderItem.setSubtotal(cartItem.getSubtotal());
				orderItem.setProduct(cartItem.getProduct());
				orderItem.setOrder(order);

				order.getOrderItems().add(orderItem);
			}
		}

		// order对象封装完毕

		ProductService service = new ProductService();
		boolean isSubmitSuccess = service.submitOrder(order);

		if (isSubmitSuccess) {
			session.setAttribute("order", order);
		}

		response.sendRedirect(request.getContextPath() + "/order_info.jsp");

	}

	public void confirmOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {

		Map<String, String[]> parameterMap = request.getParameterMap();
		Order order = new Order();
		try {
			BeanUtils.populate(order, parameterMap);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		// 更新收货人信息
		ProductService service = new ProductService();
		service.updateReceiverInfo(order);

		// 在线支付
		/*
		 * String pd_frpId = request.getParameter("pd_FrpId"); if
		 * ("ICBC-NET-B2C".equals(pd_frpId)) { // 接入工商银行的B2C接口 } else if
		 * ("BOC-NET-B2C".equals(pd_frpId)) { // 接入中国银行的B2C接口 }
		 */
		// 其实此处仅接入一个已经集成了大部分银行的接口就可以了
		// 这个接口由第三方支付平台提供 （此处是易宝）
		// 获得 支付必须基本数据
		String orderid = request.getParameter("oid");
		String money = order.getTotal() + "";// 不使用getParameter方法因为数据在前端传递到此处可能会被篡改
		// 银行
		String pd_FrpId = request.getParameter("pd_FrpId");

		// 发给支付公司需要哪些数据
		String p0_Cmd = "Buy";
		String p1_MerId = ResourceBundle.getBundle("merchantInfo").getString("p1_MerId");
		String p2_Order = orderid;
		String p3_Amt = money;
		String p4_Cur = "CNY";
		String p5_Pid = "";
		String p6_Pcat = "";
		String p7_Pdesc = "";
		// 支付成功回调地址 ---- 第三方支付公司会访问、用户访问
		// 第三方支付可以访问网址
		String p8_Url = ResourceBundle.getBundle("merchantInfo").getString("callback");
		String p9_SAF = "";
		String pa_MP = "";
		String pr_NeedResponse = "1";
		// 加密hmac 需要密钥
		String keyValue = ResourceBundle.getBundle("merchantInfo").getString("keyValue");
		String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt, p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc,
				p8_Url, p9_SAF, pa_MP, pd_FrpId, pr_NeedResponse, keyValue);

		String url = "https://www.yeepay.com/app-merchant-proxy/node?pd_FrpId=" + pd_FrpId + "&p0_Cmd=" + p0_Cmd
				+ "&p1_MerId=" + p1_MerId + "&p2_Order=" + p2_Order + "&p3_Amt=" + p3_Amt + "&p4_Cur=" + p4_Cur
				+ "&p5_Pid=" + p5_Pid + "&p6_Pcat=" + p6_Pcat + "&p7_Pdesc=" + p7_Pdesc + "&p8_Url=" + p8_Url
				+ "&p9_SAF=" + p9_SAF + "&pa_MP=" + pa_MP + "&pr_NeedResponse=" + pr_NeedResponse + "&hmac=" + hmac;

		// 重定向到第三方支付平台
		response.sendRedirect(url);

	}

	public void myOrders(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		HttpSession session = request.getSession();
		// 判断用户是否已经登录 未登录下面代码不执行
		User user = (User) session.getAttribute("user");
		if (user == null) {
			// 没有登录
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			return;
		}

		// 查询该用户的所有订单信息
		ProductService service = new ProductService();
		// order对象的数据是不完整的，缺少User对象和OrderItem的列表
		List<Order> orderList = service.findAllOrders(user.getUid());

		// 然后遍历订单列表，补全所有订单对象的数据
		if (orderList != null) {
			List<Map<String, Object>> mapList;
			for (Order order : orderList) {

				mapList = service.findAllOrderItemByOid(order.getOid());

				// 补全为空的orderItems列表

				for (Map<String, Object> map : mapList) {

					try {
						// 此处应该想起用BeanUtils封装实体
						OrderItem item = new OrderItem();
						BeanUtils.populate(item, map);

						Product product = new Product();
						BeanUtils.populate(product, map);
						// 将product存入orderItem对象中
						item.setProduct(product);

						// 添加此orderItem对象进入orderItems列表中
						order.getOrderItems().add(item);

					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}

				}

			}
		}

		request.setAttribute("orderList", orderList);
		request.getRequestDispatcher("/privilege/order_list.jsp").forward(request, response);

	}

}
