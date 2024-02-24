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

echo "[$TARGET_SERVICE] UP!"

# target service 배포
docker-compose pull $TARGET_SERVICE
docker-compose up -d $TARGET_SERVICE

# Nginx 설정 파일 내에서 서비스 이름 변경
sed -i "s/server $OTHER_SERVICE:8080;/server $TARGET_SERVICE:8080;/" $NGINX_CONF

# Nginx 설정 적용을 위해 Nginx 프로세스 재로드
docker exec $NGINX_CONTAINER nginx -s reload

echo "$TARGET_SERVICE deployment completed."