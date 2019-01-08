### > zookeeper启动报错解决办法：删除你自己设定的dataDir路径下的version-2文件夹，然后重启即可。

```properties
D:\tmp\zookeeper
```



### > 服务提供者暴露的接口需要添加的注解

```java
*@Component
*@Service(interfaceClass = FilmAPI.class)
public class DefaultFilmServiceImpl implements FilmAPI {

    @Autowired
    private MoocBannerTMapper moocBannerTMapper;
    @Autowired
    private MoocFilmTMapper moocFilmTMapper;
    
}
```

### > 服务提供者配置文件

> - server 的端口不能重复
> - dubbo 的端口不能重复

```properties
rest:
  auth-open: false #jwt鉴权机制是否开启(true或者false)
  sign-open: false #签名机制是否开启(true或false)

jwt:
  header: Authorization   #http请求头所需要的字段
  secret: mySecret        #jwt秘钥
  expiration: 604800      #7天 单位:秒
  auth-path: auth         #认证请求的路径
  md5-key: randomKey      #md5加密混淆key

server:
  port: 8084 #项目端口

mybatis-plus:
  mapper-locations: classpath*:com/stylefeng/guns/rest/**/mapping/*.xml
  typeAliasesPackage: com.stylefeng.guns.rest.common.persistence.model
  global-config:
    id-type: 0  #0:数据库ID自增   1:用户输入id  2:全局唯一id(IdWorker)  3:全局唯一ID(uuid)
    db-column-underline: false
    refresh-mapper: true
  configuration:
    map-underscore-to-camel-case: false
    cache-enabled: true #配置的缓存的全局开关
    lazyLoadingEnabled: true #延时加载的开关
    multipleResultSetsEnabled: true #开启的话，延时加载一个属性时会加载该对象全部属性，否则按需加载属性
#    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl #打印sql语句,调试用

spring:
  application:
    name: metting-film
  dubbo:
    server: true
    registry: zookeeper://localhost:2181
    protocol:
      name: dubbo
      #name: rmi
      port: 20885
  datasource:
      #url: jdbc:mysql://127.0.0.1:3306/guns_rest?autoReconnect=true&useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull
      url: jdbc:mysql://127.0.0.1:3306/guns_rest?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8&useSSL=false&failOverReadOnly=false
      username: root
      password: 123456
      filters: log4j,wall,mergeStat

logging:
  level.root: info
  level.com.stylefeng: debug
  path: logs/
  file: guns-rest.log
```

### > 消费服务的controller层添加的注解

```java
@RestController
@RequestMapping("film")
public class FilmController {

    private static final String IMG = "http://img.maoyan.com";

    *@Reference(interfaceClass = FilmAPI.class,check = false)
    FilmAPI filmAPI;
}
```

### > 一对多的查询语句

```xml
<!-- 一对多的查询 -->
<resultMap id="getFilmInfoMap" type="com.stylefeng.guns.api.cinema.vo.FilmInfoVO">
    <result column="film_id" property="filmId"></result>
    <result column="film_name" property="filmName"></result>
    <result column="film_length" property="filmLength"></result>
    <result column="film_language" property="filmType"></result>
    <result column="film_cats" property="filmCats"></result>
    <result column="actors" property="actors"></result>
    <result column="img_address" property="imgAddress"></result>
    <collection property="filmFields" ofType="com.stylefeng.guns.api.cinema.vo.FilmFieldVO">
        <result column="UUID" property="fieldId"></result>
        <result column="begin_time" property="beginTime"></result>
        <result column="end_time" property="endTime"></result>
        <result column="film_language" property="language"></result>
        <result column="hall_name" property="hallName"></result>
        <result column="price" property="price"></result>
    </collection>
</resultMap>
```

### > 热点数据的处理-redis/结果缓存

- Dubbo可以通过注解对热点数据进行缓存
- 了解Dubbo结果缓存于Redis等的区别

### > 缓存类型

- lru 基于最近最少使用原则删除多余缓存，保持最热的数据被缓存
- threadlocal 当前线程缓存，比如一个页面渲染，用到很多portal，每个portal都要去查用户信息，通过线程缓存，可以减少多余访问。
- jcache yuJSR107集成，可以桥接各种缓存实现。

### > 配置

```xml
<dubbo:reference interface="com.foo.BasrService" cache="lru" />
```

