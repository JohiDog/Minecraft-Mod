#!/bin/sh
# Baut die fertige Mod-Jar mit Gradle/Fabric Loom (braucht JDK 17; erstes Mal laedt es Minecraft/Mappings herunter).
set -e
cd "$(dirname "$0")/.."
./gradlew build
cp build/libs/klassen-framework-*.jar dist/ 2>/dev/null || true
ls dist/
