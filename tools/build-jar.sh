#!/bin/sh
# Baut das fertige Mod-Jar aus src/main/resources (Jar = Zip; braucht kein Gradle/Java-Abhaengigkeiten beim Packen via python3, sonst 'jar').
set -e
cd "$(dirname "$0")/.."
OUT="dist/klassen-framework-1.0.0+1.20.2.jar"
mkdir -p dist
if command -v python3 >/dev/null 2>&1; then
  python3 - "$OUT" <<'PY'
import zipfile, os, sys
src = 'src/main/resources'
with zipfile.ZipFile(sys.argv[1], 'w', zipfile.ZIP_DEFLATED) as z:
    for root, dirs, files in os.walk(src):
        for f in sorted(files):
            full = os.path.join(root, f)
            z.write(full, os.path.relpath(full, src))
print('gebaut:', sys.argv[1])
PY
else
  (cd src/main/resources && jar cf "../../$OUT" .)
fi
