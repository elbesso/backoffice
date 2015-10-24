package com.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import ru.oxygensoftware.backoffice.WebsiteApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebsiteApplication.class)
@WebAppConfiguration
public class WebsiteApplicationTests {

	@Test
	public void contextLoads() {
	}

}
