package ru.crystals.set10.test.tablereports;

import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.operday.HTMLRepotResultPage;
import ru.crystals.set10.pages.operday.tablereports.AbstractReportConfigPage;
import ru.crystals.set10.pages.operday.tablereports.TableReportPage;
import ru.crystals.set10.test.AbstractTest;
import static ru.crystals.set10.pages.operday.tablereports.AbstractReportConfigPage.HTMLREPORT;

public class AbstractReportTest extends AbstractTest{
	LoginPage loginPage;
	MainPage mainPage;
	TableReportPage tableReportsPage;
	HTMLRepotResultPage htmlReportResults;


	public AbstractReportConfigPage navigateToReportConfig(
			String hostUrl,
			String user,
			String password,
			Class<AbstractReportConfigPage> reportConfigPageClass,
			int tabIndex,
			String reportType) {
		mainPage = new LoginPage(getDriver(), hostUrl).doLogin(user, password);
		tableReportsPage = mainPage.openOperDay().openTableReports();
		return tableReportsPage.openReportConfigPage(reportConfigPageClass, tabIndex, reportType);
	}	
	
	public void doHTMLReport(AbstractReportConfigPage reportConfigPageClass){
		htmlReportResults = reportConfigPageClass.generateReport(HTMLREPORT);
		reportConfigPageClass.switchWindow(true);
	}
	
}
