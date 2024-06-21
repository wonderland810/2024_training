#!/bin/bash

# 运行 sbt 打包命令
sbt stage

# 删除之前构建的同名容器（如果存在）
if [ "$(docker ps -aq -f name=delaysquareapi)" ]; then
    docker rm -f delaysquareapi
fi

# 删除之前构建的同名镜像（如果存在）
if [ "$(docker images -q delaysquareapi)" ]; then
    docker rmi delaysquareapi
fi


# 构建 镜像
docker build -t delaysquareapi .

# 创建并启动 Docker 容器
docker run -d --name delaysquareapi -p 9000:9000 delaysquareapi