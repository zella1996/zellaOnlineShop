package com.zella.dao;

import com.zella.domain.Category;
import com.zella.domain.Order;
import com.zella.domain.OrderItem;
import com.zella.domain.Product;
import com.zella.utils.DataSourceUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ProductDao {

	// 获得热门商品
	public List<Product> findHotProductList() throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "select * from product where is_hot=? limit ?,?";
		return runner.query(sql, new BeanListHandler<>(Product.class), 1, 0, 9);
	}

	public List<Product> findNewProductList() throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "select * from product order by pdate desc limit ?,?";
		return runner.query(sql, new BeanListHandler<>(Product.class), 0, 9);
	}

	public List<Category> findAllCategory() throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "select * from category";
		return runner.query(sql, new BeanListHandler<>(Category.class));
	}

	public int getCount(String cid) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "select count(*) from product where cid=?";
		Long query = (Long) runner.query(sql, new ScalarHandler(), cid);
		return query.intValue();
	}

	public List<Product> findProductByPage(String cid, int index, int currentCount) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "select * from product where cid=? limit ?,?";
		List<Product> list = runner.query(sql, new BeanListHandler<>(Product.class), cid, index, currentCount);
		return list;
	}

	public Product findProductByPid(String pid) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "select * from product where pid=?";
		return runner.query(sql, new BeanHandler<>(Product.class), pid);
	}

	public int addOrder(Order order) throws SQLException {

		QueryRunner runner = new QueryRunner();
		String sql = "insert into orders values(?,?,?,?,?,?,?,?)";
		Object[] params = { order.getOid(), order.getOrdertime(), order.getTotal(), order.getState(),
				order.getAddress(), order.getName(), order.getTelephone(), order.getUser().getUid() };

		int update = runner.update(DataSourceUtils.getConnection(), sql, params);

		return update;
	}

	@SuppressWarnings("All")
	public void addOrderItem(Order order) throws SQLException {

		QueryRunner runner = new QueryRunner();
		String sql = "insert into orderitem values(?,?,?,?,?)";

		List<OrderItem> orderItems = order.getOrderItems();
		OrderItem orderItem = new OrderItem();
		for (int i = 0; i < orderItems.size(); i++) {
			orderItem = orderItems.get(i);

			Object[] params = { orderItem.getItemid(), orderItem.getCount(), orderItem.getSubtotal(),
					orderItem.getProduct().getPid(), orderItem.getOrder().getOid() };

			runner.update(DataSourceUtils.getConnection(), sql, params);

		}

	}

	public void updateReceiverInfo(Order order) throws SQLException {

		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "update orders set address=?,name=?,telephone=? where oid=?";

		int update = runner.update(sql, order.getAddress(), order.getName(), order.getTelephone(), order.getOid());

		System.out.println(update);

	}

	public void updateOrderState(String r6_order) throws SQLException {

		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "update orders set state=? where oid=?";

		int update = runner.update(sql, 1, r6_order);

	}

	public List<Order> findAllOrders(String uid) throws SQLException {

		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "select * from orders where uid=?";

		List<Order> orderList = runner.query(sql, new BeanListHandler<>(Order.class), uid);

		return orderList;

	}

	public List<Map<String, Object>> findAllOrderItemByOid(String oid) throws SQLException {

		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "select * from orderitem i,product p where i.pid=p.pid and i.oid=?";

		List<Map<String, Object>> mapList = runner.query(sql, new MapListHandler(), oid);// 多表查询
		// 多表查询没有唯一实体类对应需要查询的两个实体数据，应该用MapListHandler先不封装数据成为实体，取出数据然后手动封装

		return mapList;

	}
}
