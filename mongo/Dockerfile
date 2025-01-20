FROM mongo:6.0

COPY ./Lab1Db.movies.json /movies.json
COPY ./Lab1Db.users.json /users.json


CMD mongoimport --uri mongodb://mongodb:27017/Lab1Db --collection movies --drop --file ./movies.json --jsonArray && mongoimport --uri mongodb://mongodb:27017/Lab1Db --collection users --drop --file ./users.json --jsonArray