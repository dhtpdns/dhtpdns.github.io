Spring "Field Injection"? or "Constructor Injection"?
=============
### Intro
* Spring Framework에서 제공하는 @Autowired나 @Qualifier를 이용해서 객체를 찾아서 DI(Dependency Injection) 
물론 Java에서도 @Inject, @Resource를 제공해주어서 DI(Dependency Injection)를 할 수 있다.
* 우선, DI는 IoC(Inversion of Control)의 핵심 원리를 구현하는 개념.
### 간단하게 IoC(Inversion of Control)란? 
* IoC의 핵심은 기존의 Programing code 안에 들어가 있던 객체의 생성/관리를 Spring Container에게 위임하여 객체의 생명주기를 관리하게 하는 것입니다. 관리하는 객체의 단위를 Bean이라고 명명합니다.
* IoC를 구현하기 위해서는 DI를 통해서 Bean을 생성하는 시점에 필요한 객체를 주입해야 함.
* 결국, 우리가 이야기하는 핵심은 "언제 어떻게 Bean을 생성하는 시점에 필요한 객체를 주입해야 하는가?"


### Field Injection인 @Autowired
![IDE에서 보여주는 경고](img/T-1.PNG)
* @Autowired에 대한 노란색 경고.

![Injection 경고 문구](img/T-2.PNG)
* Field injection을 추천하지 않는다.
* Spring Team은 너의 Bean들에게 Constructor 기반의 DI를 항상 사용해라.
* 필수적인 의존성들을 위한 assertions를 사용해라.

위 문구는 한 문장으로 "Constructor based injection을 써라. 안 쓰면 DI를 보장 못할 수 있어."


## Field Injection 이 가지고 있는 문제?

* 우리는 Java를 사용하면서, 가장 많이 듣는말. OOP(Object Oriented Programming).
* OOP는 낮은 "결합성", 높은 "응집성" 구현하기 위한 개념.
* 결국, "Inheritance", "Polymolphism", "Encapsulation", "Abstraction"는 낮은 "결합성", 높은 "응집성" 구현하기 위한 개념.
* IoC의 DI를 통해서 우리는 낮은 "결합성"을 구현할 수 있음.
* 여기까지는 "Setter Injection", "Constructor Injection", "Field Injection" 모두 아무런 문제를 발견할 수 없음.

* Code를 통한 Injection : "Setter Injection", "Constructor Injection"
* Annotation을 통한 Injection : "Field Injection"

#### Setter Injection"에 대한 코드
<pre><code>
@Service
public class UserService {
    private UserRepository userRepository;

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(long userId) {
        return userRepository.findById(userId);
    }
}
@Controller
public class UserController {
	
    @GetMapping("/users/{id}")
    public User getUserInfo(@PathVariable long id) {
    	UserService userService = new UserService();
        userService.setUserRepository(new UserRepository());
        
        return userService.getUser(id);
    }
}
</code></pre>

여기까지는 정상적으로 new UserRepository()를 setter에 전달해주었기 때문에 문제가 없다.

#### 개발자의 실수

<pre><code>
@Controller public class UserController { 
@GetMapping("/users/{id}") 
public User getUserInfo(@PathVariable long id) { 
    UserService userService = new UserService(); 
    // userService.setUserRepository(new UserRepository());
    return userService.getUser(id);
    } 
}
</code></pre>

new UserRepository()를 setter로 전달해주는 것을 까먹은 것.

문제는 CompileTime에 이 실수를 잡아주지 않음

UserService는 생성이 되고, 결국 RunTime에 userService.getUser(id);가 호출되는 시점에 NullPointException()이 발생.

Field Injection도 Setter Injection과 마찬가지로 Spring Container가 주입해야할 new UserRepository()를 주입하지 않아도  UserService가 생성되는 문제.

결국 Setter Injection처럼 userService.getUser(id);가 호출되는 시점에 NullPointException()이 발생..

<pre><code>
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;//<- null

    public User getUser(long userId) {
        return userRepository.findById(userId);
    }
}
@Controller
public class UserController {
	
    @GetMapping("/users/{id}")
    public User getUserInfo(@PathVariable long id) {
    	UserService userService = new UserService();
        
        return userService.getUser(id);
    }
}
</code></pre>

차이점은 주입을 setter로 프로그래머가 직접 하는가, 아니면 @Autowired로 Spring Container가 해주는가

여기까지 Setter Injection / Field Injection의 단점
* CompileTime에 문제가 발견되지 않고, RunTime에 Method를 호출하게 되면, NPE가 발생하는 것

## Constructor Injection 장점

