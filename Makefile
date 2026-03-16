.PHONY: docker-base-images docker-buildx-setup docker-publish docker-publish-load

docker-base-images:
	docker pull moby/buildkit:buildx-stable-1
	docker pull maven:3.9.9-eclipse-temurin-17
	docker pull eclipse-temurin:17-jre-jammy

docker-buildx-setup:
	docker buildx inspect multiarch-builder >/dev/null 2>&1 || docker buildx create --name multiarch-builder --use
	docker buildx use multiarch-builder
	docker buildx inspect --bootstrap || (docker buildx rm multiarch-builder && docker buildx create --name multiarch-builder --use && docker buildx inspect --bootstrap)

docker-publish: docker-base-images docker-buildx-setup
	docker buildx bake --file docker-bake.hcl --push configserver
	docker buildx bake --file docker-bake.hcl --push eureka
	docker buildx bake --file docker-bake.hcl --push userservice
	docker buildx bake --file docker-bake.hcl --push activityservice
	docker buildx bake --file docker-bake.hcl --push aiservice
	docker buildx bake --file docker-bake.hcl --push gateway

docker-publish-load: docker-buildx-setup
	docker buildx bake --file docker-bake.hcl --set "*.platform=linux/$$(uname -m | sed 's/x86_64/amd64/;s/aarch64/arm64/;s/arm64/arm64/')" --load
