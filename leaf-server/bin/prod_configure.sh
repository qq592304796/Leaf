#!/bin/bash
# 打包前操作 （替换配置）
#curl 	 xxxxxx >xxxx.yml
sed -r -n -i 's/(active: )(\w+)/\1prod/g' leaf-server/src/main/resources/application.yml
