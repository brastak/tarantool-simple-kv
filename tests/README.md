# Testing

## Run with maven
### Prerequisites
1. JDK 11 or later
2. Maven 3
### Run
To run tests with maven you can use command:

`mvn test -Dkv.uri.base=<service endpoint URI>`

where service endpoint URI specifies Simple KV service location, i.e. http://localhost/kv

## Run with Docker
1. Build image: `docker build . -t tarantool-simple-kv-tests`
2. Run container:

   `docker run --rm --cpus=4 tarantool-simple-kv-tests mvn test -Dkv.uri.base=<service endpoint URI>`

   where service endpoint URI specifies Simple KV service location, i.e. http://localhost/kv
