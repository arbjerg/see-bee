import sys
import time
from pyspark.sql import SparkSession
from pyspark.sql import SQLContext
from pyspark.sql.utils import AnalysisException

spark = SparkSession.builder \
    .master('local') \
    .appName('tablestreamer') \
    .getOrCreate()

sc = spark.sparkContext
sqlContext = SQLContext(sc)

table = sys.argv[1]

spool = "/spool/%s" % table

print "Streaming %s from %s" % (table, spool)

while True:
    try:
        schema = spark.read.load(spool).schema
        df = spark.readStream.schema(schema).load(spool)
        df.writeStream.format("console").start()
        time.sleep(1000000)  # Just wait here while the stream continues
    except AnalysisException:
        print "No spooled files yet. Sleeping for 10s..."
        time.sleep(10)
