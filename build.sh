#!/bin/bash
set -e

# 运行 sbt 打包命令
sbt dist

# 删除之前构建的同名容器（如果存在）
if [ "$(docker ps -aq -f name=delaysquareapi)" ]; then
    docker rm -f delaysquareapi
fi

# 删除之前构建的同名镜像（如果存在）
if [ "$(docker images -q delaysquareapi)" ]; then
    docker rmi delaysquareapi
fi

# 登录 Docker
docker login

# 构建 Docke
docker build -t delaysquareapi .

# 创建并启动 Docker 容器
docker docker run -d --name delaysquareapi -p 9000:9000 delaysquareapi