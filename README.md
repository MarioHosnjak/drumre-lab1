"# drumre-lab1" 

## Building an image (Java 21 version expected on local machine)
In application.properties add/modifiy the line so that it has this correct uri
spring.data.mongodb.uri=mongodb://mongodb:27017/Lab1Db

./mvnw package
docker compose build

## Running an image
docker compose up -d
