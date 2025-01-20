#!/bin/sh

docker build -t {{ cookiecutter.project_slug }} .

docker compose pull

docker compose build --parallel --no-cache

docker compose up --detach --remove-orphans