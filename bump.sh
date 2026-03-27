#!/bin/bash
set -euo pipefail

JAVA_VERSION="25"
GRADLE_VERSION="9.4.0"
MINECRAFT_VERSION="26.1"
MOD_VERSION="1.1.7"

LOADER_VERSION="0.18.4"
LOOM_VERSION="1.15-SNAPSHOT"
FABRIC_VERSION="0.144.0+26.1"
GSON_VERSION="2.13.2"
CLOTH_CONFIG_VERSION="26.1.154"
MOD_MENU_VERSION="18.0.0-alpha.8"

BUILD_GRADLE="$(pwd)/build.gradle"
GRADLE_PROPERTIES="$(pwd)/gradle.properties"
GRADLE_WRAPPER_PROPERTIES="$(pwd)/gradle/wrapper/gradle-wrapper.properties"
FABRIC_MOD_JSON="$(pwd)/src/main/resources/fabric.mod.json"

update_gradle_prop() {
  sed -i "s|^\([[:space:]]*$1[[:space:]]*=[[:space:]]*\).*|\1$2|" "$GRADLE_PROPERTIES"
  echo "  $1 = $2"
}

update_json_depends() {
  local tmp=$(jq --arg k "$1" --arg v "$2" '.depends[$k] = $v' "$FABRIC_MOD_JSON")
  echo "$tmp" > "$FABRIC_MOD_JSON"
  echo "  depends.$1 = $2"
}

echo "[gradle.properties]"
update_gradle_prop "mod_version" "$MOD_VERSION"
update_gradle_prop "minecraft_version" "$MINECRAFT_VERSION"
update_gradle_prop "loader_version" "$LOADER_VERSION"
update_gradle_prop "loom_version" "$LOOM_VERSION"
update_gradle_prop "fabric_version" "$FABRIC_VERSION"
update_gradle_prop "gson_version" "$GSON_VERSION"
update_gradle_prop "cloth_config_version" "$CLOTH_CONFIG_VERSION"
update_gradle_prop "mod_menu_version" "$MOD_MENU_VERSION"

echo "[build.gradle]"
sed -i "s|it\.options\.release = [0-9]\+|it.options.release = ${JAVA_VERSION}|g" "$BUILD_GRADLE"
sed -i "s|JavaVersion\.VERSION_[0-9_A-Z]\+|JavaVersion.VERSION_${JAVA_VERSION}|g" "$BUILD_GRADLE"
echo "  JAVA_VERSION = $JAVA_VERSION"

echo "[gradle-wrapper.properties]"
sed -i "s|^\(distributionUrl=.*gradle-\)[0-9][0-9.]*\(-.*\)|\1${GRADLE_VERSION}\2|" "$GRADLE_WRAPPER_PROPERTIES"
echo "  GRADLE_VERSION = $GRADLE_VERSION"

echo "[fabric.mod.json]"
update_json_depends "fabricloader" ">=$LOADER_VERSION"
update_json_depends "minecraft" "~$MINECRAFT_VERSION"
update_json_depends "java" ">=$JAVA_VERSION"

./refresh.sh
