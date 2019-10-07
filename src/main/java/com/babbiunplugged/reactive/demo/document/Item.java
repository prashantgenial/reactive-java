package com.babbiunplugged.reactive.demo.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//@document is equivalent to @entity
@Document
public class Item {

	@Id
	private String id;
	private String desc;
	private Double price;

	public Item() {
	}

	public Item(String id, String desc, Double price) {
		super();
		this.id = id;
		this.desc = desc;
		this.price = price;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", desc=" + desc + ", price=" + price + "]";
	}

}
