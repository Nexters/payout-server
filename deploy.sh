#!/bin/bash

NGINX_CONF="/etc/nginx/nginx.conf"
NGINX_CONTAINER="nginx"
GREEN_API_CONTAINER="green-api"
BLUE_API_CONTAINER="blue-api"

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

# Nginx 설정 업데이트하여 트래픽 전환
docker exec $NGINX_CONTAINER sed -i "s/$OTHER_SERVICE/$TARGET_SERVICE/" $NGINX_CONF

# Nginx 설정 적용을 위해 Nginx 프로세스 재로드
docker exec $NGINX_CONTAINER nginx -s reload

echo "$TARGET_SERVICE deployment completed."