### 或：

```xml
<dubbo:reference interface="com.foo.BasrService">
	<dubbo:method name="findBar" cache="lru" />
</dubbo:reference>
```

### 在controller层配置cache

```java
@Reference(interfaceClass = CinemaAPI.class,cache = "lru",check = false)
    CinemaAPI cinemaAPI;
```



### > 并发、连接控制

- Dubbo可以对连接和兵法数量进行控制
- 超出部门以错误形式返回

#### 服务端连接控制

##### 限制服务器端接受的连接不能超过10个

```xml
<dubbo:provider protocol="dubbo" accepts="10" />
```

##### 或

```xml
<dubbo:protocol name="dubbo" accepts="10" />
```

#### 客户端连接控制

##### 限制客户端服务使用连接不能超过10

```xml
<dubbo:reference interface="com.foo.BarService" connections="10" />
```

##### 或

```xml
<dubbo:service interface="com.foo.BarService" connections="10" />
```

> ###### # 如果 <dubbo:service> 和<dubbo:reference> 都配了connections，<dubbo:reference>   优先。
>
> 1. 因为连接在Server上，所以配置在Provider上
> 2. 如果是长连接，比如Dubbo协议，connections表示该服务对每个提供者建立的长连接数

### > 并发控制

### 配置样例

#### 样例一

###### 限制BarService的每个方法，服务器端并发执行不能超过10个

```xml
<dubbo:service interface="com.foo.BarService" executes="10" />
```

#### 样例二

##### 限制BarService的sayHello方法，服务器端并发执行不能超过10个

```xml
<dubbo:service interface="com.foo.BarService">
	<dubbo:method name="sayHello" executes="10" />
</dubbo:service>
```

### SpringBoot上配置

#### 服务提供者的yum文件

```properties
spring:
  application:
    name: metting-cinema
  dubbo:
    server: true
    registry: zookeeper://localhost:2181
    protocol:
      name: dubbo
      #name: rmi
      port: 20886
     *accepts: 10
  datasource:
      #url: jdbc:mysql://127.0.0.1:3306/guns_rest?autoReconnect=true&useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull
      url: jdbc:mysql://127.0.0.1:3306/guns_rest?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8&useSSL=false&failOverReadOnly=false
      username: root
      password: 123456
      filters: log4j,wall,mergeStat
```

#### 在提供者的实现类的注解上

```java
@Service(interfaceClass = CinemaAPI.class,executes = 10)
```

#### 消费者的暴露的接口上

```java
@Reference(interfaceClass = CinemaAPI.class,cache = "lru",connections = 10,check = false)
    CinemaAPI cinemaAPI;
```



### 订单模块

#### 章节概要

- 完成订单莫i快业务开发
- 完成限流和熔断、降级相关内容
- Dubbo特性之分组聚合和版本控制



### ftp 读取文件信息

#### 在win10系统上安装ftp服务器

```html
https://jingyan.baidu.com/article/3a2f7c2e32f40e26afd611c0.html
```

#### ftp工具类

```java
package com.stylefeng.guns.rest.common.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @ClassName FTPUtil
 * @Description FTP工具类
 * @Author lxd
 * @Date 2019/1/2 10:31
 **/
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "ftp")
public class FTPUtil {
    // 地址 端口 用户名 密码
    private String hostName;
    private Integer port;
    private String userName;
    private String password;

    private FTPClient ftpClient = null;

    private void initFTPClient(){
        try{
            ftpClient = new FTPClient();
            ftpClient.setControlEncoding("utf-8");
            ftpClient.connect(hostName,port);
            ftpClient.login(userName,password);
        }catch (Exception e){
            log.error("初始化FTP失败",e);
        }
    }

    // 输入一个路径，然后将路径里的文件转换成字符串返回
    public String getFileContentByAddress(String path) {
        BufferedReader bufferedReader = null;
        try{
            initFTPClient();
            bufferedReader = new BufferedReader(new InputStreamReader(ftpClient.retrieveFileStream(path)));
            StringBuffer stringBuffer = new StringBuffer();
            while (true) {
                String str = bufferedReader.readLine();
                if (str == null) {
                    break;
                }
                stringBuffer.append(str);
            }
            ftpClient.logout();
            return stringBuffer.toString();
        }catch (Exception ex) {
            log.error("获取文件失败",ex);
        }finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        FTPUtil ftpUtil = new FTPUtil();
        String fileStrByAddress = ftpUtil.getFileContentByAddress("seats/cgs.json");

        System.out.println(fileStrByAddress);
    }

}

```

