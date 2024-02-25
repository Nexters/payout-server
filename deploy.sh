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
docker-compose -f /home/docker-compose.yml up -d $TARGET_SERVICE $BATCH_CONTAINER

# Wait for the target service to be healthy before proceeding
sleep 10

# Update the nginx config and reload
sed -it "s/$OTHER_SERVICE/$TARGET_SERVICE/" $NGINX_CONF
docker-compose -f /home/docker-compose.yml restart nginx

# Stop the other service
docker-compose -f /home/docker-compose.yml stop $OTHER_SERVICE
