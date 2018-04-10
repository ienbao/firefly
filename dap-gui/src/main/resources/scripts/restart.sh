#!/bin/sh
    cd "$(dirname "$0")"
	now=`date "+%Y%m%d"`
	export APP_JAR=APP
    export JRE_HOME=jre
    export CLASSPATH=.:${CLASSPATH}:${JRE_HOME}/lib:${JRE_HOME}/lib/server
    export PATH=${JRE_HOME}/bin:${PATH}:${HOME}/bin
    if test -z "${DYLD_FALLBACK_LIBRARY_PATH}"; then
      DYLD_FALLBACK_LIBRARY_PATH="${R_LD_LIBRARY_PATH}"
    else
      DYLD_FALLBACK_LIBRARY_PATH="${R_LD_LIBRARY_PATH}:${DYLD_FALLBACK_LIBRARY_PATH}"
    fi
    export DYLD_FALLBACK_LIBRARY_PATH
    if [ ! -d ./log/ ]
    then
     mkdir ./log/
    fi

    exec java -jar ${APP_JAR} >> log/dap_restart_"$now".log &

