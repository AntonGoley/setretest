package ru.crystals.set10.utils;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;



import ru.crystals.setretailx.products.catalog.Good;
import ru.crystals.setretailx.products.catalog.Likond;

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "goods-catalog")
	public class GoodsCatalog{
		
		@XmlElement(name = "good")
	    protected List<Good> goods;
		
		@XmlElement(name = "product-sales-allowing")
	    protected List<Likond> likonds;
		
		
		public void setGoods(List<Good> goods){
			this.goods = goods;
		}
		
		public void setLikonds(List<Likond> likonds){
			this.likonds = likonds;
		}
	}


