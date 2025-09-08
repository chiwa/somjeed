# ---- build stage ----
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
# เพิ่มสิทธิ์ mvnw กัน permission denied
RUN chmod +x mvnw
RUN ./mvnw -B -DskipTests clean package

# ---- run stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]