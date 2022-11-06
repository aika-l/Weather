package KatalonTestWebsite.openWeather;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.util.List;


public class WeatherAppSimpleTest {
    String url = "https://openweathermap.org/";
    public WebDriver driver;

    @BeforeTest
    public void setUp(){
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.get(url);

    }

    @Test
    public void TestGuideSectionTitle() throws InterruptedException {
        WebElement guideBtn = driver.findElement(By.xpath("//div[@id='desktop-menu']//a[text()= 'Guide']"));
        Thread.sleep(4000);
        guideBtn.click();
        String expTitle = "OpenWeatherMap API guide - OpenWeatherMap";
        // TODO assert if we are in correct url (/guide)
        Assert.assertEquals(driver.getTitle(), expTitle);
    }

    @Test
    public void TestSettingsAreInFar() throws InterruptedException {
        driver.navigate().back();
        Thread.sleep(5000);
        Assert.assertEquals(driver.getTitle(), "Сurrent weather and forecast - OpenWeatherMap");
        Thread.sleep(5000);
        WebElement settings = driver.findElement(By.xpath("//div[contains(text(), 'Imperial')]"));
        settings.click();
        String exp = "F";
        String act = driver.findElement(By.xpath("//span[@class='heading']")).getText();
        Assert.assertEquals(act.substring(act.length()-1), exp);
    }


    @Test
    public void TestCookiesOnTheBottomPresent(){
        // TODO checkCookies areDisplayed(), text==exp, last assert is to verify there are 2 btns on the bottom (not the text but actual 2 btns)
        String expCookiesText = "We use cookies which are essential for the site to work. " +
                "We also use non-essential cookies to help us improve our services. " +
                "Any data collected is anonymised. You can allow all cookies or manage them individually.";
        WebElement cookiesText = driver.findElement(By.xpath("//div[@id='stick-footer-panel']//p[contains(text(), 'We use cookies' )]"));
        Assert.assertEquals(cookiesText.getText(), expCookiesText);

        String expAllowAllBtn = "Allow all";
        WebElement allowAllBtn = driver.findElement(By.xpath("//div[@id='stick-footer-panel']//button"));
        Assert.assertEquals(allowAllBtn.getText(), expAllowAllBtn);

        String expManageCookiesBtn = "Manage cookies";
        WebElement manageCookiesBtn = driver.findElement(By.xpath("//a[contains(text(), ' Manage cookies ')]"));
        System.out.println(manageCookiesBtn.getText());
        Assert.assertEquals(manageCookiesBtn.getText(), expManageCookiesBtn);
    }

    @Test
    public void TestDropDownOptsOfSupportBtn() throws InterruptedException {
        //TODO to check the .size() can use * at the end of xpath. **Verify there are 3 options.

        driver.manage().window().maximize();
        WebDriverWait wait = new WebDriverWait(driver, 5);
        WebElement support = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//nav[@id='nav-website']//li[@class='with-dropdown']")));

        //driver.findElement(By.xpath("//nav[@id='nav-website']//li[@class='with-dropdown']/div")).click();
        //TODO review this step, Can be performed with reg. click.  id("support-dropdown").click -- Irina;
        Actions actions = new Actions(driver);
        actions.moveToElement(support).click().perform();

        String[] assertDropDwn = {"FAQ", "How to start", "Ask a question"};

        List<WebElement> dropDwnMenu = driver.findElements(By.xpath("//ul[@id='support-dropdown-menu']//a"));
        for (int i = 0; i < dropDwnMenu.size(); i++) {
            Assert.assertEquals(dropDwnMenu.get(i).getAttribute("textContent"), assertDropDwn[i]);
            break;
        }
    }

