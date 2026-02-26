#!/bin/bash

echo "================================================="
echo "               PiggyMetrics CLI"
echo "================================================="
echo "              ^-----^"
echo "             (  o   o  )"
echo "             (     (..)     )"
echo "              (   (  ----  )   )"
echo "              (    (________)    )"
echo "               (                 )"
echo "                ( (  )       (  ) )"
echo "                 (__(__)_______(__)__)"

echo ""

case "$1" in
  dev)
    echo "Starting development environment..."
    docker compose -f docker-compose.yml -f docker-compose.dev.yml up
    ;;
  down)
    echo "Stopping environment..."
    docker compose down
    ;;
  rebuild)
    echo "Rebuilding containers..."
    docker compose -f docker-compose.yml -f docker-compose.dev.yml up --build
    ;;
  logs)
    echo "Showing logs..."
    docker compose logs -f
    ;;
  clean)
    echo "Stopping and removing volumes..."
    docker compose down -v
    ;;
  *)
    echo "Usage:"
    echo "  ./pig dev       -> Start dev environment"
    echo "  ./pig down      -> Stop containers"
    echo "  ./pig rebuild   -> Rebuild and start"
    echo "  ./pig logs      -> Follow logs"
    echo "  ./pig clean     -> Remove containers and volumes"
    ;;
esac