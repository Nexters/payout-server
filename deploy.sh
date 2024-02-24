#!/bin/bash

NGINX_CONF="/etc/nginx/nginx.conf"
NGINX_CONTAINER="nginx"
GREEN_API_CONTAINER="green-api"
BLUE_API_CONTAINER="blue-api"
BATCH_CONTAINER="batch"

# nginx 정상 동작 확인
IS_NGINX_RUNNING=$(docker ps | grep running)

if [ -z "$IS_NGINX_RUNNING" ]; then
  # 정상 작동하지 않을 시 nginx 재시작
  echo "nginx container is not running. run nginx container"
  docker rmi nginx
  docker-compose -f /home/docker-compose.yml up -d nginx
else
  echo "nginx is already running"
fi

# api-server 정상 동작 확인
IS_BLUE_RUNNING=$(docker ps | grep ${BLUE_API_CONTAINER})

if [ -z "$IS_BLUE_RUNNING" ]; then
    TARGET_SERVICE=${BLUE_API_CONTAINER}
    OTHER_SERVICE=${GREEN_API_CONTAINER}
else
    TARGET_SERVICE=${GREEN_API_CONTAINER}
    OTHER_SERVICE=${BLUE_API_CONTAINER}
fi

echo "Switching to $TARGET_SERVICE..."

docker-compose -f /home/docker-compose.yml up -d $TARGET_SERVICE $BATCH_CONTAINER

# Nginx 설정 업데이트하여 트래픽 전환
docker exec $NGINX_CONTAINER sed -i "s/$OTHER_SERVICE/$TARGET_SERVICE/" $NGINX_CONF

# Nginx 설정 적용을 위해 Nginx 프로세스 재로드
docker exec $NGINX_CONTAINER nginx -s reload

echo "$TARGET_SERVICE deployment completed."