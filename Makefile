.PHONY: bump refresh build

JAVA_VERSION := 25
GRADLE_VERSION := 9.5.1
MINECRAFT_VERSION := 26.2
MOD_VERSION := 1.1.8

LOADER_VERSION := 0.19.3
LOOM_VERSION := 1.17-SNAPSHOT
FABRIC_VERSION := 0.152.1+26.2
GSON_VERSION := 2.13.2
CLOTH_CONFIG_VERSION := 26.2.155
MOD_MENU_VERSION := 20.0.0-beta.2

BUILD_GRADLE := $(CURDIR)/build.gradle
GRADLE_PROPERTIES := $(CURDIR)/gradle.properties
GRADLE_WRAPPER_PROPERTIES := $(CURDIR)/gradle/wrapper/gradle-wrapper.properties
FABRIC_MOD_JSON := $(CURDIR)/src/main/resources/fabric.mod.json

bump:
	@echo "[gradle.properties]"
	@sed -i 's|^\([[:space:]]*mod_version[[:space:]]*=[[:space:]]*\).*|\1$(MOD_VERSION)|' "$(GRADLE_PROPERTIES)"
	@echo "  mod_version = $(MOD_VERSION)"
	@sed -i 's|^\([[:space:]]*minecraft_version[[:space:]]*=[[:space:]]*\).*|\1$(MINECRAFT_VERSION)|' "$(GRADLE_PROPERTIES)"
	@echo "  minecraft_version = $(MINECRAFT_VERSION)"
	@sed -i 's|^\([[:space:]]*loader_version[[:space:]]*=[[:space:]]*\).*|\1$(LOADER_VERSION)|' "$(GRADLE_PROPERTIES)"
	@echo "  loader_version = $(LOADER_VERSION)"
	@sed -i 's|^\([[:space:]]*loom_version[[:space:]]*=[[:space:]]*\).*|\1$(LOOM_VERSION)|' "$(GRADLE_PROPERTIES)"
	@echo "  loom_version = $(LOOM_VERSION)"
	@sed -i 's|^\([[:space:]]*fabric_version[[:space:]]*=[[:space:]]*\).*|\1$(FABRIC_VERSION)|' "$(GRADLE_PROPERTIES)"
	@echo "  fabric_version = $(FABRIC_VERSION)"
	@sed -i 's|^\([[:space:]]*gson_version[[:space:]]*=[[:space:]]*\).*|\1$(GSON_VERSION)|' "$(GRADLE_PROPERTIES)"
	@echo "  gson_version = $(GSON_VERSION)"
	@sed -i 's|^\([[:space:]]*cloth_config_version[[:space:]]*=[[:space:]]*\).*|\1$(CLOTH_CONFIG_VERSION)|' "$(GRADLE_PROPERTIES)"
	@echo "  cloth_config_version = $(CLOTH_CONFIG_VERSION)"
	@sed -i 's|^\([[:space:]]*mod_menu_version[[:space:]]*=[[:space:]]*\).*|\1$(MOD_MENU_VERSION)|' "$(GRADLE_PROPERTIES)"
	@echo "  mod_menu_version = $(MOD_MENU_VERSION)"
	@echo "[build.gradle]"
	@sed -i 's|it\.options\.release = [0-9]\+|it.options.release = $(JAVA_VERSION)|g' "$(BUILD_GRADLE)"
	@sed -i 's|JavaVersion\.VERSION_[0-9_A-Z]\+|JavaVersion.VERSION_$(JAVA_VERSION)|g' "$(BUILD_GRADLE)"
	@echo "  JAVA_VERSION = $(JAVA_VERSION)"
	@echo "[gradle-wrapper.properties]"
	@sed -i 's|^\(distributionUrl=.*gradle-\)[0-9][0-9.]*\(-.*\)|\1$(GRADLE_VERSION)\2|' "$(GRADLE_WRAPPER_PROPERTIES)"
	@echo "  GRADLE_VERSION = $(GRADLE_VERSION)"
	@echo "[fabric.mod.json]"
	@jq --arg v ">=$(LOADER_VERSION)" '.depends.fabricloader = $$v' "$(FABRIC_MOD_JSON)" > /tmp/_fab.json && mv /tmp/_fab.json "$(FABRIC_MOD_JSON)"
	@echo "  depends.fabricloader = >=$$LOADER_VERSION"
	@jq --arg v "~$(MINECRAFT_VERSION)" '.depends.minecraft = $$v' "$(FABRIC_MOD_JSON)" > /tmp/_fab.json && mv /tmp/_fab.json "$(FABRIC_MOD_JSON)"
	@echo "  depends.minecraft = ~$(MINECRAFT_VERSION)"
	@jq --arg v ">=$(JAVA_VERSION)" '.depends.java = $$v' "$(FABRIC_MOD_JSON)" > /tmp/_fab.json && mv /tmp/_fab.json "$(FABRIC_MOD_JSON)"
	@echo "  depends.java = >=$$JAVA_VERSION"
	$(MAKE) refresh

refresh:
	rm -rf .gradle .vscode build run
	./gradlew --refresh-dependencies
	./gradlew vscode

build:
	./gradlew build
