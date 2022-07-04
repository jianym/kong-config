# 徽章
[![license Status](https://img.shields.io/badge/License-Apache%202.0-blue.svg?branch=v2.0)](http://www.apache.org/licenses/LICENSE-2.0.txt)

# 2.0新增
1.支持etcd接入

# 使用步骤
1.使用maven依赖项目
``` 
<!-- https://mvnrepository.com/artifact/org.jeecf/kong-config -->
<dependency>
    <groupId>org.jeecf</groupId>
    <artifactId>kong-config</artifactId>
    <version>2.0-RELEASE</version>
</dependency>

```
2.配置启动参数
``` 
kong:
   config:
      zookeeper:
         address: localhost:2181
         namespace: jym
```
etcd配置
``` 
kong:
   config:
      etcd:
         address: localhost:2379
         namespace: jym
```
3.启动配置
``` 
@SpringBootApplication
@EnableKongConfig
public class ApplicationC {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationC.class, args);
    }

}
```
4.设置动态参数
``` 
@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "test")
public class ConfigProperties {
    
    @Value("${name:hello}")
    private String name;

}
```
5.访问配置中心  http://项目地址/kong/config, 添加对应的动态参数。注意：配置中心与参数分级对应关系，例如 test.name 配置中心则为 /test/name 。配置中心为目录索引格式

![Alt text](https://github.com/jianym/meditate-static/blob/master/img/kong-config-view1.png)


6.启动项目，当添加或更新配置中心参数时，会同步到项目中并动态刷新 refreshScope 下的参数

# 扩展
 你可以通过继承AbstractConfigRefresh类编写自己的刷新规则，使用@EnableKongConfig中configRefresh参数指向你的实现类

