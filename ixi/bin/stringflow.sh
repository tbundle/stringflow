#!/bin/sh
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

PRGDIR=`dirname "$PRG"`

IXI_HOME=`cd "$PRGDIR/.." >/dev/null; pwd`
CLASS_PATH="$IXI_HOME"/libs/*:"$IXI_HOME"/conf/
DEPLOY_DIR="$IXI_HOME"/deploy/
IXI_LOG="$IXI_HOME"/logs/sf-console.log
NOW=$(date +'%d-%m-%Y-%H-%M-%S')

#Adding deployed project's classpath to StringFlow classpath
for d in $DEPLOY_DIR*/ ; do
    CLASS_PATH=$CLASS_PATH:"$d":"$d"libs/*:"$d"conf/:"$d"classes/
done

echo "Classpath -> $CLASS_PATH"

#loading JVM_ARGS from jvmargs.sh file
if [ -r "$IXI_HOME/bin/unix/jvmargs.sh" ]; then
  . "$IXI_HOME/bin/unix/jvmargs.sh"
fi

echo "JVM_ARGS -> $JVM_ARGS"

if [ "$1" = "start" ]; then
	eval  java -Dixi.home="\"$IXI_HOME\"" $JVM_ARGS -cp $CLASS_PATH abs.ixi.server.Stringflow >> $IXI_LOG 2>&1 "&"
	echo "Staringflow has started"

elif [ "$1" = "stop" ]; then
	eval  java -Dixi.home="\"$IXI_HOME\"" $JVM_ARGS -cp $CLASS_PATH abs.ixi.server.sys.admin.ServerAdmin stop
#	pgrep -f abs.ixi.server.Stringflow | xargs kill -9
	
	# record what tcp_max_orphans's current value
	original_value=$(cat /proc/sys/net/ipv4/tcp_max_orphans)
	
	#set the tcp_max_orphans to 0 temporarily
	echo 0 > /proc/sys/net/ipv4/tcp_max_orphans

	# watch /var/log/messages
	# it will split out "kernel: TCP: too many of orphaned sockets"
	# it won't take long for the connections to be killed

	# restore the value of tcp_max_orphans whatever it was before. 
	echo $original_value > /proc/sys/net/ipv4/tcp_max_orphans
	echo "Stringflow has stopped"

elif [ "$1" = "restart" ]; then
	pgrep -f abs.ixi.server.StringflowServer | xargs kill -9
        # record what tcp_max_orphans's current value
        original_value=$(cat /proc/sys/net/ipv4/tcp_max_orphans)

        #set the tcp_max_orphans to 0 temporarily
        echo 0 > /proc/sys/net/ipv4/tcp_max_orphans

        # watch /var/log/messages
        # it will split out "kernel: TCP: too many of orphaned sockets"
        # it won't take long for the connections to be killed

        # restore the value of tcp_max_orphans whatever it was before.
        echo $original_value > /proc/sys/net/ipv4/tcp_max_orphans

        eval  java -Dixi.home="\"$IXI_HOME\"" $JVM_ARGS -cp $CLASS_PATH abs.ixi.server.Server >> $IXI_LOG 2>&1 "&"

	echo "StringFlow has restarted"

elif [ "$1" = "show" ]; then
	echo "Executing show command"
	eval  java -Dixi.home="\"$IXI_HOME\"" $JVM_ARGS -cp $CLASS_PATH abs.ixi.server.admin.ServerAdmin "$@"

else
	echo "Usage: stringflow.sh(commands ...)"
	echo "commands:"
ii	echo "start	To start stringslow"
	echo "stop	To stop stringflow"
	echo "restart	To restart stringflow"
fi

exit 1
