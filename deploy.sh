RUNNING_CONTAINER=$(docker ps | grep blue)
NGINX_CONF="/home/nginx.conf"

if [ -z "$RUNNING_CONTAINER" ]; then
    TARGET_SERVICE="blue-api"
    OTHER_SERVICE="green-api"
else
    TARGET_SERVICE="green-api"
    OTHER_SERVICE="blue-api"
fi

echo "$TARGET_SERVICE Deploy..."
docker-compose -f /home/docker-compose.yml pull $TARGET_SERVICE $BATCH_CONTAINER
docker-compose -f /home/docker-compose.yml up -d $TARGET_SERVICE $BATCH_CONTAINER

# Wait for the target service to be healthy before proceeding
sleep 10
#while true; do
#    echo "$TARGET_SERVICE health check...."
#    HEALTH=$(docker-compose -f /home/docker-compose.yml exec nginx curl http://$TARGET_SERVICE:8080)
#    if [ -n "$HEALTH" ]; then
#        break
#    fi
#    sleep 3
#done

# Update the nginx config and reload
sed -it "s/$OTHER_SERVICE/$TARGET_SERVICE/" $NGINX_CONF
docker exec $NGINX_CONTAINER nginx -s reload
#docker-compose -f /home/docker-compose.yml exec nginx service nginx reload

# Stop the other service
docker-compose -f /home/docker-compose.yml stop $OTHER_SERVICE


##!/bin/bash
#
#NGINX_CONF="/home/nginx.conf"
#NGINX_CONTAINER="nginx"
#GREEN_API_CONTAINER="green-api"
#BLUE_API_CONTAINER="blue-api"
#BATCH_CONTAINER="batch"
#
## nginx 정상 동작 확인
#IS_NGINX_RUNNING=$(docker ps | grep ${NGINX_CONTAINER})
#
## api-server 정상 동작 확인
#IS_BLUE_RUNNING=$(cat $NGINX_CONF | grep $BLUE_API_CONTAINER)
#
#if [ -z "$IS_NGINX_RUNNING" ]; then
#  # 정상 작동하지 않을 시 nginx 재시작
#  echo "nginx container is not running. run nginx container"
#  docker rmi nginx
#  docker-compose -f /home/docker-compose.yml up -d nginx
#else
#  echo "nginx is already running"
#fi
#
#if [ -z "$IS_BLUE_RUNNING" ]; then
#    TARGET_SERVICE=${BLUE_API_CONTAINER}
#    OTHER_SERVICE=${GREEN_API_CONTAINER}
#else
#    TARGET_SERVICE=${GREEN_API_CONTAINER}
#    OTHER_SERVICE=${BLUE_API_CONTAINER}
#fi
#  sleep 3
#
#echo "Switching to $TARGET_SERVICE..."
#
#docker-compose -f /home/docker-compose.yml up -d $TARGET_SERVICE $BATCH_CONTAINER
#
## Nginx 설정 업데이트하여 트래픽 전환
#sed -it "s/$OTHER_SERVICE/$TARGET_SERVICE/" $NGINX_CONF
#docker exec $NGINX_CONTAINER nginx -s reload
#
##docker stop $OTHER_SERVICE
#
## Nginx 설정 적용을 위해 Nginx 프로세스 재로드
#
#echo "$TARGET_SERVICE deployment completed."