#### 在yum文件中配置

```properties
ftp:
  host-name: 192.168.2.246
  port: 21
  user-name: ftp
  password: 123456
```



### 科学计算法（四舍五入）

```java
private static double coutPrice(int sourts,double price) {
        BigDecimal sourtsDeci = new BigDecimal(sourts);
        BigDecimal priceDeci = new BigDecimal(price);

        BigDecimal result = sourtsDeci.multiply(priceDeci);
        double finalResult = result.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return finalResult;
    }
```

### 订单模块问题

- 订单模块的横向和纵向拆表解决
- 服务限流操作处理
- 服务熔断和降级
- 保证多版本的蓝绿上线

### 分组合并

- 生成对应实体类和mapper文件
- 同样实现接口，但需要加一个注解

```java
@Slf4j
@Component
@Service(interfaceClass = OrderAPI.class,group = "order2017")
public class OrderServiceImpl2017 implements OrderAPI {

    @Autowired
    private MoocOrder2017TMapper moocOrder2017TMapper;
}
```

```java
@Slf4j
@Component
@Service(interfaceClass = OrderAPI.class,group = "order2018")
public class OrderServiceImpl2018 implements OrderAPI {

    @Autowired
    private MoocOrder2018TMapper moocOrder2018TMapper;
}
```

- controller层，对分层数据进行聚合

```java
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference(interfaceClass = OrderAPI.class,check = false,group = "order2018")
    OrderAPI orderAPI;
    @Reference(interfaceClass = OrderAPI.class,check = false,group = "order2017")
    OrderAPI orderAPI2017;

    /**
        购票模块
     * @param fieldId
     * @param soldSeats
     * @param seatsName
     * @return
     */
    @PostMapping("buyTickets")
    public ResponseEntity buyTickets(Integer fieldId,String soldSeats,String seatsName) {

        //验证售出的票是否为真
        boolean isTrue = orderAPI.isTrueSeats(fieldId + "", soldSeats);
        if (!isTrue) {
            log.error("售出的票不为真");
            return ResponseEntity.serviceFail("售出的票不为真");
        }
        //已经销售的座位里，有没有这些座位
        boolean isNotSold = orderAPI.isNotSoldSeats(fieldId + "", soldSeats);
        if (!isNotSold) {
            log.error("售出的票已经存在");
            return ResponseEntity.serviceFail("售出的票已经存在");
        }
        //创建订单，注意获取登陆人
        if (isTrue && isNotSold) {
            //只有都为true，才创建订单
            String userId = CurrentUser.getUserInfo();
            if (userId == null && userId.trim().length() <= 0) {
                log.error("用户未登陆");
                return ResponseEntity.serviceFail("用户未登陆");
            }

            OrderVO orderVO = orderAPI.saveOrderInfo(fieldId, soldSeats, seatsName, Integer.parseInt(userId));
            if (orderVO == null) {
                log.error("购票失败");
                return ResponseEntity.serviceFail("购票业务错误");
            }else {
                return ResponseEntity.success(orderVO);
            }
        }else{
            return ResponseEntity.serviceFail("订单中的座位编号有问题");
        }
    }

    /**
     * 获取订单信息
     * @param nowPage
     * @param pageSize
     * @return
     */
    @PostMapping("getOrderInfo")
    public ResponseEntity getOrderInfo(@RequestParam(name = "nowPage",required = false,defaultValue = "1") Integer nowPage,
                                       @RequestParam(name = "pageSize",required = false,defaultValue = "5") Integer pageSize) {

        String userId = CurrentUser.getUserInfo();

        Page<OrderVO> page = new Page<>(nowPage,pageSize);
        if (userId != null && userId.trim().length() > 0) {
            Page<OrderVO> result = orderAPI.getOrderByUserId(Integer.parseInt(userId), page);
            Page<OrderVO> result2017 = orderAPI2017.getOrderByUserId(Integer.parseInt(userId), page);
            int totalPages = (int) (result.getPages()+result2017.getPages());

            //整合两个结果
            List<OrderVO> resultList = new ArrayList<>();
            resultList.addAll(result.getRecords());
            resultList.addAll(result2017.getRecords());

            return ResponseEntity.success(nowPage,totalPages,"",resultList);
        }else {
           // log.error("用户未登陆");
            return ResponseEntity.serviceFail("用户未登陆");
        }
    }
```

