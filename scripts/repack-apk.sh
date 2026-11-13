#!/bin/sh
# 去掉 AGP 打包时插入的大段对齐填充，再重新签上 debug 证书。
# 需要 build-tools 目录在 PATH 里（zipalign / apksigner）。
# 用法: scripts/repack-apk.sh [input.apk] [output.apk]
set -e

IN="${1:-app/build/outputs/apk/foss/debug/app-foss-debug.apk}"
OUT="${2:-app/build/outputs/apk/foss/debug/app-foss-debug-slim.apk}"
TMP="$OUT.tmp"
KS="${DEBUG_KEYSTORE:-$HOME/.android/debug.keystore}"

zipalign -f -P 16 4 "$IN" "$TMP"
apksigner sign \
  --ks "$KS" --ks-pass pass:android --key-pass pass:android \
  --ks-key-alias androiddebugkey \
  --v1-signing-enabled false --v2-signing-enabled true \
  --out "$OUT" "$TMP"
apksigner verify "$OUT"
rm -f "$TMP"
echo "$OUT"
