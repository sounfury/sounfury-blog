# 使用 Java 运行环境作为基础镜像
FROM openjdk:17-jdk-slim

# 维护者信息
LABEL maintainer="a2133266@outlook.com"

# 安装字体栈依赖：freetype + fontconfig + 常用字体（DejaVu）
RUN set -eux; \
    apt-get update; \
    apt-get install -y --no-install-recommends \
        libfreetype6 \
        fontconfig \
        fonts-dejavu-core; \
    rm -rf /var/lib/apt/lists/*

# 可选：显式开启无头模式，并设置字体目录（更稳）
ENV JAVA_TOOL_OPTIONS="-Djava.awt.headless=true -Dsun.java2d.fontdir=/usr/share/fonts"

# 设置工作目录
WORKDIR /app

# 复制jar包到容器中
COPY admin/target/blog-admin.jar /app/blog-admin.jar

# 设置环境变量
ENV SPRING_PROFILES_ACTIVE=prod

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "blog-admin.jar"]