### 限流操作

> 使用令牌桶来实现限流操作

- 创建一个令牌桶的类

```java
package com.stylefeng.guns.core.util;

/**
 * @ClassName TokenBucket
 * @Description 令牌桶
 * @Author lxd
 * @Date 2019/1/4 13:52
 **/
public class TokenBucket {

    private int bucketNums = 100; //桶的容量
    private int rate = 1;           //流入速度
    private int nowTokens;          //当前令牌数量
    private long timestamp = getNowTime();     //当前时间

    //获取当前时间
    private long getNowTime() {
        return System.currentTimeMillis();
    }

    private int min(int tokens) {
        if (bucketNums > tokens) {
            //令牌数量还没有超过桶
            return tokens;
        }else {
            //令牌数量超过桶，则返回桶
            return bucketNums;
        }
    }

    public boolean getToken() {
        //记录拿令牌的时间
        long nowTime = getNowTime();
        //添加令牌【判断有多少个令牌】
        nowTokens = nowTokens + (int)((nowTime - timestamp)*rate);
        //添加以后的令牌数量与桶的容量哪个小
        nowTokens = min(nowTokens);
        //修改拿令牌的时间
        timestamp = nowTime;
        //判断令牌是否足够
        if (nowTokens < 1) {
            return false;
        }else {
            return true;
        }
    }
}

```

- 在业务中进行限流控制

```java
//判断人数是否过多
        if (tokenBucket.getToken()) {
            //验证售出的票是否为真
            boolean isTrue = orderAPI.isTrueSeats(fieldId + "", soldSeats);
            if (!isTrue) {
                log.error("售出的票不为真");
                return ResponseEntity.serviceFail("售出的票不为真");
            }
            //已经销售的座位里，有没有这些座位
            boolean isNotSold = orderAPI.isNotSoldSeats(fieldId + "", soldSeats);
            if (!isNotSold) {
                log.error("售出的票已经存在");
                return ResponseEntity.serviceFail("售出的票已经存在");
            }
            //创建订单，注意获取登陆人
            if (isTrue && isNotSold) {
                //只有都为true，才创建订单
                String userId = CurrentUser.getUserInfo();
                if (userId == null && userId.trim().length() <= 0) {
                    log.error("用户未登陆");
                    return ResponseEntity.serviceFail("用户未登陆");
                }

                OrderVO orderVO = orderAPI.saveOrderInfo(fieldId, soldSeats, seatsName, Integer.parseInt(userId));
                if (orderVO == null) {
                    log.error("购票失败");
                    return ResponseEntity.serviceFail("购票业务错误");
                } else {
                    return ResponseEntity.success(orderVO);
                }
            } else {
                return ResponseEntity.serviceFail("订单中的座位编号有问题");
            }
        }else {
            return ResponseEntity.serviceFail("购买人数过多，请稍后再试！！！");
        }
```

### 熔断器

- 加入依赖

```java
<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
            <version>2.0.0.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
            <version>2.0.0.RELEASE</version>
        </dependency>
```

- 在启动类上面加上注解

```java
@SpringBootApplication(scanBasePackages = {"com.stylefeng.guns"})
@EnableAsync
@EnableDubboConfiguration
@EnableHystrixDashboard
@EnableCircuitBreaker
@EnableHystrix
public class GatewayApplication {

    public static void main(String[] args) {

        SpringApplication.run(GatewayApplication.class, args);
    }
}
```

- 哪个方法需要熔断器，就在那个方法上面加上注解

```java
@HystrixCommand(fallbackMethod = "error", commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD"),
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "4000"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50")},
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "1"),
                    @HystrixProperty(name = "maxQueueSize", value = "10"),
                    @HystrixProperty(name = "keepAliveTimeMinutes", value = "1000"),
                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "8"),
                    @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1500")
            })
```

- 然后写一个熔断器对应的方法（返回类型和参数保持一样）

