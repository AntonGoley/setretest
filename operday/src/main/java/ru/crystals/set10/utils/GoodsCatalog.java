package ru.crystals.set10.utils;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


import ru.crystals.setretailx.products.catalog.Good;

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "goods-catalog")
	public class GoodsCatalog{
		
		@XmlElement(name = "good")
	    protected List<Good> goods;
		
		public void setGoods(List<Good> goods){
			this.goods = goods;
		}
		
	}


