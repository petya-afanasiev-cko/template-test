#!/bin/sh

docker build -t dci-settlement .

docker compose pull

docker compose build --parallel --no-cache

docker compose -f docker-compose.yml -f docker-compose.tests.yml up --build --quiet-pull --exit-code-from dci-acceptance-tests --attach dci-acceptance-tests