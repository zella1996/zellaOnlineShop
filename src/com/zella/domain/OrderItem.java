package com.zella.domain;

public class OrderItem {

	private String itemid;// 订单项id
	private int count;// 订单项内该商品的购买数量
	private double subtotal;// 订单项金额小计
	private Product product;// 订单项对应商品
	private Order order;// 订单项所属订单

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
}
