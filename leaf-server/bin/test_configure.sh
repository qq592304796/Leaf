#!/bin/bash
# 打包前操作 （替换配置）
#curl 	 xxxxxx >xxxx.yml
sed -r 's/^(active: )(\w+)$/\1test/g' src/main/resources/application.yml