    //TODO it's good practice to click on email field and clear it.
    /*
    1. Открыть базовую ссылку
    2. Нажать пункт меню Support → Ask a question
    3. Заполнить поля Email, Subject, Message
    4. Не подтвердив CAPTCHA, нажать кнопку Submit
    5. Подтвердить, что пользователю будет показана ошибка “reCAPTCHA verification failed, please try again.”
     */
    @Test
    public void testCapturaDisplayedInSupportAskQuestion() throws InterruptedException {
        driver.manage().window().maximize();
        Thread.sleep(5000);
        String exp = "reCAPTCHA verification failed, please try again.";

        String mainPage = driver.getWindowHandle();

//        WebDriverWait wait = new WebDriverWait(driver, 5);
        WebElement supportButton = driver.findElement(By.id("support-dropdown"));
//        wait.until(ExpectedConditions.elementToBeClickable(supportButton));

        Assert.assertEquals(supportButton.getText(), "Support");
        supportButton.click();
        Thread.sleep(5000);
        WebElement askQuestion = driver.findElement(By.xpath("//ul[@id='support-dropdown-menu']//a[contains(text(), 'Ask a question')]"));
        askQuestion.click();
        for(String newTab : driver.getWindowHandles()){
            if(!newTab.equals(mainPage)){
                driver.switchTo().window(newTab);
            }
        }
        Thread.sleep(3000);
        WebElement emailAddress = driver.findElement(By.xpath("//input[@type='email']"));
        emailAddress.click();
        emailAddress.clear();
        emailAddress.sendKeys("test@test.com");
        WebElement subjectEntry = driver.findElement(By.xpath("//select[@name='question_form[subject]']"));
        subjectEntry.click();
        Select select = new Select(subjectEntry);
        select.selectByIndex(2);
        WebElement msgField = driver.findElement(By.xpath("//textarea[@name='question_form[message]']"));
        msgField.sendKeys("test");
        WebElement commitBtn = driver.findElement(By.name("commit"));
        commitBtn.click();

        String errorMsg = driver.findElement(By.xpath("//div[contains(text(), 'reCAPTCHA verification')]")).getText();
        Assert.assertEquals(errorMsg, exp);
    }

    //1.  Открыть базовую ссылку
    //2.  Нажать пункт меню Support → Ask a question
    //3.  Оставить значение по умолчанию в checkbox Are you an OpenWeather user?
    //4. Оставить пустым поле Email
    //5. Заполнить поля  Subject, Message
    //6. Подтвердить CAPTCHA
    //7. Нажать кнопку Submit
    //8. Подтвердить, что в поле Email пользователю будет показана ошибка “can't be blank”
    @Test
    public void testEmailFieldCannotBeBlankInAskQuestion() throws InterruptedException {
        driver.manage().window().maximize();
        Thread.sleep(5000);
        String mainPage = driver.getWindowHandle();
        WebElement supportButton = driver.findElement(By.id("support-dropdown"));
        Assert.assertEquals(supportButton.getText(), "Support");
        supportButton.click();
        Thread.sleep(5000);
        WebElement askQuestion = driver.findElement(By.xpath("//ul[@id='support-dropdown-menu']//a[contains(text(), 'Ask a question')]"));
        askQuestion.click();
        for(String newTab : driver.getWindowHandles()){
            if(!newTab.equals(mainPage)){
                driver.switchTo().window(newTab);
            }
        }
        Thread.sleep(3000);
        WebElement checkDefaultRadioBtn = driver.findElement(By.id("question_form_is_user_false"));
        Assert.assertTrue(checkDefaultRadioBtn.isSelected());
        WebElement subjectEntry = driver.findElement(By.xpath("//select[@name='question_form[subject]']"));
        subjectEntry.click();
        Select select = new Select(subjectEntry);
        select.selectByIndex(2);
        WebElement msgField = driver.findElement(By.xpath("//textarea[@name='question_form[message]']"));
        msgField.sendKeys("test");

        WebElement recaptchaBtn = driver.findElement(By.xpath("//iframe[@title='reCAPTCHA']"));
        driver.switchTo().frame(recaptchaBtn);
        driver.findElement(By.xpath("//span[@id='recaptcha-anchor']")).click();
        Thread.sleep(7000);
        driver.switchTo().defaultContent();
        Thread.sleep(3000);
        WebElement commitBtn = driver.findElement(By.name("commit"));
        commitBtn.click();

        Thread.sleep(3000);

        String expErrorCannotHaveEmptyEmail = "can't be blank";
        String emptyEmailError = driver.findElement(By.xpath("//input[@id='question_form_email']//parent::div/span")).getText();
        Assert.assertEquals(emptyEmailError, expErrorCannotHaveEmptyEmail);

    }
/*
1.  Открыть базовую ссылку
2.  Нажать на единицы измерения Imperial: °F, mph
3.  Нажать на единицы измерения Metric: °C, m/s
4.  Подтвердить, что в результате этих действий, единицы измерения температуры изменились с F на С
 */
    @Test
    public void testTempUnitControls() throws InterruptedException {
        String expectedUnit = "C";
        Thread.sleep(5000);
        String currentUnit = driver.findElement(By.xpath("//span[@class='heading']")).getText();
        System.out.println(currentUnit);
        String defaultUnit = currentUnit.substring(currentUnit.length()-1);
        WebElement imperialF = driver.findElement(By.xpath("//div[contains(text(), 'Imperial: °F, mph')]"));
        imperialF.click();

        Thread.sleep(5000);
        WebElement metricC = driver.findElement(By.xpath("//div[contains(text(), 'Metric: °C, m/s')]"));
        metricC.click();
        String updatedUnit = driver.findElement(By.xpath("//span[@class='heading']")).getText();
        Assert.assertEquals(updatedUnit.substring(updatedUnit.length()-1), expectedUnit);
    }

