build:
	mvn clean install
	docker build -t sqshq/piggy-app .

run:
	docker run --name piggy-db -p 3306:3306 -d -e MYSQL_ROOT_PASSWORD=dev-password -v /data/mysql:/var/lib/mysql mysql:latest
	sleep 10
	-docker exec -i piggy-db mysql -uroot -pdev-password < init.sql
	docker run --name piggy-app -p 8080:8080 -d --link piggy-db:piggy-db sqshq/piggy-app

clean:
	-docker stop piggy-db
	-docker rm piggy-db
	-docker stop piggy-app
	-docker rm piggy-app

.PHONY: build run reload