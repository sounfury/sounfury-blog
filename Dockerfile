# 使用Java运行环境作为基础镜像
FROM openjdk:17-jdk-slim

# 维护者信息
LABEL maintainer="a2133266@outlook.com"

# 设置工作目录
WORKDIR /app

# 复制jar包到容器中
COPY admin/target/blog-admin.jar /app/blog-admin.jar

# 设置环境变量
ENV SPRING_PROFILES_ACTIVE=prod

# 暴露端口（根据您的应用实际端口进行修改）
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java","-jar","blog-admin.jar"]