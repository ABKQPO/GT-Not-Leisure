#!/usr/bin/env bash
# Starts the dedicated server once, stops it after it finished loading and fails on a load error or a changed
# compat snapshot. Usage: server-check.sh <gradle run task> <run directory>
set -uo pipefail

TASK="${1:-runServer25}"
RUN_DIR="${2:-run/server_new}"
LOG="build/server-check.log"
SNAPSHOT="$PWD/build/compat-snapshot.txt"
BASELINE="src/test/resources/compat/compat-snapshot.txt"

mkdir -p "$RUN_DIR" build
echo "eula=true" > "$RUN_DIR/eula.txt"
rm -f "$LOG" "$SNAPSHOT"
touch "$LOG"

(
  for _ in $(seq 1 360); do
    if grep -q 'Done (' "$LOG"; then sleep 15; echo stop; break; fi
    if grep -q -e 'BUILD FAILED' -e 'BUILD SUCCESSFUL' -e 'Crash report saved' -e 'Caught exception from' "$LOG"; then
      echo stop
      break
    fi
    sleep 5
  done
  sleep 30
) | GTNL_COMPAT_SNAPSHOT="$SNAPSHOT" timeout 2400 ./gradlew --no-daemon "$TASK" > "$LOG" 2>&1

failed=0
if ! grep -q 'Done (' "$LOG"; then
  echo "::error::The dedicated server did not finish loading"
  failed=1
fi
if grep -q -e 'Caught exception from sciencenotleisure' -e 'GTNL content loader .* failed' -e 'GTNL compat for .* failed' "$LOG"; then
  echo "::error::GTNL threw while loading"
  grep -n -A20 -e 'Caught exception from sciencenotleisure' -e 'GTNL content loader' "$LOG" | head -80
  failed=1
fi
if [[ ! -f "$SNAPSHOT" ]]; then
  echo "::error::No compat snapshot was written"
  failed=1
elif ! diff <(tr -d '\r' < "$BASELINE") <(tr -d '\r' < "$SNAPSHOT"); then
  echo "::error::Names that saves depend on changed. If the change is intended, update $BASELINE from build/compat-snapshot.txt in the same PR"
  failed=1
fi
tail -40 "$LOG"
exit $failed
