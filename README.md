# springboot-mybatis-secondary

## 소개
 > mybatis 2개이상 여러가지 방법

## 스팩
- java 1.8.x
- gradle 6.x.x
- spring-boot 2.2.x
- use lombok lib
- use `logback` logger

### 빌드
```
$ ./gradlew build -x test
$ ./gradlew bootRun -x test --args='test'

``` 

### 실행
``` 
# linux
$ sh startup.sh start
$ sh startup.sh [start,stop,restart,status,log

# windows
> startup-start.bat 또는 startup.bat start
> startup-log.bat <-- 로그조회

```