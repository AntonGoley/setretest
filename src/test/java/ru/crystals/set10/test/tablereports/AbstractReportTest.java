package ru.crystals.set10.test.tablereports;

import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.operday.HTMLRepotResultPage;
import ru.crystals.set10.pages.operday.tablereports.ReportConfigPage;
import ru.crystals.set10.pages.operday.tablereports.TableReportPage;
import ru.crystals.set10.test.AbstractTest;
import static ru.crystals.set10.pages.operday.tablereports.ReportConfigPage.HTMLREPORT;

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
			int tabIndex,
			String reportType) {
		mainPage = new LoginPage(getDriver(), hostUrl).doLogin(user, password);
		tableReportsPage = mainPage.openOperDay().openTableReports();
		return tableReportsPage.openReportConfigPage(reportConfig, tabIndex, reportType);
	}	
	
	public void doHTMLReport(ReportConfigPage reportConfigPage, boolean closeReport){
		htmlReportResults = reportConfigPage.generateReport(HTMLREPORT);
		// закрываем окно отчета и переключаемся в главное окно
		if (closeReport){
			reportConfigPage.switchWindow(true);
		}	
	}
	
}
