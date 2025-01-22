"# drumre-lab1" 

## Building an image (Java 21 version expected on local machine)
In application.properties add/modifiy the line so that it has this correct uri
spring.data.mongodb.uri=mongodb://mongodb:27017/Lab1Db

./mvnw package
docker compose build

## Running an image
docker compose up -d

## Project Overview
Login Page
![login](docs/img/login.png)
Profile Page
![profile](docs/img/profile.png)
Community
![community](docs/img/community.png)
Browse Movies
![browse](docs/img/browse.png)
Movie Feed
![feed1](docs/img/feed1.png)
Movie Feed (scrolled down)
![feed2](docs/img/feed2.png)
