REGISTER /usr/lib/pig/piggybank.jar
REGISTER /usr/lib/pig/lib/avro-1.7.1.cloudera.2.jar
REGISTER /usr/lib/pig/lib/jackson-core-asl-1.8.8.jar
REGISTER /usr/lib/pig/lib/jackson-mapper-asl-1.8.8.jar
REGISTER /usr/lib/pig/lib/json-simple-1.1.jar
REGISTER /usr/lib/pig/lib/snappy-java-1.0.4.1.jar

fs -rm -r -f inputdir outputdir

A = LOAD '1.csv' USING PigStorage(',') AS (f1:bytearray, f2:bytearray);
B = MAPREDUCE 'target/hbase-bulkload-avro-1.0.0-job.jar' STORE A INTO 'inputdir' USING org.apache.pig.piggybank.storage.avro.AvroStorage('schema', '
{
  "type" : "record",
  "name" : "r",
  "fields" : [ {
    "name" : "f1",
    "type" : "bytes"
  }, {
    "name" : "f2",
    "type" : "bytes"
  } ]
}
') LOAD 'outputdir' `com.cloudera.examples.hbase.bulkimport.Driver inputdir outputdir foo`;
DUMP B;