```java
public ResponseEntity error(Integer fieldId,String soldSeats,String seatsName){
        return ResponseEntity.serviceFail("抱歉，下单的人太多了，请稍后重试");
    }
```

> 注意：如果使用ThreadLocal线程保存用户信息，使用熔断器后，会改变线程而不会保存用户信息，这样就是取不到用户信息，报错。
>
> 解决办法：将ThreadLocal换成InheritableThreadLocal，InheritableThreadLocal可以再线程改变的时候可以保存用户信息。

### 支付操作

#### 支付流程

获取二维码 -》 等待支付宝回调 -》 修改订单状态 -》 定期对账



### ftp 上传和下载文件工具类

https://www.cnblogs.com/yingyujyf/p/6933823.html

### ftp类

```java
package com.stylefeng.guns.rest.common.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.*;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "ftp")
public class FTPUtil {

    // 地址 端口 用户名 密码
    private String hostName;
    private Integer port;
    private String userName;
    private String password;
    private String uploadPath;

    private FTPClient ftpClient = null;

    private void initFTPClient(){
        try{
            ftpClient = new FTPClient();
            ftpClient.setControlEncoding("utf-8");
            ftpClient.connect(hostName,port);
            ftpClient.login(userName,password);
        }catch (Exception e){
            log.error("初始化FTP失败",e);
        }
    }

    // 输入一个路径，然后将路径里的文件转换成字符串返回给我
    public String getFileStrByAddress(String fileAddress){
        BufferedReader bufferedReader = null;
        try{
            initFTPClient();
            bufferedReader = new BufferedReader(
                    new InputStreamReader(
                            ftpClient.retrieveFileStream(fileAddress))
            );

            StringBuffer stringBuffer = new StringBuffer();
            while(true){
                String lineStr = bufferedReader.readLine();
                if(lineStr == null){
                    break;
                }
                stringBuffer.append(lineStr);
            }

            ftpClient.logout();
            return stringBuffer.toString();
        }catch (Exception e){
            log.error("获取文件信息失败",e);
        }finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // 上传FTP文件
    public boolean uploadFile(String fileName,File file) {
        FileInputStream fileInputStream = null;
        try{
            fileInputStream = new FileInputStream(file);

            // FTP相关内容
            initFTPClient();
            // 设置FTP的关键参数
            ftpClient.setControlEncoding("utf-8");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();

            // 将ftpClient的工作空间修改
            ftpClient.changeWorkingDirectory(uploadPath);

            // 上传文件
            ftpClient.storeFile(new String(fileName.getBytes("GBK"),"iso-8859-1"),fileInputStream);

            return true;
        }catch (Exception e){
            log.error("上传失败",e);
            return false;
        }finally {
            try {
                fileInputStream.close();
                ftpClient.logout();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

```

### 本地存根

> 没有使用本地存根的时候，consumer直接调用serviceimpl接口；
>
> 当使用本地存根的时候，consumer调用xxxServiceStub,然后stub调用proxy代理（dubbo），使用代理去调用对应的接口和实现类   

### dubbo 自带的服务降级

- 降级类

```java
package com.stylefeng.guns.alipay;

import com.stylefeng.guns.alipay.vo.AliPayInfoVO;
import com.stylefeng.guns.alipay.vo.AliPayResultVO;


/*
    业务降级方法
 */
public class AliPayServiceMock implements AlipayAPI{
    @Override
    public AliPayInfoVO getQRCode(String orderId) {
        return null;
    }

    @Override
    public AliPayResultVO getOrderStatus(String orderId) {

        AliPayResultVO aliPayResultVO = new AliPayResultVO();
        aliPayResultVO.setOrderId(orderId);
        aliPayResultVO.setOrderStatus(0);
        aliPayResultVO.setOrderMsg("尚未支付成功");

        return aliPayResultVO;
    }
}

```

- 在实现类上加上mock

```java
@Service(interfaceClass = AlipayAPI.class,mock = "com.stylefeng.guns.alipay.AliPayServiceMock")
```

> 当程序发生错误的时候，mock降级类会执行；
>
> 当stub 和 mock 同时配置的时候，先执行stub，当stub发生错误的时候，mock会执行； 



### dubbo 的隐式传递

```java
 //dubbo的隐式传递
 RpcContext.getContext().setAttachment("userId",userInfo);

 String userId = RpcContext.getContext().getAttachment("userId");
```

























