#!/bin/sh

docker build -t dci-settlement .

docker compose pull

docker compose build --parallel --no-cache

docker compose up --detach --remove-orphans