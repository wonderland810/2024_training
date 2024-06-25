# 使用官方的OpenJDK作为基础镜像
FROM openjdk:8-jre-slim

# 设置工作目录
WORKDIR /app/delaysquareapi-1.0/

# 复制解压后的文件到容器中
COPY stage/  /app/delaysquareapi-1.0/

# 更改权限
RUN chmod +x /app/delaysquareapi-1.0/bin/delaysquareapi

# 暴露应用运行的端口
EXPOSE 9000

# 启动应用
ENTRYPOINT ["/app/delaysquareapi-1.0/bin/delaysquareapi", "-J-Xmx4G", "-J-XX:+UseG1GC"]
