#!/bin/sh

docker build -t {{ cookiecutter.scheme_slug }}-settlement .

docker compose pull

docker compose build --parallel --no-cache

docker compose up --detach --remove-orphans