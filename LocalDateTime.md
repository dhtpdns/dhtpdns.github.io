LocalDateTime 설정하기
=============
클래스 설정
-------------
<pre><code>
@EnableJpaAuditing
@EntityScan(
        basePackageClasses = {Jsr310JpaConverters.class},  // basePackageClasses에 지정
        basePackages = {"com"})
public class SpringJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringJpaApplication.class, args);
    }
}
</code></pre>

Json 포메팅
-------------
<pre><code>
{
    "expirationDate": [ // 포멧팅전
          2018,
          12,
          12,
          0,
          0
        ],
 }
</code></pre>
<pre><code>
{
    "expirationDate": "2018-12-12T00:00:00", // 변경후
}
</code></pre>

Maven
-------------
```
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
</dependency>
```

gradle
-------------
<pre><code>
// https://mvnrepository.com/artifact/com.fasterxml.jackson.datatype/jackson-datatype-jsr310
compile group: 'com.fasterxml.jackson.datatype', name: 'jackson-datatype-jsr310', version: '2.9.9'
</code></pre>

# Spring Boot (version: 2.0.0^)
-------------

Spring Boot 2.0 버전 이상에서는 라이브러리를 추가하거나, 특정한 설정을 하지 않아도 LocalDateTime 클래스를 String 형태로 잘 리턴해준다. 그 이유는 Spring Boot 2.0부터는 jackson-datatype-jsr310를 내장하고 있기 때문이다.

하지만 다음과 같이 특정 어노테이션으로 인해 jackson-datatype-jsr310 이 무시되는 경우가 있다. 

```
@EnableWebMvc
@Configuration
public class WebMVCConfiguration implements WebMvcConfigurer {
	...
    // 특정한 설정 추가
    ...
}
```

@EnableWebMvc 어노테이션을 사용하면 Spring MVC 모드가 되어, Spring Boot의 Auto Configuration 기능이 활성화되지 않는다. 따라서 @EnableWebMvc를 활성화하고 LocalDateTime 값을 JSON 응답으로 받으면 다음과 같이 반환된다.
```
{
  "localDateTimeWithJSR310": [
    2019,
    6,
    19,
    8,
    50,
    18,
    3000000
  ]
}
```
WebMVC 설정이 필요한 경우 @EnableWebMvc 어노테이션을 사용하지말고, WebMvcConfigurer 인터페이스를 구현하여 설정을 적용한 뒤에 해당 클래스에 @Configuration 어노테이션을 붙여 스프링 부트가 설정 클래스로 인식하게 해야 한다
