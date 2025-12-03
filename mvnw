#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Maven Wrapper startup script, version 3.2.0
# ----------------------------------------------------------------------------

set -e

MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-"$( cd "$( dirname "$0" )" && pwd )"}"
MAVEN_WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
MAVEN_WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"

if [ -r "$MAVEN_WRAPPER_PROPERTIES" ]; then
  DOWNLOAD_URL="$(cat "$MAVEN_WRAPPER_PROPERTIES" | grep "^distributionUrl" | cut -d'=' -f2-)"
else
  DOWNLOAD_URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.5/apache-maven-3.9.5-bin.zip"
fi

MAVEN_HOME="${MAVEN_PROJECTBASEDIR}/.mvn/wrapper/maven"
MAVEN_BIN="${MAVEN_HOME}/bin/mvn"

if [ ! -x "$MAVEN_BIN" ]; then
  echo "Downloading Maven from: $DOWNLOAD_URL"
  
  if command -v curl > /dev/null; then
    curl -fsSL -o "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven.zip" "$DOWNLOAD_URL"
  elif command -v wget > /dev/null; then
    wget -q -O "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven.zip" "$DOWNLOAD_URL"
  else
    echo "Error: curl or wget required to download Maven"
    exit 1
  fi
  
  echo "Installing Maven..."
  mkdir -p "$MAVEN_HOME"
  unzip -q "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven.zip" -d "$MAVEN_HOME"
  mv "$MAVEN_HOME"/apache-maven-*/* "$MAVEN_HOME"/
  rm -rf "$MAVEN_HOME"/apache-maven-*
  rm "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven.zip"
  chmod +x "$MAVEN_BIN"
fi

exec "$MAVEN_BIN" "$@"
