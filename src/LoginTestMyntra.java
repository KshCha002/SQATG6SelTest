import java.util.Arrays;
import java.util.List;
import java.time.Duration;
import org.openqa.selenium.*;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.interactions.Actions;

public class LoginTestMyntra {

    public static void main(String[] args) {
        WebDriver driver = new SafariDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        Actions actions = new Actions(driver);

        List<String> phoneNumbers = Arrays.asList(
            "999999999",     // Min -1 (9 digits)
            "999999998",     // Min -2 (9 digits)
            "1000000000",    // Min
            "1000000001",    // Min +1
            "5555555555",    // Nominal
            "9999999998",    // Max -1
            "9999999999",    // Max
            "10000000000",    // Max +1 (11 digits)
            "aaaaaaaaaa", // Alphabetic
            "7088822002"    // 10 digits valid in use phone number
        );

        try {
            driver.get("https://www.myntra.com/");
            driver.manage().window().maximize();
            waitForPageLoad();

            // Hover over profile icon
            WebElement profileIcon = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[@class='desktop-userIconsContainer']")));
            actions.moveToElement(profileIcon).perform();
            System.out.println("Hovered over profile icon");

            // Click login/signup
            WebElement loginSignup = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[normalize-space()='login / Signup']")));
            loginSignup.click();
            waitForPageLoad();

            for (String number : phoneNumbers) {
                System.out.println("\n=== Testing with Phone Number: " + number + " ===");
                checkAndClickCheckbox(driver, wait);
                Thread.sleep(3000);
                enterPhoneNumber(driver, wait, number);

                if (isCaptchaPresent(driver)) {
                    System.out.println("CAPTCHA detected. Retrying after delay...");
                    Thread.sleep(31000);
                    enterPhoneNumber(driver, wait, number); // Retry
                }

                if (!checkResponse(driver, wait, number)) {
                    System.out.println("Phone Number: " + number + " → Unknown behavior");
                }

                driver.get("https://www.myntra.com/login?referer=https://www.myntra.com/");
                waitForPageLoad();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
            System.out.println("Test execution completed.");
        }
    }

    private static void enterPhoneNumber(WebDriver driver, WebDriverWait wait, String number) throws InterruptedException {
        WebElement phoneField = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//input[@type='tel']")));
        phoneField.clear();
        phoneField.sendKeys(number);
        System.out.println("Entered phone number: " + number);

        checkAndClickCheckbox(driver, wait);
        Thread.sleep(1000);
        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//div[@class='submitBottomOption' and contains(text(), 'CONTINUE')]")));
        continueButton.click();

        Thread.sleep(1000);  // Backend wait
    }

    private static void checkAndClickCheckbox(WebDriver driver, WebDriverWait wait) {
        try {
            WebElement checkbox = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@type='checkbox']")));
            if (!checkbox.isSelected()) {
                checkbox.click();
                System.out.println("Checked consent checkbox");
            }
        } catch (Exception e) {
            System.out.println("Checkbox not found or clickable.");
        }
    }

    private static boolean checkResponse(WebDriver driver, WebDriverWait wait, String number) {
        try {
            List<WebElement> errorPlaceholder = driver.findElements(
                By.cssSelector("span.errorActive.placeholderAlternative.mobileNumber"));
            if (!errorPlaceholder.isEmpty()) {
                System.out.println("Phone Number: " + number + " → Invalid input error shown.");
                return true;
            }
        } catch (Exception ignored) {}

        try {
            if (driver.getCurrentUrl().contains("otplogin")) {
                System.out.println("Phone Number: " + number + " → Valid (OTP Page Reached)");
                return true;
            }
        } catch (Exception ignored) {}

        try {
            WebElement failMessage = driver.findElement(By.xpath(
                "//*[@id=\"notifyMainDiv\"]/div"));
            if (failMessage.isDisplayed()) {
                System.out.println("Phone Number: " + number + " → Mobile Authentication Failed");
                return true;
            }
        } catch (NoSuchElementException ignored) {}

        return false;
    }

    private static boolean isCaptchaPresent(WebDriver driver) {
        try {
            WebElement captchaDiv = driver.findElement(By.id("sec-container"));
            return captchaDiv.isDisplayed();
        } catch (NoSuchElementException ignored) {}

        try {
            WebElement captchaIframe = driver.findElement(By.id("sec-cpt-if"));
            return captchaIframe.isDisplayed();
        } catch (NoSuchElementException ignored) {}

        return false;
    }

    private static void waitForPageLoad() throws InterruptedException {
        Thread.sleep(3000); 
    }
}

