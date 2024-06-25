## 基于 Play Framework + AKKA Actor 的服务器高延迟 API

- **输入**：一个整形数据
- **输出**：返回输入数据的平方根(浮点型，保留2位小数)
- **延迟**：模拟业务处理耗时100ms~250ms
  - 延迟小于100ms则视为实现错误
  - 延迟大于200ms则视为超时，算请求失败，记录异常日志

#### 功能：

1.**用docker容器或直接将服务部署在linux环境下**

- 提供容器内服务启动的脚本代码

2.**用play framework实现http** **api****的接入层**


- 基于play action实现限流能力，限流值配置在 ./conf/application.conf 中。
- 日志分级：Error/Warn 日志与 Info 日志区分开。

3.**AKKA Actor 实现低高级业务逻辑：**

- actor接收来自接入层数据后，模拟高延迟处理，然后返回结果(平方根)给接入层，需要同时实现**低级**和**高级**两种方式，通过两个不同API来分别访问。

- 【低级】用 sleep 模拟高延迟处理：此时需要用自定义线程池隔离sleep阻塞逻辑，线程池要求是线程数量固定且队列有界。
- 【高级】用 system.scheduler scheduleOnce 异步逻辑模拟高延迟，定时一段时间后，再返回结果。

- Actor 设置超时逻辑（5s），防止 Actor 超时太长，不能及时退出。

4.**基于服务性能要求：**

- 堆内存最多 4GB，使用 G1 作为垃圾回收器。
- Play 框架和 Actor 框架线程池都不超过 CPU 核数。
- CPU 总消耗不能超过物理机的 50%。


###
### 实现效果

#### 1docker容器部署

运行项目根目录下的 [./build.sh](https://github.com/wonderland810/2024_training/blob/examination-delaySquareAPI/build.sh) 脚本文件

【高级】http://localhost:9000/highLevel?num=25

【低级】http://localhost:9000/lowLevel?num=25

####
#### 2实现http** **api****的接入层

##### 限流能力

将限流器设置为20，
![image-20240621132505239](https://github.com/wonderland810/2024_training/assets/75829062/d148583f-2461-49d7-8a90-bcc340ecfa0c)

使用JMeter访问50次/s  访问**高级API**http://localhost:9000/highLevel?num=100，

![image-20240621142241943](https://github.com/wonderland810/2024_training/assets/75829062/6a7d71dc-8b88-4c1c-aa3c-0edb6dce53c4)


![image-20240621133127571](https://github.com/wonderland810/2024_training/assets/75829062/30e33eed-b130-4f69-b532-c4f27533b1b8)


从结果可以看出，在使用高级API异步处理时，每个请求的响应时间都比较平均，性能较好，前面的请求均成功响应，后续请求部分成功，是由于前面请求已处理完毕，限流器又允许后面的请求进入；从失败的具体响应体“Too many requests” 可以看出是限流器起作用，直接将请求拦截。

##### 日志分级

使用高级API访问

http://localhost:9000/highLevel?num=100

![image](https://github.com/wonderland810/2024_training/assets/75829062/f83002c3-5979-4490-a604-c68ac517994b)


- 延迟小于100ms则视为实现错误，延迟大于200ms则视为超时，算请求失败，记录异常日志
- 延迟在100ms-200ms之间请求成功，返回num的平方根，以info日志记录

####
#### 3.AKKA Actor 实现低高级业务逻辑：

##### 【低级】http://localhost:9000/lowLevel?num=25 和超时逻辑

将Actor 超时时间设置为2s，线程池数量设置为2，限流器设置为100，使用JMeter访问50次/s ，此时限流器将不在生效，而线程池数量有限，使用**低级API**必会存在一些请求会阻塞，**当请求超过2s必会触发超时**， 访问http://localhost:9000/lowLevel?num=100，

![image-20240621142803765](https://github.com/wonderland810/2024_training/assets/75829062/21110311-d96e-4cad-b66b-b7b8f0299bae)

![image-20240621140105076](https://github.com/wonderland810/2024_training/assets/75829062/80e479f5-7abc-4d9c-be95-f332374302e6)

从结果可以看出，在请求响应时间小于2s均成功响应，后续请求全部失败；从失败的具体响应体“The request timed out. Please try again later.” 可以看出是Actor 超时，及时退出了。

##### 【高级】http://localhost:9000/highLevel?num=25

但是相同的条件下，使用**高级API**，异步进行处理时，每个请求由于是异步处理就不会出现阻塞，全部成功。具体运行结果如下

![image-20240621143107811](https://github.com/wonderland810/2024_training/assets/75829062/462712e5-4f1c-4d70-81bb-29b2552998e3)

![image-20240621143241732](https://github.com/wonderland810/2024_training/assets/75829062/356ba477-f362-44e9-8f72-5f34dbd81a2b)

####
#### 4.基于服务性能要求：

- 堆内存最多 4GB，使用 G1 作为垃圾回收器。

- Play 框架和 Actor 框架线程池都不超过 CPU 核数
  通过lscpu指令查看详细的 CPU 信息，包括核心数、线程数、架构等，将线程池最大线程数设置为4,线程数量固定且队列有界

  ```bash
  lscpu
  ```

  ![image](https://github.com/wonderland810/2024_training/assets/75829062/c9862ab8-fd54-4694-a390-54d9653a09b4)
  ![image](https://github.com/wonderland810/2024_training/assets/75829062/250e9308-0a66-4765-8a86-a402e97edba2)

- CPU总消耗不能超过物理机的50%
  top 是一个实时显示系统总体性能状态的命令，会显示每个进程的 CPU 使用情况。可以使用 top 命令来查看系统总体 CPU 使用情况。
  ```bash
    top
  ```

  ![image](https://github.com/wonderland810/2024_training/assets/75829062/d1e2264d-5a85-4f53-89e6-58e222e46c9a)

`us`：用户空间的 CPU 使用率

`sy`：内核空间的 CPU 使用率

`ni`：用户进程空间内改变过优先级的进程的 CPU 使用率

`id`：空闲的 CPU 比率

`wa`：等待 I/O 的 CPU 比率

`hi`：硬中断的 CPU 使用率

`si`：软中断的 CPU 使用率

`st`：虚拟机偷取的 CPU 时间
