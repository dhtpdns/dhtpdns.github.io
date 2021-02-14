Spring MockMvc?
=============
### 1. Intro
* MockMvc는 기존의 MockHttpServletRequest, MockHttpServletResponse을 활용한 단위 테스트에서 발전 
* 기존의 Mock 객체들은 충분한 검증 결과Annotaions, Request, etc. 를 포함한를 보여 줄 수 없었기 때문.
* 그래서 spring-test 모듈을 스프링 프레임워크에 더해짐.
### 2. How to use Spring MockMvc

### 2.1 Create Project
* 프로젝트 생성

### 2.2 Update pom.xml for Spring
* pom.xml 파일을 갱신. 아래와 같은 부분을 추가하거나 수정해 주세요. Springboot는 이 부분을 무시.
```
<properties>
	<java-version>1.8</java-version>
	<org.springframework-version>4.1.7.RELEASE</org.springframework-version>
	<org.aspectj-version>1.9.6</org.aspectj-version>
	<org.slf4j-version>1.7.25</org.slf4j-version>
</properties>
<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring-test</artifactId>
	<version>${org.springframework-version}</version>
</dependency>
<dependency>
	<groupId>junit</groupId>
	<artifactId>junit</artifactId>
	<version>4.9</version>
	<scope>test</scope>
</dependency>
<dependency>
	<groupId>javax.servlet</groupId>
	<artifactId>javax.servlet-api</artifactId>
	<version>3.1.0</version>
</dependency>
```

## 2.3 Controller
```
package kr.ose.junit;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);

		Date date = new Date();
		DateFormat dateFormat = DateFormat
								.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate );

		return "home";
	}
}
```
### 2.4 Setup
* 셋업 코드를 작성. 두 가지의 테스트 클래스가 작성됨. 해당 클래스들은 src/test/java 디렉토리 하위 kr.ose.junit 패키지 경로에 만들어짐

###  2.4.1 Setup with StandAlone
```
package kr.ose.junit;

// 이부분 추가하셔야 합니다.
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class StandAloneTest {

	private MockMvc mockMvc;

	// 테스트 메소드 실행전 셋업 메소드
	@Before
	public void setup(){
		// 이곳에서 HomeController를 MockMvc 객체로 만든다.
		this.mockMvc = MockMvcBuilders.standaloneSetup(new HomeController()).build();
	}

	@Test
	public void test() throws Exception{
		// HomeController의 "/" 매핑으로 정의합니다.
		this.mockMvc.perform(get("/"))
		// 처리 내용을 출력합니다.
		.andDo(print())
		// 상태값은 OK가 나와야 합니다.
		.andExpect(status().isOk())
		// "serverTime"이라는 attribute가 존재해야 합니다.
		.andExpect(model().attributeExists("serverTime"));
	}
}
```

```
INFO : kr.ose.junit.HomeController - Welcome home! The client locale is en.

MockHttpServletRequest:
         HTTP Method = GET
         Request URI = /
          Parameters = {}
             Headers = {}

             Handler:
                Type = kr.ose.junit.HomeController
              Method = public java.lang.String kr.ose.junit.HomeController.home(java.util.Locale,org.springframework.ui.Model)

               Async:
       Async started = false
        Async result = null

  Resolved Exception:
                Type = null

        ModelAndView:
           View name = home
                View = null
           Attribute = serverTime
               value = Feb 14, 2021 9:42:51 PM KST

            FlashMap:

MockHttpServletResponse:
              Status = 200
       Error message = null
             Headers = {}
        Content type = null
                Body =
       Forwarded URL = home
      Redirected URL = null
             Cookies = []
```

### 2.4.2 Setup with Application Configuration
```
package kr.ose.junit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml")
public class ContextConfigTest {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@Before
	public void setup(){
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
	}

	@Test
	public void test() throws Exception{
		 this.mockMvc.perform(get("/"))
		 .andDo(print())
		 .andExpect(status().isOk())
		 .andExpect(model().attributeExists("serverTime"));
	}
}
```

