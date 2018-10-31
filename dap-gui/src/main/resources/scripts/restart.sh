#!/bin/sh
    cd "$(dirname "$0")"
	now=`date "+%Y%m%d"`
	export APP_ARGS=()
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
    count=0
    while [ "$#" -ge "1" ];
    do
        let count=count+1
        APP_ARGS[$count]=$1
        new=(${APP_ARGS[*]})
        shift
    done
    echo  ${new[@]}
    exec java -jar dap-restart-2.5.1-SNAPSHOT.jar ${new[@]} >> log/dap_restart_"$now".log &

