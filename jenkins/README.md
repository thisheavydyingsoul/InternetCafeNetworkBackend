# Backend Jenkins pipeline

## Job
Multibranch pipeline, script path: `Jenkinsfile`

## Branches
- CI: feature/*, fix/*, hotfix/*, dev, main + PRs to dev/main
- Deploy: push to `dev` → staging, push to `main` → production

## Images
`ghcr.io/<owner>/internet-cafe-api:sha-<git-commit>`

## Required credentials (Jenkins global)
- ghcr-credentials, ghcr-owner
- github-ssh-deploy-staging, github-ssh-deploy-production
- backend-deploy-path-staging, backend-deploy-path-production

## Parity with GitHub Actions
See `.github/workflows/ci.yml` and `.github/workflows/deploy.yml`.
After Jenkins is stable, disable or remove those workflows.