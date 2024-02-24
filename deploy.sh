#!/bin/bash

# Docker 설치
sudo apt-get -y update
sudo apt-get install -y apt-transport-https ca-certificates curl gnupg-agent software-properties-common
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
sudo apt-get -y update
sudo apt-get -y install docker-ce docker-ce-cli containerd.io

# 현재 사용자를 docker 그룹에 추가 (sudo 없이 docker 명령어 사용 가능)
sudo usermod -aG docker $USER

# Docker Compose 설치
sudo curl -L "https://github.com/docker/compose/releases/download/1.28.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose

# 설치가 제대로 되었는지 확인
docker --version
docker-compose --version

# 시스템 부팅 시에 자동 시작을 위해 Docker 서비스 활성화
sudo systemctl enable docker

echo "docker, docker-compose install complete"