    /*
    1.  Открыть базовую ссылку
    2.  Нажать на лого компании
    3.  Дождаться, когда произойдет перезагрузка сайта, и подтвердить, что текущая ссылка не изменилась
     */
    @Test
    public void testCompLogoAndVerifyURLDidnotChange() throws InterruptedException {
        Thread.sleep(5000);
        WebElement compLogo = driver.findElement(By.xpath("//ul[@id='first-level-nav']/li/a"));
        compLogo.click();
        Thread.sleep(5000);
        Assert.assertEquals(driver.getCurrentUrl(), url);
    }
    /*
    1.  Открыть базовую ссылку
    2.  В строке поиска в навигационной панели набрать “Rome”
    3.  Нажать клавишу Enter
    4.  Подтвердить, что вы перешли на страницу в ссылке которой содержатся слова “find” и “Rome”
    5. Подтвердить, что в строке поиска на новой странице вписано слово “Rome”
     */
    @Test
    public void testSearchRomeAndVerifyURLAndSearchFieldContainsRome() throws InterruptedException {
        Thread.sleep(5000);
        String searchText = "Rome";
        WebElement navSearch = driver.findElement(By.xpath("//div[@id='desktop-menu']//input[@name='q']"));
        navSearch.sendKeys(searchText, Keys.ENTER);
        Thread.sleep(5000);
        if(driver.getCurrentUrl().contains("find")){
            Assert.assertTrue(driver.getCurrentUrl().contains(searchText));
            Assert.assertEquals(driver.findElement(By.id("search_str")).getAttribute("value"), searchText);
        }
    }

    @Test
    public void testAPISelectionContains30OrangeBtns() throws InterruptedException {
        Thread.sleep(5000);
        int expNumOfOrangBtns = 30;
        WebElement apiSelection = driver.findElement(By.xpath("//div[@id='desktop-menu']//a[@href='/api']"));
        apiSelection.click();
        Thread.sleep(3000);
        List<WebElement> listOfOrangeBtns = driver.findElements(By.xpath("//a[contains(@class,'orange')]"));
        Assert.assertEquals(listOfOrangeBtns.size(), expNumOfOrangBtns);
        int elementsAreDisplayed = 0;
        for(WebElement el : listOfOrangeBtns){
            if(el.isDisplayed()){
                elementsAreDisplayed++;
            }
        }
        Assert.assertEquals(elementsAreDisplayed, expNumOfOrangBtns);
    }


    @AfterTest
    public void teardown(){
        driver.quit();
    }

}
