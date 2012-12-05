hbase-bulkload-avro
====================

An example of how to bulk import data from Avro files into a HBase table.

In this example, I do the following:

    1. Load a CSV file with PigStorage and store data as an Avro file using AvroStorage.
    2. Launch a MR job that loads the Avro file and dump HFile
    3. Do bulkload HFile into HBase

CSV file
-------------

    1,2

Avro schema
-------------

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

HBase columns
-------------

    cf:f1, cf:f2

Steps
-------------

    1. service hbase-master start // use hbase in standalone mode
    2. hbase shell
        a. create 'foo', {NAME => 'cf'} 
    3. hadoop fs -copyFromLocal 1.csv
    4. pig test.pig
    5. hbase shell
        a. scan 'foo'
