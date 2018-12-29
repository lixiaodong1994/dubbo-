> zookeeper启动报错解决办法：删除你自己设定的dataDir路径下的version-2文件夹，然后重启即可。

    D:\tmp\zookeeper



> 服务提供者暴露的接口需要添加的注解

    *@Component
    *@Service(interfaceClass = FilmAPI.class)
    public class DefaultFilmServiceImpl implements FilmAPI {
    
        @Autowired
        private MoocBannerTMapper moocBannerTMapper;
        @Autowired
        private MoocFilmTMapper moocFilmTMapper;
        
    }

> 服务提供者配置文件

- server 的端口不能重复
- dubbo 的端口不能重复

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

> 消费服务的controller层添加的注解

    @RestController
    @RequestMapping("film")
    public class FilmController {
    
        private static final String IMG = "http://img.maoyan.com";
    
        *@Reference(interfaceClass = FilmAPI.class,check = false)
        FilmAPI filmAPI;
    }

> 一对多的查询语句

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

> 热点数据的处理-redis/结果缓存

- Dubbo可以通过注解对热点数据进行缓存
- 了解Dubbo结果缓存于Redis等的区别

> 缓存类型

- lru 基于最近最少使用原则删除多余缓存，保持最热的数据被缓存
- threadlocal 当前线程缓存，比如一个页面渲染，用到很多portal，每个portal都要去查用户信息，通过线程缓存，可以减少多余访问。
- jcache yuJSR107集成，可以桥接各种缓存实现。

> 配置

    <dubbo:reference interface="com.foo.BasrService" cache="lru" />

或：

    <dubbo:reference interface="com.foo.BasrService">
    	<dubbo:method name="findBar" cache="lru" />
    </dubbo:reference>

在controller层配置cache

    @Reference(interfaceClass = CinemaAPI.class,cache = "lru",check = false)
        CinemaAPI cinemaAPI;



> 并发、连接控制

- Dubbo可以对连接和兵法数量进行控制
- 超出部门以错误形式返回

服务端连接控制

限制服务器端接受的连接不能超过10个

    <dubbo:provider protocol="dubbo" accepts="10" />

或

    <dubbo:protocol name="dubbo" accepts="10" />

客户端连接控制

限制客户端服务使用连接不能超过10

    <dubbo:reference interface="com.foo.BarService" connections="10" />

或

    <dubbo:service interface="com.foo.BarService" connections="10" />

# 如果 dubbo:service 和dubbo:reference 都配了connections，dubbo:reference   优先。

1. 因为连接在Server上，所以配置在Provider上
2. 如果是长连接，比如Dubbo协议，connections表示该服务对每个提供者建立的长连接数

> 并发控制

配置样例

样例一

限制BarService的每个方法，服务器端并发执行不能超过10个

    <dubbo:service interface="com.foo.BarService" executes="10" />

样例二

限制BarService的sayHello方法，服务器端并发执行不能超过10个

    <dubbo:service interface="com.foo.BarService">
    	<dubbo:method name="sayHello" executes="10" />
    </dubbo:service>

SpringBoot上配置

服务提供者的yum文件

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

在提供者的实现类的注解上

    @Service(interfaceClass = CinemaAPI.class,executes = 10)

消费者的暴露的接口上

    @Reference(interfaceClass = CinemaAPI.class,cache = "lru",connections = 10,check = false)
        CinemaAPI cinemaAPI;







