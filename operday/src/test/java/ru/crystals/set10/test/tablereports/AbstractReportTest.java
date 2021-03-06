package ru.crystals.set10.test.tablereports;

import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.operday.HTMLRepotResultPage;
import ru.crystals.set10.pages.operday.tablereports.ReportConfigPage;
import ru.crystals.set10.pages.operday.tablereports.TableReportPage;
import ru.crystals.set10.test.AbstractTest;
import static ru.crystals.set10.pages.operday.tablereports.ReportConfigPage.HTMLREPORT;
import static ru.crystals.set10.pages.operday.OperDayPage.TABLEREPORTS;

public class AbstractReportTest extends AbstractTest{
	LoginPage loginPage;
	MainPage mainPage;
	TableReportPage tableReportsPage;
	HTMLRepotResultPage htmlReportResults;
	
	public <T> T navigateToReportConfig(
			String hostUrl,
			String user,
			String password,
			Class<T> reportConfig,
			String tabName,
			String reportType) {
		mainPage = new LoginPage(getDriver(), hostUrl).doLogin(user, password);
		tableReportsPage = mainPage.openOperDay().navigatePage(TableReportPage.class, TABLEREPORTS);
		return tableReportsPage.openReportConfigPage(reportConfig, tabName, reportType);
	}	
	
	public void doHTMLReport(ReportConfigPage reportConfigPage, boolean closeReport){
		htmlReportResults = reportConfigPage.generateReport(HTMLREPORT);
		// закрываем окно отчета и переключаемся в главное окно
		if (closeReport){
			reportConfigPage.switchWindow(true);
		}	
	}
	
}
