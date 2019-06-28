# tcpip

## test1

### 已知一文件test.dat，其数据为若干图书信息，使用DataOutputStream写入，每本图书信息格式如下：

- 2字节
- Short类型，表示书名字符数
- 若干字节
- 书名
- 2字节
- Short类型，表示作者字符数
- 若干字节
- 作者名
- 2字节
- Short类型，价格
- 相关文件：`test.dat` `test1.java` `testa.java`

`test.txt`

```
四世同堂,老舍,88
网络程序设计-基于java8,刘海霞,36
数据库系统概论,王珊 萨师煊,48
java网络编程,Elliotte Rusty Harold,68
```



## test2

### 多线程计算指定范围内质数

- 相关文件：test2.java

```
Thread.0 executes isPrimeSum from 1 to 25000 Primesum is: 2762
Thread.1 executes isPrimeSum from 25001 to 50000 Primesum is: 2371
Thread.3 executes isPrimeSum from 75001 to 100000 Primesum is: 2199
Thread.2 executes isPrimeSum from 50001 to 75000 Primesum is: 2260
Thread.4 executes isPrimeSum from 100001 to 125000 Primesum is: 2142
Thread.6 executes isPrimeSum from 150001 to 175000 Primesum is: 2068
Thread.5 executes isPrimeSum from 125001 to 150000 Primesum is: 2114
Thread.7 executes isPrimeSum from 175001 to 200000 Primesum is: 2068
The sum is :17984
```

## Server

### 聊天小工具

## chatroom

### 简易聊天室
