#
echo Starting Oracle...
export ORACLE_HOME=/u01/app/oracle/product/11.2.0/xe
export PATH=$ORACLE_HOME/bin:$PATH
export ORACLE_SID=XE

status="1"
while [ "$status" != "0" ]; do
  sleep 1
  sqlplus /nolog @/init/oracle/has-oracle-started.sql
  status=$?
done

echo exit | sqlplus system/oracle@xe @/init/oracle/oracle-ddl.sql
