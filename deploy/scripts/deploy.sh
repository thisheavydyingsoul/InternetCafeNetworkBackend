set -euo pipefail
ENV_FILE="docker/.env"
HEALTH_URL="${HEALTH_URL:-http://localhost:8080/api/actuator/health}"

: "${DEPLOY_PATH:?DEPLOY_PATH is required}"
: "${COMPOSE_FILE:?COMPOSE_FILE is required}"
: "${GHCR_TOKEN:?GHCR_TOKEN is required}"
: "${GHCR_USER:?GHCR_USER is required}"
: "${BACKEND_IMAGE:?BACKEND_IMAGE is required}"

cd "$DEPLOY_PATH"



echo "Logging in to GHCR..."
echo "$GHCR_TOKEN" | docker login ghcr.io -u "$GHCR_USER" --password-stdin

export BACKEND_IMAGE="${BACKEND_IMAGE:?BACKEND_IMAGE is required}"

echo "Deploying image: ${BACKEND_IMAGE}"
docker compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" pull backend
docker compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" up -d --remove-orphans

set -a
source "$ENV_FILE"
set +a

: "${ACTUATOR_PASSWORD:?ACTUATOR_PASSWORD is required}"
echo "Waiting for health..."
for i in $(seq 1 30); do
  if curl -sf -u "actuator:${ACTUATOR_PASSWORD}" \
    "$HEALTH_URL" >/dev/null 2>&1; then
    echo "Health OK"
    exit 0
  fi
  sleep 5
done

echo "Health check failed"
docker compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" logs backend --tail 100
exit 1