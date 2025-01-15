FROM node:22-alpine AS build

COPY package.json ./app/
COPY package-lock.json ./app/
WORKDIR /app
RUN npm install
COPY . /app
CMD ["npm", "run", "test"]