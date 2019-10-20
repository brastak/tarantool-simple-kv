FROM tarantool/tarantool:2

COPY app.lua storage.lua /opt/tarantool/
WORKDIR /opt/tarantool

CMD ["tarantool", "app.lua"]