#!/usr/bin/env bash
# Starts the dedicated server once, stops it after it finished loading and fails on a load error or a changed
# compat snapshot. Usage: server-check.sh <gradle run task> <run directory> [host|library]
set -uo pipefail

TASK="${1:-:runServer25}"
# A bare task name also selects matching subproject tasks. Start exactly one server.
[[ "$TASK" == :* ]] || TASK=":$TASK"
RUN_DIR="${2:-run/server_new}"
MODE="${3:-host}"
LOG="build/server-check.log"
SNAPSHOT="$PWD/build/compat-snapshot.txt"
BASELINE="src/test/resources/compat/compat-snapshot.txt"
case "$MODE" in
  host) ;;
  library)
    LOG="build/library-server-check-${TASK##*:}.log"
    SNAPSHOT="$PWD/build/library-unexpected-host-snapshot.txt"
    ;;
  *) echo "Unknown server check mode: $MODE" >&2; exit 2 ;;
esac

mkdir -p "$RUN_DIR" build
echo "eula=true" > "$RUN_DIR/eula.txt"
# RFG otherwise asks about online-mode before starting a server in a fresh directory. The input pipe below
# only sends stop after startup, so that prompt would deadlock CI. Do not overwrite an existing dev config.
if [[ ! -f "$RUN_DIR/server.properties" ]]; then
  cat > "$RUN_DIR/server.properties" <<'PROPERTIES'
online-mode=true
server-ip=127.0.0.1
server-port=0
PROPERTIES
fi
rm -f "$LOG" "$SNAPSHOT"
touch "$LOG"

(
  for _ in $(seq 1 360); do
    if grep -q 'Done (' "$LOG"; then sleep 15; echo stop; break; fi
    if grep -q -e 'GTNL_SERVER_CHECK_EXIT=' -e 'BUILD FAILED' -e 'BUILD SUCCESSFUL' -e 'Crash report saved' -e 'Caught exception from' "$LOG"; then
      echo stop
      break
    fi
    sleep 5
  done
  sleep 30
) | (
  GTNL_COMPAT_SNAPSHOT="$SNAPSHOT" timeout 2400 ./gradlew --no-daemon "$TASK"
  status=$?
  # Release the input producer even if Gradle exits before printing its usual success/failure banner.
  echo "GTNL_SERVER_CHECK_EXIT=$status"
  exit "$status"
) > "$LOG" 2>&1
gradle_status=${PIPESTATUS[1]}

failed=0
if [[ "$gradle_status" -ne 0 ]]; then
  echo "::error::The server run exited with status $gradle_status (124 means it timed out)"
  failed=1
fi
if grep -q -e 'Crash report saved' -e 'BUILD FAILED' "$LOG"; then
  echo "::error::The server crashed or Gradle failed after it started"
  failed=1
fi
if ! grep -q 'Done (' "$LOG"; then
  echo "::error::The dedicated server did not finish loading"
  failed=1
fi
if grep -q -e 'Caught exception from sciencenotleisure' -e 'Caught exception from gtnllib' -e 'GTNL content loader .* failed' -e 'GTNL compat for .* failed' "$LOG"; then
  echo "::error::GTNL threw while loading"
  grep -n -A20 -e 'Caught exception from sciencenotleisure' -e 'GTNL content loader' "$LOG" | head -80
  failed=1
fi
if ! grep -q 'GTNLLib initialized' "$LOG"; then
  echo "::error::GTNLLib did not initialize"
  failed=1
fi
if [[ "$MODE" == library ]]; then
  if [[ -f "$SNAPSHOT" ]]; then
    echo "::error::The standalone library server unexpectedly loaded GTNL"
    failed=1
  fi
elif [[ ! -f "$SNAPSHOT" ]]; then
  echo "::error::No compat snapshot was written"
  failed=1
elif ! diff <(tr -d '\r' < "$BASELINE") <(tr -d '\r' < "$SNAPSHOT"); then
  echo "::error::Names that saves depend on changed. If the change is intended, update $BASELINE from build/compat-snapshot.txt in the same PR"
  failed=1
fi
tail -40 "$LOG"
exit $failed
