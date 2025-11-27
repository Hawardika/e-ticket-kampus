# Build stage
FROM gradle:8.9-jdk17 AS build
WORKDIR /workspace
COPY . .
RUN gradle clean installDist -x test

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app
# sesuai settings.gradle.kts -> rootProject.name = "e-ticket-kampus"
COPY --from=build /workspace/build/install/e-ticket-kampus /app
ENV PORT=8080
EXPOSE 8080
CMD ["./bin/e-ticket-kampus"]
