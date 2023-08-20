package org.webscraper;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class WebScraper {

    public static final String URL = "https://twitter.com/asianpaints/with_replies";

    public static final String TWEET_CLASS = "div[data-testid='tweetText']";

    public static final String USERNAME = "sudhat1700";
    public static final String PASSWORD = "xxxxxxxxx";

    public static final int SCROLL_TIME = 60000 * 20;

    public static void main(String[] args) throws IOException {
        // Set the path to the ChromeDriver executable
        System.setProperty("webdriver.chrome.driver", "C:/chromedriver.exe");

        // Create a new instance of ChromeDriver
        WebDriver driver = new ChromeDriver();
        FileWriter fileWriter = new FileWriter("output.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        try {
            // Navigate to the desired URL
            driver.get(URL);

            // Create an explicit wait with a timeout of 10 seconds
            WebDriverWait wait = new WebDriverWait(driver, 10);

            // enter username and press next
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("input")));
            usernameField.sendKeys(USERNAME);
            WebElement nextButton = driver.findElement(By.xpath(getXPathExpression("Next")));
            nextButton.click();

            // enter pass and press login
            WebElement passField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("input")));
//            passField.sendKeys(PASSWORD);

            // Scroll down to trigger loading more content
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            long endTime = System.currentTimeMillis() + SCROLL_TIME;

            while (System.currentTimeMillis() < endTime) {
                // Scroll down
                jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                Thread.sleep(1000);  // Wait for .5 second between scrolls
                List<WebElement> divElements = driver.findElements(By.cssSelector(TWEET_CLASS));
                for (WebElement tweet : divElements) {
                    bufferedWriter.write(tweet.getText());
                    bufferedWriter.newLine();
                    bufferedWriter.newLine();
                }
                Thread.sleep(500);  // Wait for .5 second between scrolls
            }

        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            driver.quit();
            bufferedWriter.close();
        }
    }

    private static String getXPathExpression(String searchText) {
        return "//*[contains(text(), '" + searchText + "')]";
    }
}
