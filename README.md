# Tarantool Simple-KV
## Overview
Tarantool Simple-KV is a REST web service based on Tarantool which provides HTTP interface to key-value store. Service uses Tarantool as a persistense store and nginx with Tarantool nginx upstream module as a HTTP gateway.

## Easy start
```bash
$ docker-compose build
$ docker-compose up -d
```
After that service will accept requests via HTTP protocol on port 80. If you need to start service bound on other port or if you want to mount tarantool volume (tarantool-data) to particular path, you can use *docker-compose run* command.

## Components
Application consists of two docker-based services. The first service (app directory) contains application logic and defines interaction protocol. The second one (http directory) contains nginx configuration that allows to expose application methods to HTTP.

## Operarations
- **Create record**: `POST http://<hostname>/kv`

   Payload message must be JSON document which contains "key" and "value" properties. "key" will be used as primary key in persistence store and "value" will be considered as data to store.

- **Retreive record**: `GET http://<hostname>/kv/<key>`
- **Update record**: `PUT http://<hostname>/kv/<key>`
- **Delete record**: `DELETE http://<hostname>/kv/<key>`

## Configuration
You will need to edit sources to make changes in service configuration. And after that, you will need to rebuild and restart the service.
### Maximum message size
By default, it is limited by 8 KB. If you need to increase this, edit *http/nginx.conf* file and *change client_max_body_size* parameter (also, you may need to change *client_header_buffer_size* and set up *tnt_pass_http_request_buffer_size* params).
### Request rate
By default, request rate is limited by 200 requests per second. If you need to change this, edit *http/nginx.conf* file and change *limit_req_zone* parameter definition.