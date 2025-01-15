#!/bin/sh

docker build -t {{ cookiecutter.scheme_slug }}-settlement .

docker compose pull

docker compose build --parallel --no-cache

docker compose -f docker-compose.yml -f docker-compose.tests.yml up --build --quiet-pull --exit-code-from {{ cookiecutter.scheme_slug }}-acceptance-tests --attach {{ cookiecutter.scheme_slug }}-acceptance-tests