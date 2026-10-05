pipeline {
    agent { label 'linux-docker' }

    options {
        timestamps()
        disableConcurrentBuilds()
        skipDefaultCheckout(true)
        buildDiscarder(logRotator(numToKeepStr: '30'))
    }

    environment {
        GHCR_REGISTRY = 'ghcr.io'
        GHCR_OWNER = credentials('ghcr-owner')
        IMAGE_NAME = 'internet-cafe-api'
        JAVA_HOME = tool name: 'jdk-17', type: 'jdk'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.IMAGE_TAG = "sha-${env.GIT_COMMIT}"
                    env.BUILD_CONFIGURATION = (env.BRANCH_NAME == 'main') ? 'production' : 'staging'
                    env.DEPLOY_ENV = (env.BRANCH_NAME == 'dev') ? 'staging'
                                    : (env.BRANCH_NAME == 'main') ? 'production'
                                    : ''
                }
            }
        }

        stage('CI') {
            parallel {
                stage('Unit tests') {
                    steps {
                        sh 'chmod +x ./mvnw'
                        sh './mvnw --batch-mode test'
                    }
                }
                stage('Secret scan') {
                  steps {
                    sh '''
                      set -e

                      if [ "$(git rev-parse --is-shallow-repository)" = "true" ]; then
                        git fetch --unshallow
                      fi

                      docker run --rm \
                        -v "$WORKSPACE:/repo:ro" \
                        -w /repo \
                        ghcr.io/gitleaks/gitleaks:latest \
                        git /repo \
                        --config /repo/.gitleaks.toml \
                        --verbose \
                        --redact
                    '''
                  }
                }
            }
        }

        stage('Build JAR') {
            steps {
                sh './mvnw --batch-mode package -DskipTests'
            }
        }

        stage('Integration & Docker') {
            stages {
                stage('Integration tests') {
                    steps {
                        sh './mvnw --batch-mode verify -Pintegration'
                    }
                }

                stage('Docker build') {
                    steps {
                        sh '''
                            set -eu

                            docker build \
                              -t "${GHCR_REGISTRY}/${GHCR_OWNER}/${IMAGE_NAME}:${IMAGE_TAG}" .
                        '''
                    }
                }

                stage('Docker push') {
                    when {
                        allOf {
                            not { changeRequest() }
                            anyOf {
                                branch 'dev'
                                branch 'main'
                            }
                        }
                    }

                    steps {
                        withCredentials([usernamePassword(
                            credentialsId: 'ghcr-credentials',
                            usernameVariable: 'GHCR_USER',
                            passwordVariable: 'GHCR_TOKEN'
                        )]) {
                            sh '''
                                set -eu

                                printf '%s\\n' "$GHCR_TOKEN" |
                                  docker login "$GHCR_REGISTRY" \
                                    -u "$GHCR_USER" --password-stdin

                                docker push \
                                  "${GHCR_REGISTRY}/${GHCR_OWNER}/${IMAGE_NAME}:${IMAGE_TAG}"
                            '''
                        }
                    }
                }
            }
        }

        stage('Deploy') {
            when {
                allOf {
                    not { changeRequest() }
                    anyOf {
                        branch 'dev'
                        branch 'main'
                    }
                }
            }

            steps {
                script {
                    def credId = env.DEPLOY_ENV == 'production'
                        ? 'github-ssh-deploy-production'
                        : 'github-ssh-deploy-staging'

                    def deployPathCred = env.DEPLOY_ENV == 'production'
                        ? 'backend-deploy-path-production'
                        : 'backend-deploy-path-staging'

                    def backendImage =
                        "${env.GHCR_REGISTRY}/${env.GHCR_OWNER}/${env.IMAGE_NAME}:${env.IMAGE_TAG}"

                    def composeFile =
                        "docker/docker-compose.${env.DEPLOY_ENV}.yml"

                    withEnv([
                        "DEPLOY_HOST=${getDeployHost(env.DEPLOY_ENV)}",
                        "BACKEND_IMAGE=${backendImage}",
                        "COMPOSE_FILE=${composeFile}"
                    ]) {
                        withCredentials([
                            sshUserPrivateKey(
                                credentialsId: credId,
                                keyFileVariable: 'SSH_KEY',
                                usernameVariable: 'SSH_USER'
                            ),
                            string(
                                credentialsId: deployPathCred,
                                variable: 'DEPLOY_PATH'
                            ),
                            usernamePassword(
                                credentialsId: 'ghcr-credentials',
                                usernameVariable: 'GHCR_USER',
                                passwordVariable: 'GHCR_TOKEN'
                            )
                        ]) {
                            sh '''#!/usr/bin/env bash
        set +x
        set -euo pipefail

        {
            printf '%s\\n' 'set +x' 'set -euo pipefail'

            for name in \
                DEPLOY_PATH \
                COMPOSE_FILE \
                BACKEND_IMAGE \
                GHCR_USER \
                GHCR_TOKEN \
                BRANCH_NAME \
                GIT_COMMIT \
                DEPLOY_ENV
            do
                printf 'export %s=%q\\n' "$name" "${!name}"
            done

            cat <<'REMOTE'
        export GIT_TERMINAL_PROMPT=0

        cd "$DEPLOY_PATH"

        git fetch origin "$BRANCH_NAME" </dev/null
        git checkout --detach "$GIT_COMMIT" </dev/null

        bash "deploy/scripts/deploy-${DEPLOY_ENV}.sh" </dev/null

        set -a
        source "$DEPLOY_PATH/docker/.env"
        set +a

        : "${ACTUATOR_PASSWORD:?ACTUATOR_PASSWORD is required}"

        curl \
            --fail \
            --show-error \
            --silent \
            --retry 5 \
            --retry-delay 5 \
            --retry-connrefused \
            --connect-timeout 10 \
            --max-time 30 \
            -u "actuator:${ACTUATOR_PASSWORD}" \
            http://localhost:8080/api/actuator/health

        printf '\\nBackend smoke OK\\n'
        REMOTE
        } | ssh \
            -o BatchMode=yes \
            -o StrictHostKeyChecking=yes \
            -o IdentitiesOnly=yes \
            -i "$SSH_KEY" \
            "${SSH_USER}@${DEPLOY_HOST}" \
            'bash -s'
        '''
                        }
                    }
                }
            }
        }
    }

    post {
        failure {
            echo 'Backend pipeline failed.'
        }
    }
}

def getDeployHost(String envName) {
    return '72.56.236.45'
}