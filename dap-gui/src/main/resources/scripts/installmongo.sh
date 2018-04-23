#!/bin/sh
    cd "$(dirname "$0")"
    now=`date "+%Y%m%d"`
    export MONGO_PORT=27017
    usage="Usage: `basename $0` (start|stop)"
	command=$1
    function start() {
        if [ ! -d ./data ]
	    then
	     mkdir ./data
	    fi
	    if [ ! -d ./data/db ]
	    then
	     mkdir ./data/db
	    fi
	    ###########Mongodb process  check #############
	    mongodThread=`lsof -i tcp:$MONGO_PORT|grep mongod|wc -l`
	    if [ $mongodThread -eq  0 ]
	      then
	      exec ./mongodb/bin/./mongod --port $MONGO_PORT --dbpath ./data/db -logpath ./log/mongo_$now.log -logappend -fork
	    fi
	}
	function stop() {
        mongod_port=`ps -ef | grep mongod | grep -v "grep" | awk '{if (27017==$10) print $2}'`
        echo "mongod_port:"$mongod_port

	    if [  -n  "$mongod_port"  ];  then
	        kill  -9  $mongod_port;
	        echo "kill success"
	    fi
	}
	case $command in
	  (start)
	     start
	     ;;
	  (stop)
	     stop
	     ;;
	  (*)
	     echo "Error command"
	     echo "$usage"
	     ;;
	esac