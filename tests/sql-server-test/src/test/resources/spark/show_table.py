import sys
from pyspark.sql import SparkSession
from pyspark.sql import SQLContext

spark = SparkSession.builder \
    .master('local') \
    .appName('tableshower') \
    .getOrCreate()

sc = spark.sparkContext
sqlContext = SQLContext(sc)

df = sqlContext.read.parquet(sys.argv[1])
df.show()
