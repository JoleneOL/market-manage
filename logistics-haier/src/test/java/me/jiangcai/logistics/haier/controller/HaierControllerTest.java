package me.jiangcai.logistics.haier.controller;

import me.jiangcai.logistics.LogisticsTestBase;
import me.jiangcai.logistics.haier.HaierConfig;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author CJ
 */
@WebAppConfiguration
@ContextConfiguration(classes = HaierConfig.class)
public class HaierControllerTest extends LogisticsTestBase {

    @Test
    public void go() throws Exception {
        mockMvc.perform(post("/_haier_callback")
                        .contentType("text/plain; charset=utf-8")
                        .content("outcode=LMA54543&notifytime=2017-08-03 00:11:01&source=LIMEIJIA&butype=rrs_outinstore&type=xml&sign=NjMyYTRjY2FiZTc2ODE1ZGViNGE0ODk1MzNjZWJmZTk=&content=W7IhXiMvVlmxKGP7Aclh1J1kS5REWIzmTrMzTC%2BXanCwQJ5J8rfEePWcZ%2FS%2FCWftU9aLhEyGWLAfZ%2BQLK7dyeQ7Tqu0CMLAQ3oc4%2FRH9QtPRXpHvuM8aaafBC15xXABjjkbwAsHgo49eY7ZoXfAb3NV8KizJiBQm5gGPEDSYRJOQjiLz1ztFL6Kg4CpkziAW3KlMe6KPijQW%2BP%2F2JGxnRX4sxVuQYdjZz1Kt6oSe651dOMS8NXjBv3q0%2BGEmLsmhxaj7JlXNi0UGRjcnd6jU%2BZ7uYXMQMtFZ6Fo4%2Fre7u4PUVTz%2BqYX7WTK9VQZxUMKIW0gfycg1c1NVjk6mMhHIkoF6uF634H%2BzIwhktdQEnJwSLWy4Itel9AgmvF04y%2B7zcUBQCe55oXsLAOa%2BrVkXdI%2FPbTAlkWTaOIaS5GMCU0FOzCIA69AHkgSFprY3qrEXnZcl5I68w76G2RFrpNQ%2BhiXXsAIZJJPpSLmNKhueQcNcEr3FumdBx7EcAYBAtHCoZBcTYS1QhPO7e0%2BcUB2Xek83ji6QUBJaHFg7UjeOBFhHqJfx3f7M2j3VjKxL4sdLwGnOu0GdvnR7PG%2FyGv%2FSYFOeYSjOy%2BReA0csNwMf3o9VZ6pmTcs1c4H53ouq5IbIRhc8TTSlYMhauSxJgdvwhkMbirHEjZIJ80x5qziq4u6kBzTyRoVD25YmK%2FPOy6vV3OpyCK%2FpvxKYUsbIcKgiPhPoPFdHohV%2F8ED7QagLf949B35ddgz%2FF29YMPOeWhM%2BJSwYAqhVLUJU30DxBEgxhCgVlN8ZfhWNmmjaXtW57PUTKCgomC014dbf4a1IiJeL5p3niD3hNQB4JDM1MhVFkTT9BrLklxnW7B%2F2rXzODy%2FjWfCsKP%2B3hM4auBSDxluyJaUB%2FjCWtfayl44BXIADEnwc1Fibusya9sb9kgRGIh8dCNF7fvrkp09UnnVS4i%2BUleTV6qY%2FR%2BeSr%2FPXtcj%2F%2BejYh%2BhvEGIV4oZb6wPT4dVng7hH4vG%2FQpUlrbMlvLUjme8pNVIelflXKB%2F3xPjXZr8BgCKJTY31jqJtmDYBMW%2Bs9H%2BmxpvYLiokKoH0jfkZAghCT8lt%2Bh4OvEHfHjdIoGbLGVsPyvDN2bKVinysCdBpIb47z%2BUrKvrFI0v48mZ8wbynRJSeFkbJBBmlmn8%2F3A%3D%3D")
//                .content("outcode=LM391101794&notifytime=2017-08-03 00:01:03&source=LIMEIJIA&butype=rrs_statusback&type=xml&sign=NmViMWJjM2NkY2FiZWQyNjE3YmY2NmU3ZmJlYWViOWE=&content=u96%2BhYYJBe0ajsRg8PX8jA%3D%3D")
        )
                .andDo(print());

        // 0803 001101 450
        mockMvc.perform(post("/_haier_callback")
                        .contentType("text/plain; charset=utf-8")
                        .content("outcode=LM391101794&notifytime=2017-08-03 00:11:01&source=LIMEIJIA&butype=rrs_statusback&type=xml&sign=MzQ0OTE5MTczNzhmYjUyMjg4YjEwN2E2ZmRmYTY4MTg=&content=HOmbh7KuceD2H18yBv2Hh8Wo%2ByZVzYtFBkY3J3eo1Pnyu0jacX6mgzwzbPzTC4KdlHuRdZBjo%2B5ZtaUIviJJS7IvZrzRy%2BVWv7i66mW5ZWMhLVjoFCgVslJgh7W2M%2F6sbog2DUQlqynokLn37xmjl7w5IuoFUj9z8EvlWfC4sS9Qx6GZKSpWudW9KrrTucFDHHwmJIP5cPHDjwdoycI6klNrPIRUmFe6OoOhBgw4lDIsyVS6BDsuDsZ1XdSOGigiTZje0LPzv5lKqMfsdQCW9%2BOb5uaoYOkP%2BQafc18lrZ9YwKBxOJNPt0ktK5z8aTsPTEyHEppSG8DncbmLHZ3b4iVu4zQUWiyGj09zDlebWfU%3D")
//                .content("outcode=LM391101794&notifytime=2017-08-03 00:01:03&source=LIMEIJIA&butype=rrs_statusback&type=xml&sign=NmViMWJjM2NkY2FiZWQyNjE3YmY2NmU3ZmJlYWViOWE=&content=u96%2BhYYJBe0ajsRg8PX8jA%3D%3D")
        )
                .andDo(print());

    }

}