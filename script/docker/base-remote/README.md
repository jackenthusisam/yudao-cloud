# yudao-cloud base remote deployment

This package runs the base services used for local module development:

- Nacos: `8.141.17.93:8848`
- Gateway: `8.141.17.93:48080`
- System service: `8.141.17.93:48081`
- Infra service: `8.141.17.93:48082`
- MySQL and Redis are bound to `127.0.0.1` on the server by default: MySQL `13306`, Redis `26379`.
- The compose file reuses server-side base images: `mysql:8.0.33`, `redis:7.2`, `nacos/nacos-server:v2.4.2`.

## Build images

From the repository root:

```bash
mvn clean package -DskipTests -pl yudao-gateway,yudao-module-system/yudao-module-system-server,yudao-module-infra/yudao-module-infra-server -am

docker buildx build --platform linux/amd64 -t yudao-gateway:base --load yudao-gateway
docker buildx build --platform linux/amd64 -t yudao-module-system-server:base --load yudao-module-system/yudao-module-system-server
docker buildx build --platform linux/amd64 -t yudao-module-infra-server:base --load yudao-module-infra/yudao-module-infra-server
```

The service images use Eclipse Temurin 17 JRE as the runtime.

## Ports

| Name | Default | Note |
| --- | --- | --- |
| `MYSQL_HOST_PORT` | `13306` | Host port for this project's MySQL container |
| `REDIS_HOST_PORT` | `26379` | Host port for this project's Redis container |

The compose file keeps memory lower than the upstream defaults so it can coexist with other services on a small x86 server.

## Copy to the server

```bash
docker save yudao-gateway:base yudao-module-system-server:base yudao-module-infra-server:base | gzip > yudao-base-images.tar.gz

ssh root@8.141.17.93 'mkdir -p /opt/yudao-cloud/script/docker/base-remote /opt/yudao-cloud/sql/mysql'
scp yudao-base-images.tar.gz root@8.141.17.93:/opt/yudao-cloud/
scp script/docker/base-remote/docker-compose.yml script/docker/base-remote/.env.example script/docker/base-remote/init-nacos.sh root@8.141.17.93:/opt/yudao-cloud/script/docker/base-remote/
scp sql/mysql/ruoyi-vue-pro.sql root@8.141.17.93:/opt/yudao-cloud/sql/mysql/
```

## Start on the server

```bash
ssh root@8.141.17.93
cd /opt/yudao-cloud/script/docker/base-remote
cp .env.example .env
docker load -i /opt/yudao-cloud/yudao-base-images.tar.gz
docker compose up -d
chmod +x init-nacos.sh && ./init-nacos.sh
docker compose restart gateway system infra
docker compose ps
```

## Local module settings

Run your new local module with these environment variables so it uses the remote base services:

```bash
SPRING_PROFILES_ACTIVE=dev
SPRING_CLOUD_NACOS_SERVER_ADDR=8.141.17.93:8848
SPRING_CLOUD_NACOS_CONFIG_SERVER_ADDR=8.141.17.93:8848
SPRING_CLOUD_NACOS_USERNAME=nacos
SPRING_CLOUD_NACOS_PASSWORD=nacos
SPRING_CLOUD_NACOS_DISCOVERY_NAMESPACE=dev
SPRING_CLOUD_NACOS_CONFIG_NAMESPACE=dev
```

If the remote gateway must route traffic to your local module, the server must be able to reach your local machine. Use a VPN, Tailscale, frp, or an SSH reverse tunnel; otherwise only local-to-remote calls will work.
