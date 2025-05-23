public class TestCase {
    String id;
    String username;
    String password;
    String expectedResult;

    public TestCase(String id, String username, String password, String expectedResult) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.expectedResult = expectedResult;
    }
}