# --- Estágio 1: Build (Compilação) ---
# Usamos a imagem do Maven com a JDK 21 (baseado no seu pom.xml)
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copia o 'pom.xml' primeiro para aproveitar o cache
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o restante do código-fonte
COPY src ./src

# Compila o projeto e gera o .jar
RUN mvn clean package -DskipTests

# --- Estágio 2: Final (Produção) ---
# Usamos uma imagem "limpa" apenas com o Java 21 (JRE)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copia o .jar gerado no Estágio 1
# O nome será algo como 'comissoes-api-0.0.1-SNAPSHOT.jar'
# O '*.jar' pega ele automaticamente
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]