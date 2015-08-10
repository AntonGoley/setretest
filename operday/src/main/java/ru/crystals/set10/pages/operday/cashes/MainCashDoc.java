package ru.crystals.set10.pages.operday.cashes;

import java.math.BigDecimal;

public class MainCashDoc {
	
	/*
	 * Документы ПКО
	 */
	public static final String DOC_TYPE_PKO_CASH_EXCESS = "Излишек по кассе";
	public static final String DOC_TYPE_PKO_UNENCLOSURE_ENCASHMENT = "Недовложение инкассация";
	public static final String DOC_TYPE_PKO_UNENCLOSURE_FROM_COUNTERPARTS = "Недовложение от контрагентов";
	public static final String DOC_TYPE_PKO_INCOME_FROM_OTHER_COUNTERPARTS = "Поступление от прочих контрагентов";
	public static final String DOC_TYPE_PKO_INCOME_FROM_EMPLOYEES = "Поступление от сотрудников магазина";
	public static final String DOC_TYPE_PKO_EXCHANGE_INCOME = "Размен денег приход";
	
	/*
	 * Документы РКО
	 */
	public static final String DOC_TYPE_RKO_ENCASHMENT= "Инкассация торговой выручки";
	public static final String DOC_TYPE_RKO_PAYMENT_FROM_DEPOSITOR = "Выдача с депонента";
	public static final String DOC_TYPE_RKO_SALARY_PAYMENT = "Выдача зарплаты";
	public static final String DOC_TYPE_RKO_CASH_LACK = "Недостача по кассе";
	public static final String DOC_TYPE_RKO_EXCESS_ENCASHMENT = "Перевложение Инкассация";
	public static final String DOC_TYPE_RKO_EXCHANGE_WITHDRAWAL = "Размен денег расход";
	
	/*
	 * Автоматически создаваемые документы
	 */
	public static final String DOC_TYPE_PKO_REVENUE = "ПКО Выручка магазина";
	public static final String DOC_TYPE_KM7 = "Акт КМ-7";
	public static final String DOC_TYPE_DDS = "ДДС";
	public static final String DOC_TYPE_LKK = "Лист Кассовой Книги";
	
	private MainCashDocStatus status;
	private Integer number;
	private String type;
	private String employee;
	private BigDecimal income;
	private BigDecimal outcome;
	private String date;
	private String whoPrinted;
	private Boolean isPrinted;
	private String flexTableRowLocatorName;
	private Boolean printable;
	
	public Boolean getPrinable() {
		return printable;
	}
	public void setPrinable(Boolean prinable) {
		this.printable = prinable;
	}
	public String getFlexTableRowLocatorName() {
		return flexTableRowLocatorName;
	}
	public void setFlexTableRowLocatorName(String flexTableRowLocatorName) {
		this.flexTableRowLocatorName = flexTableRowLocatorName;
	}
	public MainCashDocStatus getStatus() {
		return status;
	}
	public void setStatus(MainCashDocStatus status) {
		this.status = status;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getEmployee() {
		return employee;
	}
	public void setEmployee(String employee) {
		this.employee = employee;
	}
	public BigDecimal getIncome() {
		return income;
	}
	public void setIncome(BigDecimal income) {
		this.income = income;
	}
	public BigDecimal getOutcome() {
		return outcome;
	}
	public void setOutcome(BigDecimal outcome) {
		this.outcome = outcome;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getWhoPrinted() {
		return whoPrinted;
	}
	public void setWhoPrinted(String whoPrinted) {
		this.whoPrinted = whoPrinted;
	}
	public Boolean getIsPrinted() {
		return isPrinted;
	}
	public void setIsPrinted(Boolean isPrinted) {
		this.isPrinted = isPrinted;
	}
	
	public enum MainCashDocStatus {
		GREY,
		GREEN,
		YELLOW,
		RED
	}
}
