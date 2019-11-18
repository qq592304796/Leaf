#!/bin/bash
# 打包前操作 （替换配置）
#curl 	 xxxxxx >xxxx.yml
sed -r -i 's/(active: )(\w+)/\1dev/g' leaf-server/src/main/resources/bootstrap.yml
