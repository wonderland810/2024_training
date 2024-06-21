## 基于 Play Framework + AKKA Actor 的服务器高延迟 API

- **输入**：一个整形数据
- **输出**：返回输入数据的平方根(浮点型，保留2位小数)
- **延迟**：模拟业务处理耗时100ms~250ms
  - 延迟小于100ms则视为实现错误
  - 延迟大于200ms则视为超时，算请求失败，记录异常日志

#### 功能：

1.**用docker容器或直接将服务部署在linux环境下**

- 提供容器内服务启动的脚本代码

##### 2.用play framework实现http** **api****的接入层


- 基于play action实现限流能力，限流值配置在 ./conf/application.conf 中。
- 日志分级：Error/Warn 日志与 Info 日志区分开。

##### 3.AKKA Actor 实现低高级业务逻辑：

- actor接收来自接入层数据后，模拟高延迟处理，然后返回结果(平方根)给接入层，需要同时实现**低级**和**高级**两种方式，通过两个不同API来分别访问。

- 【低级】用 sleep 模拟高延迟处理：此时需要用自定义线程池隔离sleep阻塞逻辑，线程池要求是线程数量固定且队列有界。
- 【高级】用 system.scheduler scheduleOnce 异步逻辑模拟高延迟，定时一段时间后，再返回结果。

- Actor 设置超时逻辑（5s），防止 Actor 超时太长，不能及时退出。

##### 4.基于服务性能要求：

- 堆内存最多 4GB，使用 G1 作为垃圾回收器。
- Play 框架和 Actor 框架线程池都不超过 CPU 核数。
- CPU 总消耗不能超过物理机的 50%。



### 实现效果

#### 1docker容器部署

运行项目根目录下的 [./build.sh](https://github.com/wonderland810/2024_training/blob/examination-delaySquareAPI/build.sh) 脚本文件

【高级】http://localhost:9000/highLevel?num=25

【低级】http://localhost:9000/lowLevel?num=25



#### 2实现http** **api****的接入层

##### 限流能力

将限流器设置为20，

![image-20240621132505239](.\public\images\image-20240621132505239.png)

使用JMeter访问50次/s  访问**高级API**http://localhost:9000/highLevel?num=100，

![image-20240621142429055](.\public\images\image-20240621142429055.png)(.\public\images\image-20240621142241943.png)

![image-20240621133127571](.\public\images\image-20240621133127571.png)

从结果可以看出，在使用高级API异步处理时，每个请求的响应时间都比较平均，性能较好，前面的请求均成功响应，后续请求部分成功，是由于前面请求已处理完毕，限流器又允许后面的请求进入；从失败的具体响应体“Too many requests” 可以看出是限流器起作用，直接将请求拦截。

##### 日志分级

使用高级API访问
$$
http://localhost:9000/highLevel?num=25
$$
![image-20240620222241664](.\public\images\image-20240620222241664.png)

- 延迟小于100ms则视为实现错误，延迟大于200ms则视为超时，算请求失败，记录异常日志
- 延迟在100ms-200ms之间请求成功，返回num的平方根，以info日志记录



#### 3.AKKA Actor 实现低高级业务逻辑：

##### 【低级】http://localhost:9000/lowLevel?num=25 和超时逻辑

将Actor 超时时间设置为2s，线程池数量设置为2，限流器设置为100，使用JMeter访问50次/s ，此时限流器将不在生效，而线程池数量有限，使用**低级API**必会存在一些请求会阻塞，**当请求超过2s必会触发超时**， 访问http://localhost:9000/lowLevel?num=100，

![image-20240621142803765](.\public\images\image-20240621142803765.png)

![image-20240621140105076](.\public\images\image-20240621140105076.png)

从结果可以看出，在请求响应时间小于2s均成功响应，后续请求全部失败；从失败的具体响应体“The request timed out. Please try again later.” 可以看出是Actor 超时，及时退出了。

##### 【高级】http://localhost:9000/highLevel?num=25

但是相同的条件下，使用**高级API**，异步进行处理时，每个请求由于是异步处理就不会出现阻塞，全部成功。具体运行结果如下

![image-20240621143107811](.\public\images\image-20240621143107811.png)

![image-20240621143241732](.\public\images\image-20240621143241732.png)

#### 4.基于服务性能要求：

- 堆内存最多 4GB，使用 G1 作为垃圾回收器。![image-20240621140622100](.\public\images\image-20240621140622100.png)
- Play 框架和 Actor 框架线程池都不超过 CPU 核数，设置为6。

![image-20240621140738300](.\public\images\image-20240621140738300.png)