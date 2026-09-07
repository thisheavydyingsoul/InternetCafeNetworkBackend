set -euo pipefail
DEPLOY_DIR="${STAGING_DEPLOY_PATH:-/opt/internet-cafe-staging}"
COMPOSE_FILE="docker/docker-compose.staging.yml"
ENV_FILE="docker/.env"

cd "$DEPLOY_DIR"

echo "Logging in to GHCR..."
ecgo "$GHCR_TOKEN" | docker login ghcr.io -u "$GHCR_USER" --password-stdin

export BACKEND_IMAGE="${BACKEND_IMAGE:?BACKEND_IMAGE is required}"

echo "Deploying image: ${BACKEND_IMAGE}"
docker compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" pull backend
docker compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" up -d --remove-orphans

echo "Waiting for health..."
for i in $(seq 1 30); do
  if curl -sf -u "actuator:${ACTUATOR_PASSWORD}" \
    http://localhost:8080/api/actuator/health >/dev/null 2>2&1; then
    echo "Staging health OK"
    exit 0
  fi
  sleep 5
done

echo "Health check failed"
docker compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" logs backend --tail 100
exit 1