```
INFO : kr.ose.junit.HomeController - Welcome home! The client locale is en.

MockHttpServletRequest:
         HTTP Method = GET
         Request URI = /
          Parameters = {}
             Headers = {}

             Handler:
                Type = kr.ose.junit.HomeController
              Method = public java.lang.String kr.ose.junit.HomeController.home(java.util.Locale,org.springframework.ui.Model)

               Async:
       Async started = false
        Async result = null

  Resolved Exception:
                Type = null

        ModelAndView:
           View name = home
                View = null
           Attribute = serverTime
               value = Feb 14, 2015 9:43:56 PM KST

            FlashMap:

MockHttpServletResponse:
              Status = 200
       Error message = null
             Headers = {}
        Content type = null
                Body =
       Forwarded URL = /WEB-INF/views/home.jsp
      Redirected URL = null
             Cookies = []
```

### 2.5 Result Console
* 위의 두가지 테스트 클래스의 Console 결과는 비슷하지만 조금씩 차이도 있다. 이부분은 우선 참고만.

### 3. Analyze MockMvc
### 3.1  MockMvc
* 해당 클래스는 MockMvc Doc에서 확인 가능합니다. 지원하는 메소드는 .perform()입니다. 이 메소드가 리턴하는 객체는 ResultActions라는 인터페이스
```
this.mockMvc.perform(get("/")) // basic
this.mockMvc.perform(post("/")) // send post
this.mockMvc.perform(get("/?foo={var}", "1")) // query string
this.mockMvc.perform(get("/").param("bar", "2")) // using param
this.mockMvc.perform(get("/").accept(MediaType.ALL)) // select media type
```
* post(), put(), delete() 등도 가능합니다.
* 혹시라도 Filter 처리를 하고 싶다면 MockMvc를 초기화 할때 build() 메소드 호출 전 .addFilter 또는 .addFilters 메소드를 호출해서 필터를 등록.

### 3.2 ResultActions
* MockMvc.perform 메소드로 리턴되는 인터페이스.
* ResultActions Doc에서 확인 가능.
* 지원 메소드는 andExpect, andDo, andReturn.

### 3.2.1 andExpert
* 예상값을 검증. assert* 메소드들과 유사
```aidl
// status 값이 정상인 경우를 기대하고 만든 체이닝 메소드 중 일부입니다.
.andExpect(status().isOk())
// contentType을 검증합니다.
.andExpect(content().contentType("application/json;charset=utf-8"))
```
* 혹시라도 mockMvc 객체로 테스트 중 한글이 깨지는 경우 컨트롤러 매핑을 확인
```aidl
@RequestMapping(value = "/", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
```
*위처럼 produces 부분에 캐릭터셋도 확실하게 지정을 해줘야 함.

### 3.2.2 andDo
* 요청에 대한 처리를 합니다. print() 메소드가 일반적
```aidl
.andDo(print())
```

### 3.2.3 andReturn
* 테스트 클래스에서 작성은 안했지만 테스트한 결과 객체를 받을 때 사용
```aidl
MvcResult result = this.mockMvc.perform(get("/"))
.andDo(print())
.andExpect(status().isOk())
.andExpect(model().attributeExists("serverTime"))
.andReturn();
```

### 4. Setup with Application Configuration for Springboot
* Springboot에서는 아래처럼
```aidl
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DemoApplication.class)
@WebAppConfiguration
public class TestClass {
	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	@Before
	public void setup(){
		this.mvc = MockMvcBuilders.webAppContextSetup(this.context).build();
	}

	@Test
	public void contextLoads() throws Exception {
		this.mvc.perform(post("/").param("test", "한글")).andDo(print())
		.andExpect(content().contentType("application/json;charset=utf-8"));
	}
}
```

### 5.  jsonPath
```aidl
/*
{"message":"val"} 이라는 response를 받았는지 검증하려면 아래처럼 사용
*/
.andExpect(jsonPath("$.message").value("val"))
```

