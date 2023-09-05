GREEN := \033[0;32m
NC := \033[0m

build :
	@cd ./frontend && npm run build
	@cp -rf ./frontend/dist/* ./docker/nginx/html/
	@./gradlew build -x test
	@echo "${GREEN}Build Success!${NC}"

.PHONY: build

up : build
	@docker compose up --build
.PHONY: up

deploy : build
	@scp -P 3335 ./build/libs/*.jar kangmin@100.0.0.189:/home/kangmin/OMG/build/libs
	@scp -P 3335 .env Dockerfile docker-compose.yml kangmin@100.0.0.189:/home/kangmin/OMG/
	@scp -P 3335 -r ./docker/nginx/ kangmin@100.0.0.189:/home/kangmin/OMG/docker/
	@scp -P 3335 ./docker/prometheus/config/prometheus.yml kangmin@100.0.0.189:/home/kangmin/OMG/docker/prometheus/config/

	@echo "${GREEN}Deploy Success!${NC}"
	@echo "Do you want to login to fortivpn? (yes/no): "
	@read answer; \
	if [ "$$answer" = "yes" ]; then \
	    ssh -p 3335 kangmin@100.0.0.189; \
	fi
.PHONY: deploy

down :
	@docker compose down
.PHONY: down

clean :
	@./gradlew clean
	@rm -rf ./docker/nginx/html/*
	@rm -rf ./frontend/dist/*
	@docker system prune -f
	@echo "${GREEN}Clean Success!${NC}"

.PHONY: clean

fclean : clean
	@docker volume prune -f
	@echo "${GREEN}Fclean Success!${NC}"

.PHONY: fclean