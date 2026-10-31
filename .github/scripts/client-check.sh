#!/usr/bin/env bash
set -uo pipefail
mkdir -p build
LOG="build/client-check.log"
REPORT="$PWD/build/client-atlases.txt"
CLIENT_DATA_HOME="$PWD/build/client-xdg-data"
mkdir -p "$CLIENT_DATA_HOME"
rm -f "$LOG" "$REPORT"

# Fresh CI runners lack ~/.local/share, which lwjgl3ify requires for its desktop entry.
GTNL_CLIENT_SMOKE="$REPORT" LIBGL_ALWAYS_SOFTWARE=1 XDG_DATA_HOME="$CLIENT_DATA_HOME" \
  timeout 1200 xvfb-run -a -s '-screen 0 1280x720x24 +extension GLX' \
  ./gradlew --no-daemon --console=plain '-Dorg.gradle.jvmargs=-Xmx2G -XX:MaxMetaspaceSize=512m -Dfile.encoding=UTF-8' \
  :runClient25 > "$LOG" 2>&1
status=$?
tail -50 "$LOG"
if [[ "$status" -ne 0 ]]; then
  echo "::error::Client exited with status $status (124 means it timed out)"
  exit "$status"
fi
if [[ ! -s "$REPORT" ]]; then
  echo "::error::The client exited before producing its main-menu atlas report; inspect $LOG"
  exit 1
fi
node .github/scripts/check-client-resources.cjs "$LOG" "$REPORT" src/test/resources/compat/client-missing-textures.txt
