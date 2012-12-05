package com.cloudera.examples.hbase.bulkimport;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat;
import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * HBase bulk import example<br>
 * Data preparation MapReduce job driver
 * <ol>
 * <li>args[0]: HDFS input path
 * <li>args[1]: HDFS output path
 * <li>args[2]: HBase table name
 * </ol>
 */
public class Driver {

    public static final String HBASE_TABLE = "hbase.table.name";
    public static final String AVRO_SCHEMA = "avro.output.schema";

    // TODO: avroSchema should be configurable
    private static String avroSchema =
        "{" +
        "\"type\" : \"record\"," +
        "\"name\" : \"r\"," +
        "\"fields\" : [ {" +
        "  \"name\" : \"f1\"," +
        "  \"type\" : \"bytes\"" +
        "}, {" +
        "  \"name\" : \"f2\"," +
        "  \"type\" : \"bytes\"" +
        "} ]" +
        "}";

    public static void main(String[] args) throws Exception {
        String inputDir = args[0];
        String outputDir = args[1];
        String tableName = args[2];

        Configuration conf = new Configuration();
        conf.set(HBASE_TABLE, tableName);
        conf.set(AVRO_SCHEMA, avroSchema);

        // Load hbase-site.xml 
        HBaseConfiguration.addHbaseResources(conf);

        Job job = new Job(conf, "HBase Bulk Import Example");
        job.setJarByClass(HBaseKVMapper.class);
        job.setMapperClass(HBaseKVMapper.class);
        job.setMapOutputKeyClass(ImmutableBytesWritable.class);
        job.setMapOutputValueClass(KeyValue.class);
        job.setInputFormatClass(AvroInputFormat.class);

        HTable hTable = new HTable(tableName);

        // Auto configure partitioner and reducer
        HFileOutputFormat.configureIncrementalLoad(job, hTable);
        FileInputFormat.addInputPath(job, new Path(inputDir));
        FileOutputFormat.setOutputPath(job, new Path(outputDir));

        job.waitForCompletion(true);

        // Load generated HFiles into table
        LoadIncrementalHFiles loader = new LoadIncrementalHFiles(conf);
        loader.doBulkLoad(new Path(outputDir), hTable);
    }

}
