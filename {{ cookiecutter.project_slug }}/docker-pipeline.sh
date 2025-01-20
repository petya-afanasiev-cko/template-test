#!/bin/sh

set -e

docker build --build-arg CODEARTIFACT_AUTH_TOKEN=${CODEARTIFACT_AUTH_TOKEN} -t {{ cookiecutter.project_slug }} .

docker compose pull

docker compose build --parallel --no-cache

docker compose -f docker-compose.yml -f docker-compose.tests.yml up --build --quiet-pull --exit-code-from {{ cookiecutter.scheme_slug }}-acceptance-tests --attach {{ cookiecutter.scheme_slug }}-acceptance-tests