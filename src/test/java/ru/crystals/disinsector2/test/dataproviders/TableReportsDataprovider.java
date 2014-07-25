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
	
	@DataProvider (name = "Шапка отчета Прайс чекеры")
	public static Object[][] priceCheckerTableHead() {
		return new Object[][] {
		{"Отчёт для Прайс чекеров"},
		{"№ ТК"},
		{"Дата"},
		{"Время"},
		{"MAC адрес"},
		{"Прайс Чекер"},
		{"Штрихкод"},
		{"Код товара"},
		{"Наименование"},
		};	
	}
	
	@DataProvider (name = "Шапка отчета Товары на ТК")
	public static Object[][] goodOnTKTableHead() {
		return new Object[][] {
		{"Отчёт по товару"},
		{"Id товара"},
		{"Короткое название"},
		{"Полное название"},
		{"Поставщик"},
		{"Подгруппа"},
		{"Прогрузка"},
		{"Страна"},
		{"Товар на полке:"},
		{"Штрих-код"},
		{"Ед. изм."},
		{"Срок от"},
		{"Срок до"},
		{"Основной"},
		{"Загрузка на весы"},
		{"Цена 1"},
		{"Цена 2"},
		{"Цена"},
		{"Валюта"},
		{"Начало периода"},
		{"Конец периода"},
		{"Номер цены"},
		};	
	}
	
}

