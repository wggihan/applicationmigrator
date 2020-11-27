PRGDIR=`dirname "$PRG"`

[ -z "$INSTALLATION_HOME" ] && INSTALLATION_HOME=`cd "$PRGDIR" ; pwd`

MIGRATOR_CLASSPATH=""
for t in "$INSTALLATION_HOME"/*.*
do
    MIGRATOR_CLASSPATH="$MIGRATOR_CLASSPATH":$t
done
MIGRATOR_HOME="$HOME/.migrator";
# ----- Execute The Requested Command -----------------------------------------

$JAVA_HOME/bin/java -classpath "$MIGRATOR_CLASSPATH" -Dmigrator.home="$MIGRATOR_HOME" com.wso2.migrator.Main $*


