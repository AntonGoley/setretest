package ru.crystals.set10.pages.operday.cashes;

import java.math.BigDecimal;

public class MainCashDoc {
	
	
	private String status;
	private Integer number;
	private String type;
	private String employee;
	private BigDecimal income;
	private BigDecimal outcome;
	private String date;
	private String whoPrinted;
	private Boolean isPrinted;
	private String flexTableRowLocatorName;
	
	
	public String getFlexTableRowLocatorName() {
		return flexTableRowLocatorName;
	}
	public void setFlexTableRowLocatorName(String flexTableRowLocatorName) {
		this.flexTableRowLocatorName = flexTableRowLocatorName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
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
	
	
}
