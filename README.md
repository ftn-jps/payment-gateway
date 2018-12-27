# Payment gateway

## Compiling from source

```
./mvnw package
```

## Usage

```
java -jar target/*.jar
```

Server listens on port 8081 by default. Use command line option --server.port= to change it.

Default URL where frontend is running (used for redirection): `https://localhost:4201`  
Use command line option `-Dfrontend.url=` to change it.
