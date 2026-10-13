#!/usr/bin/env sh
set -eu

SCRIPT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
cd "$SCRIPT_DIR"

if [ -z "${JAVA_HOME:-}" ] || [ ! -x "$JAVA_HOME/bin/javac" ]; then
  if command -v javac >/dev/null 2>&1; then
    JAVAC_BIN="$(command -v javac)"
    JAVA_HOME="$(dirname "$(dirname "$JAVAC_BIN")")"
    export JAVA_HOME
  fi
fi

if [ -z "${JAVA_HOME:-}" ] || [ ! -x "$JAVA_HOME/bin/java" ] || [ ! -x "$JAVA_HOME/bin/javac" ]; then
  echo "A Java JDK 17 or newer is required to build the backend. Install a JDK and set JAVA_HOME." >&2
  exit 1
fi

JAVA_VERSION="$("$JAVA_HOME/bin/java" -version 2>&1 | sed -n '1s/.*version "\(.*\)".*/\1/p')"
JAVA_MAJOR="${JAVA_VERSION%%.*}"
if [ "$JAVA_MAJOR" = "1" ]; then
  JAVA_VERSION="${JAVA_VERSION#*.}"
  JAVA_MAJOR="${JAVA_VERSION%%.*}"
fi
case "$JAVA_MAJOR" in
  ''|*[!0-9]*) echo "Unable to determine Java version from $JAVA_HOME/bin/java." >&2; exit 1 ;;
  *) if [ "$JAVA_MAJOR" -lt 17 ]; then echo "Java 17 or newer is required. Found Java $JAVA_MAJOR." >&2; exit 1; fi ;;
esac

if [ ! -f .mvn/wrapper/maven-wrapper.jar ] || [ ! -f .mvn/wrapper/maven-wrapper.properties ]; then
  echo "Maven Wrapper files are missing from .mvn/wrapper." >&2
  exit 1
fi

exec "$JAVA_HOME/bin/java" -classpath .mvn/wrapper/maven-wrapper.jar \
  "-Dmaven.multiModuleProjectDirectory=$SCRIPT_DIR" \
  org.apache.maven.wrapper.MavenWrapperMain "$@"