Service Layer의 Business Logic이 복잡해지면 복잡해질수록, 우리는 주입된 Bean들이 엄청 늘어나는 것을 볼 수 있다.
Bean들이 늘어나면서, 서로 다른 Service에서 거미줄처럼 주입되어있게 되는데,
Annotation "@Autowired"을 통한 Field Injection은 CompileTime에 순환 참조를 확인할 수 없다.



## 이유
Setter Injection / Field Injection은 일단 모든 Bean을 생성하여 Bean Factory에 등록.
Bean이 모두 생성되어 Bean Factory에 등록되고나면, 주입할 Bean들을 가져와서 주입.
결국 순환 참조를 알아챌 타이밍을 이 시점에는 놓쳐버리게 됨.
이제 순환 참조가 만들 지옥은 Runtime에 Business Logic에서 발생.
결국에 나도 모르는 사이에 Runtime 순환 참조가 발생할 수 있는 가능성이 생김.


### Field Injection을 통한 순환 참조 문제 발생
* 순환 참조된 상태의 Bean이 2개가 생성.
* 순환 참조된 상태의 Bean A와 Bean B가 Business Logic에 서로 Method를 호출하는 Method A를 만듬.
* Method A가 Runtime에 호출.
* 서로 호출되면서 Recusive 방식으로 Stack에 호출된 Method가 쌓임.
* 결국 Stack의 Memory Size가 넘어가면서, java.lang.StackOverflowError가 발생.


```
@Service public class AService { 
    @Autowired private BService bService; 
    public void aMethod() { 
        bService.bMethod(); 
        } 
    }
```
```aidl
@Service
public class BService {

    @Autowired
    private AService aService;

    public void bMethod() {
        aService.aMethod();
    }
}
aService.aMethod(); //가 호출되는 순간 StackOverflowError가 발생됩니다.
```
그래서 우리는 애초에 Bean이 주입되는 시점에 순환 참조의 가능성을 막아야 함.

이 시점에 IDE에서 Constructor Injection을 추천하는 이유.

결론부터 이야기를 하면,

Spring Container가 Constructor Injection을 사용하면 Application 실행 시점에 Bean이 순환 참조되었다는 것을 다음처럼 BeanCreationException으로 알려줌.

### 알려줄 수 있는 이유
* Setter Injection / Field Injection과 다르게 Constructor Injection은 생성자로 빈을 주입.
* 생성자로 빈을 주입한다는 이야기는 자바에서 객체가 생성이 되려면, 주입될 Bean의 소재지를 찾아서 넣어줘야한다는 것.
* 결국 생성 시점에 모든 Bean이 정상적인 상태가 아니면 에러를 발생시킬 수 있다는 것.
* 자바의 객체 생성 원리가 Bean의 순환 참조 가능성을 제거 가능.


Constructor Injection을 통한 순환 참조 예시
```aidl
@Service
public class AService {

    private final BService bService;
    
    public AService(BService bService) {
        this.bService = bService;
    }

    public void aMethod() {
        bService.bMethod();
    }
}
@Service
public class BService {

    private final AService aService;
    
    public BService(AService aService) {
        this.aService = aService;
    }

    public void bMethod() {
        aService.aMethod();
    }
}
***************************
APPLICATION FAILED TO START
***************************

Description:

The dependencies of some of the beans in the application context form a cycle:

┌─────┐
|  AService defined in file [AService.class]
↑     ↓
|  BService defined in file [BService.class]
└─────┘

```
*  Constructor Injection을 이용하면, final 키워드를 사용 가능.
* final은 상수를 만드는 키워드 입니다. 한 번 주입된 객체를 다른 객체로 변경을 불가능.
* 흔히 "Immutable Object"라 이야기 많이 함. S
* pring Container가 아니면 Tread Safe 이야기도 나올 수 있는 부분.
* 물론 primitive가 아니기 때문에 객체 내용물의 상태는 변화 가능. 하지만 객체 자체를 수정하는 것은 불가능.
* 그래서 Bean 주입을 위해서 쓰이기에는 최적.

## 결론
Field Injection은 다음과 같은 문제점.
* 주입되지 않은 Bean으로 인한 NullPointException 발생 가능성
* Runtime에 순환 참조로 인한 StackOverflowError 발생 가능성

그래서 사전에 위 문제점을 해결이 가능한 Construction Injection을 사용하는 것.

## 장점
* Bean이 주입되지 않는 경우, Compile Time에 알려주어서 NullPointException을 예방.
* Bean이 순환 참조될 경우를 Spring Container가 미리 알려주기 때문에 순환참조 가능성을 제거.
* final로 "Immutable Object"를 만들어버리기 때문에 Application이 재가동이 되기 전까지는 불변하는 Bean을 사용 가능.


출처: https://interconnection.tistory.com/124