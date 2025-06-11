# 基础镜像 指定具体 Alpine 版本
#由于验证码是画出来的，openjdk里没有相关类 ，可以用 oracle jdk 镜像
#OpenJDK基础代码都是来自Oracle的JDK，但是为了开原协议的要求，把Oracle JDK中用到的一些非开源的组件、
#代码去除了，替换成了开源的组件，主要的是在加密和图形的部分。可能会有一些不兼容
#Docker镜像运行Spring Boot项目一般采用的是openjdk，这个jdk环境缺省是没有字体的，在运行很多项目时，会因缺省字体报错
FROM openjdk:17-jdk-alpine

# 维护者信息
MAINTAINER aioveu <ambitiouschild@qq.com>

RUN echo "https://mirrors.ustc.edu.cn/alpine/v3.7/main/" > /etc/apk/repositories && \
    apk --no-cache add tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone   \

RUN apk add --update ttf-dejavu fontconfig
# 安装字体库 (必须)
RUN apk add --no-cache fontconfig ttf-dejavu


#这个错误发生在生成验证码时，由于缺少字体管理类 sun.font.SunFontManager。 \
#这通常是因为在容器环境中缺少必要的字体库。以下是如何彻底解决这个问题的完整方案：
#关键在于确保容器内安装了必要的字体包，并在代码中正确引用它们。

# /tmp 目录作为容器数据卷目录，SpringBoot内嵌Tomcat容器默认使用/tmp作为工作目录，任何向 /tmp 中写入的信息不会记录进容器存储层
# 在宿主机的/var/lib/docker目录下创建一个临时文件并把它链接到容器中的/tmp目录
VOLUME /tmp

# 复制主机文件至镜像内，复制的目录需放置在 Dockerfile 文件同级目录下
ADD target/aioveu-boot.jar app.jar

# 容器启动执行命令
CMD java \
    -Xms128m \
    -Xmx128m \
    -Djava.security.egd=file:/dev/./urandom \
    -jar /app.jar

# 声明容器提供服务端口
EXPOSE 8989



