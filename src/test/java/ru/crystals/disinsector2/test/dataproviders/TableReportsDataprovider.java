package ru.crystals.disinsector2.test.dataproviders;

import org.testng.annotations.DataProvider;


public class TableReportsDataprovider {
	
	@DataProvider (name = "Шапка отчета Рекламные акции")
	public static Object[][] adverstingReportTableHead() {
		return new Object[][] {
		{"Отчёт по товарам в рекламных акциях"},
		{"Тип скидки"},
		{"Код товара"},
		{"Группа продаж"},
		{"Товар в группе"},
		{"Цена 1"},
		{"Цена 2"},
		{"Цена 3"},
		{"Цена 4"},
		{"% скидки"},
		{"Код акции"},
		{"Цена по акции"},
		{"Название акции"},
		{"Название товара"},
		{"Начало акции"},
		{"Окончание акции"},
		{"Номер ТК"}
		};	
	}
	
}

