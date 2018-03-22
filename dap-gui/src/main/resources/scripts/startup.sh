#!/bin/sh
    cd "$(dirname "$0")"
	now=`date "+%Y%m%d"`
    export JRE_HOME=jre
    export R_HOME=R
    export CLASSPATH=.:${CLASSPATH}:${JRE_HOME}/lib:${JRE_HOME}/lib/server:${R_HOME}/library/rJava/jri
    export PATH=${JRE_HOME}/bin:${R_HOME}/bin:${PATH}:${HOME}/bin
    : ${R_JAVA_LD_LIBRARY_PATH=}
    if test -n ""; then
    : ${R_LD_LIBRARY_PATH=${R_HOME}/lib:}
    else
    : ${R_LD_LIBRARY_PATH=${R_HOME}/lib}
    fi
    if test -n "${R_JAVA_LD_LIBRARY_PATH}"; then
      R_LD_LIBRARY_PATH="${R_LD_LIBRARY_PATH}:${R_JAVA_LD_LIBRARY_PATH}"
    fi
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

    exec java -Djava.library.path=${R_HOME}/library/rJava/jri -jar dap-gui-1.0.0.jar >> log/ispc_"$now".log &
