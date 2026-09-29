# Trabio — Dockerfile (JAR pré-compilado localmente)
FROM eclipse-temurin:24-jre
WORKDIR /app

# Diretório de uploads persistido via volume
RUN mkdir -p uploads

# Copia o JAR gerado pelo Maven local
COPY target/*.jar app.jar

# Expõe a porta
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8080/ || exit 1

# Inicia a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
