group "default" {
  targets = [
    "activityservice",
    "aiservice",
    "configserver",
    "eureka",
    "gateway",
    "userservice",
  ]
}

target "docker-metadata" {
  platforms = ["linux/amd64", "linux/arm64"]
}

target "activityservice" {
  inherits = ["docker-metadata"]
  context = "./activityservice"
  dockerfile = "Dockerfile"
  tags = ["olawale99/activityservice:latest"]
}

target "aiservice" {
  inherits = ["docker-metadata"]
  context = "./aiservice"
  dockerfile = "Dockerfile"
  tags = ["olawale99/aiservice:latest"]
}

target "configserver" {
  inherits = ["docker-metadata"]
  context = "./configserver"
  dockerfile = "Dockerfile"
  tags = ["olawale99/configserver:latest"]
}

target "eureka" {
  inherits = ["docker-metadata"]
  context = "./eureka"
  dockerfile = "Dockerfile"
  tags = ["olawale99/eureka:latest"]
}

target "gateway" {
  inherits = ["docker-metadata"]
  context = "./gateway"
  dockerfile = "Dockerfile"
  tags = ["olawale99/gateway:latest"]
}

target "userservice" {
  inherits = ["docker-metadata"]
  context = "./userservice"
  dockerfile = "Dockerfile"
  tags = ["olawale99/userservice:latest"]
}
