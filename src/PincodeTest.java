import org.openqa.selenium.*;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class PincodeTest {

    public static void main(String[] args) {
        String[] pincodes = {
            "99999", "100000", "100001", "500000", 
            "999998", "999999", "1000000", "11", 
            "9999", "244713"
        };

        WebDriver driver = new SafariDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        boolean wasLastValid = false;

        try {
            driver.manage().window().maximize();
            //driver.get("https://www.myntra.com/dresses/kaarigari/kaarigari-girls-embellished-velvet-sheath-dress/33396168/buy");
            driver.get("https://www.myntra.com/dresses/kaarigari/kaarigari-girls-embellished-velvet-sheath-dress/33396168/buy");

            for (int i = 0; i < pincodes.length; i++) {
                String pincode = pincodes[i];
                System.out.println("Testing pincode: " + pincode);

                try {
                    if (wasLastValid) {
                        try {
                            WebElement changeBtn = driver.findElement(
                                By.xpath("//button[contains(text(),'Change')]"));
                            if (changeBtn.isDisplayed() && changeBtn.isEnabled()) {
                                changeBtn.click();
                                Thread.sleep(1000);
                            }
                        } catch (NoSuchElementException e) {
                            //System.out.println("No 'Change' button — skipping.");
                        }
                    }

                    // Enter new pincode
                    WebElement input = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//input[@type='tel' or contains(@class,'pincode')]")));
                    input.clear();
                    input.sendKeys(pincode);

                    // Click the Check button
                    WebElement checkBtn = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("input.pincode-check.pincode-button")));
                    checkBtn.click();

                    Thread.sleep(2000); // Wait for response

                    String page = driver.getPageSource();

                    // Analyze result
                    if (page.contains("Please enter a valid pincode")) {
                        System.out.println(" Result: Invalid Pincode");
                        wasLastValid = false;
                    } else if (page.contains("Unfortunately we do not ship to your pincode")) {
                        System.out.println("Result: Valid but not deliverable");
                        wasLastValid = true;
                    } else if (page.contains("sprites-lightTick")) {
                        System.out.println(" Result:Valid and Deliverable");
                        wasLastValid = true;
                    } else {
                        System.out.println(" Result: Unknown");
                        wasLastValid = false;
                    }

                } catch (Exception e) {
                    System.out.println(" Failed to test pincode: " + pincode);
                    e.printStackTrace();
                    wasLastValid = false;
                }

                System.out.println("-------------------------------------------------");
            }

        } finally {
            driver.quit();
        }
    }
}
