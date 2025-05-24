// import java.util.Arrays;
// import java.util.List;
// import java.time.Duration;
// import org.openqa.selenium.*;
// import org.openqa.selenium.safari.SafariDriver;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;
// import org.openqa.selenium.interactions.Actions;

// public class IrctcLoginTest{

//     public static void main(String[] args) {
//         WebDriver driver = new SafariDriver();
//         WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
//         Actions actions = new Actions(driver);

//         List<String> phoneNumbers = Arrays.asList(
//             "999999999",     // Min -1 (9 digits)
//             "999999998",     // Min -2 (9 digits)
//             "1000000000",    // Min
//             "1000000001",    // Min +1
//             "5555555555",    // Nominal
//             "9999999998",    // Max -1
//             "9999999999",    // Max
//             "10000000000",    // Max +1 (11 digits)
//             "aaaaaaaaaa", // Alphabetic
//             "7088822002"    // 10 digits valid in use phone number
//         );

//         try {
//             driver.get("https://www.irctc.co.in/nget/profile/user-signup");
//             driver.manage().window().maximize();
//             waitForPageLoad();


//         } catch (Exception e) {
//             e.printStackTrace();
//         } finally {
//             driver.quit();
//             System.out.println("Test execution completed.");
//         }
//     }
// }
import org.openqa.selenium.*;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class IrctcSignupTest {

    public static void main(String[] args) throws InterruptedException {
        WebDriver driver = new SafariDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        List<TestCase> testCases = getTestCases();

        for (TestCase test : testCases) {
            driver.get("https://www.irctc.co.in/nget/profile/user-signup");
            driver.manage().window().maximize();
            Thread.sleep(10000);  // Let page load

            // Open Registration form (if necessary)
            try {
                driver.findElement(By.xpath("//a[contains(text(), 'REGISTER')]")).click();
                Thread.sleep(3000);
            } catch (Exception ignored) {}

            // Find input fields
            WebElement usernameInput = driver.findElement(By.id("userName"));
            WebElement passwordInput = driver.findElement(By.id("usrPwd"));

            // --- Username Check ---
            usernameInput.clear();
            usernameInput.sendKeys(test.username + Keys.ENTER);
            Thread.sleep(2000);  // Wait for error to appear

            String usernameError = "";
            try {
                WebElement userErr = driver.findElement(By.xpath(
                    "//*[@id='divMain']/div/app-user-signup/div/div/form/div/div[2]/div/div[2]/div[2]/div/div/div/span"));
                usernameError = userErr.getText().trim();
            } catch (Exception ignored) {}

            // --- Password Check ---
            passwordInput.clear();
            passwordInput.sendKeys(test.password + Keys.ENTER);
            Thread.sleep(2000);

            String passwordError = "";
            try {
                WebElement passErr = driver.findElement(By.xpath(
                    "//*[@id='divMain']/div/app-user-signup/div/div/form/div/div[2]/div/div[2]/div[4]/div[1]/div/div/span[3]"));
                passwordError = passErr.getText().trim();
            } catch (Exception ignored) {}

            // Determine outcome
            String actualResult;
            if (!usernameError.isEmpty() && !passwordError.isEmpty()) {
                actualResult = "Both Invalid";
            } else if (!usernameError.isEmpty()) {
                actualResult = "username invalid";
            } else if (!passwordError.isEmpty()) {
                actualResult = "password invalid";
            } else {
                actualResult = "Valid";
            }

            System.out.printf("TestCase: %s | Username: %-35s | Password: %-20s | Expected: %-15s | Actual: %-15s | %s\n",
                    test.id, test.username, test.password, test.expectedResult, actualResult,
                    test.expectedResult.equals(actualResult) ? "PASS" : "FAIL");

            Thread.sleep(1000);
        }

        driver.quit();
    }

    

        public static List<TestCase> getTestCases() {
            List<TestCase> list = new ArrayList<>();
        
            list.add(new TestCase("TC_01", "us", "Aa1!bB2@Cd", "Both Invalid"));
            list.add(new TestCase("TC_09", "usr", "Aa1!bB2@Cd3", "Valid"));
            list.add(new TestCase("TC_13", "usr", "Aa1!bB2@Cd3$EfG", "Valid"));
            list.add(new TestCase("TC_14", "usr", "Aa1!bB2@Cd3$EfG4", "password invalid"));
            list.add(new TestCase("TC_16", "us", "Aa1!bB2@Cd3", "username invalid"));
            list.add(new TestCase("TC_21", "user", "Aa1!bB2@Cd3$EfG4", "password invalid"));
            list.add(new TestCase("TC_25", "usernameisnineteenn", "Aa1!bB2@Cd3$e", "Valid"));
            list.add(new TestCase("TC_27", "usernameisnineteenn", "Aa1!bB2@Cd3$EfG", "Valid"));
            list.add(new TestCase("TC_29", "MyUserNameIsThirtyFourCharacters34", "Aa1!bB2@Cd", "Valid"));
            list.add(new TestCase("TC_34", "MyUserNameIsThirtyFourCharacters34", "Aa1!bB2@Cd3$EfG", "Valid"));
            list.add(new TestCase("TC_36", "MyUserNameHasThirtyFiveCharacters35", "Aa1!bB2@Cd3$EfG4", "password invalid"));
            list.add(new TestCase("TC_41", "MyUserNameHasThirtyFiveCharacters35", "Aa1!bB2@Cd3$EfG", "Valid"));
            list.add(new TestCase("TC_49", "MyUserNameUsingThirtySixCharacters36", "Aa1!bB2@Cd3$EfG4", "Both Invalid"));
        
            return list;
        }

      
    // Static inner class for test case
    static class TestCase {
        String id;
        String username;
        String password;
        String expectedResult;

        TestCase(String id, String username, String password, String expectedResult) {
            this.id = id;
            this.username = username;
            this.password = password;
            this.expectedResult = expectedResult;
        }
    }
}
