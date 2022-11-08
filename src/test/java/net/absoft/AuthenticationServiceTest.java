package net.absoft;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.absoft.data.Response;
import net.absoft.services.AuthenticationService;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import static org.testng.Assert.*;

public class AuthenticationServiceTest {
  private AuthenticationService authenticationService;

  @BeforeMethod (groups = {"positive", "negative"})
  public void setUp() {
    authenticationService = new AuthenticationService();
    System.out.println("setup");
  }

  @Test(
          groups = "positive"
  )
  public void testSample() throws InterruptedException {
    Thread.sleep(2000);
    System.out.println("testSample: " + new Date());
    fail("FAILING TEST");
  }

  @Test(
          description = "Test Successful Authentication",
          groups = "positive",
          invocationCount = 4
  )
  @Parameters({"email-address", "password"})
  public void testSuccessfulAuthentication(@Optional("user1@test.com") String email, @Optional("password1") String password) throws InterruptedException {
    Response response = authenticationService.authenticate(email, password);
    assertEquals(response.getCode(), 200, "Response code should be 200");
    assertTrue(validateToken(response.getMessage()),
        "Token should be the 32 digits string. Got: " + response.getMessage());

    Thread.sleep(2000);
    System.out.println("testSuccessfulAuthentication: " + new Date());
  }

  @DataProvider(name = "invalidLogins", parallel = true)
  public Object[][] invalidLogins() {
    return new Object[][] {
            new Object[] {"use1@text.com", "wrong_password1", new Response(401, "Invalid email or password")},
            new Object[] {"", "password1", new Response(400, "Email should not be empty string")},
            new Object[] {"use1@text.com", "", new Response(400, "Password should not be empty string")},
            new Object[] {"use1", "password1", new Response(400, "Invalid email")}
    };
  }
  @Test(
          groups = "negative",
          dataProvider = "invalidLogins"
  )
  public void testInvalidAuthentication(String email, String password, Response expectedResponse) throws InterruptedException {
    Response actualResponse = authenticationService.authenticate(email, password);
    assertEquals(actualResponse, expectedResponse,"Unexpected response");

    Thread.sleep(2000);
    System.out.println("testInvalidAuthentication: " + new Date());
  }


  @Test(
          groups = "negative")
  public void testAuthenticationWithWrongPassword() {
    validateErrorResponse(
            authenticationService.authenticate("use1@text.com", "wrong_password1"),
            401,
            "Invalid email or password"
    );
  }
  private void validateErrorResponse(Response response, int code, String message) {
    SoftAssert sa = new SoftAssert();
    sa.assertEquals(response.getCode(), code, "Response code should be 401");
    sa.assertEquals(response.getMessage(), message,
            "Response message should be \"Invalid email or password\"");
    sa.assertAll();
    System.out.println("testAuthenticationWithWrongPassword");
  }

//  @Test(
//          priority = 3,
//          groups = "negative",
//          dependsOnMethods = {"testAuthenticationWithEmptyPassword"},
//          alwaysRun = true
//  )
//  public void testAuthenticationWithEmptyEmail() {
//    Response response = authenticationService.authenticate("", "password1");
//    assertEquals(response.getCode(), 400, "Response code should be 400");
//    assertEquals(response.getMessage(), "Email should not be empty string",
//        "Response message should be \"Email should not be empty string\"");
//    System.out.println("testAuthenticationWithEmptyEmail");
//  }
//
//  @Test(
//          groups = "negative"
//  )
//  public void testAuthenticationWithInvalidEmail() throws InterruptedException {
//    Response response = authenticationService.authenticate("user1", "password1");
//    assertEquals(response.getCode(), 400, "Response code should be 200");
//    assertEquals(response.getMessage(), "Invalid email",
//        "Response message should be \"Invalid email\"");
//    System.out.println("testAuthenticationWithInvalidEmail");
//  }
//
//  @Test(
//          groups = "negative",
//          priority = 2,
//          dependsOnMethods = {"testAuthenticationWithInvalidEmail"}
//  )
//  public void testAuthenticationWithEmptyPassword() {
//    Response response = authenticationService.authenticate("user1@test", "");
//    assertEquals(response.getCode(), 400, "Response code should be 400");
//    assertEquals(response.getMessage(), "Password should not be empty string",
//        "Response message should be \"Password should not be empty string\"");
//    System.out.println("testAuthenticationWithEmptyPassword");
//  }

  private boolean validateToken(String token) {
    final Pattern pattern = Pattern.compile("\\S{32}", Pattern.MULTILINE);
    final Matcher matcher = pattern.matcher(token);
    return matcher.matches();
  }
}
