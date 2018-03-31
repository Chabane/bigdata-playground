from __future__ import print_function

import tweepy
import os

from pyspark.sql import *
from pyspark.sql.types import *
from pyspark.sql.functions import col

from pyspark.ml import Pipeline, PipelineModel
from pyspark.ml.classification import LogisticRegression
from pyspark.ml.feature import HashingTF, Tokenizer, StringIndexer, NGram, IDF

MAX_TWEETS = 50

ACCESS_TOKEN = os.environ['TWITTER_CONSUMER_ACCESS_TOKEN']
ACCESS_SECRET = os.environ['TWITTER_CONSUMER_ACCESS_TOKEN_SECRET']
CONSUMER_KEY = os.environ['TWITTER_CONSUMER_KEY']
CONSUMER_SECRET = os.environ['TWITTER_CONSUMER_SECRET']

def initialize():

    spark = SparkSession \
        .builder \
        .appName("search-flight-spark-ml-model") \
        .getOrCreate()
    sc = spark.sparkContext

    auth = tweepy.OAuthHandler(CONSUMER_KEY, CONSUMER_SECRET)
    auth.set_access_token(ACCESS_TOKEN, ACCESS_SECRET)
    api = tweepy.API(auth)
    important_fields = ['id', 'text', 'user']

    schema = StructType([
        StructField('id', LongType(), False),
        StructField('text', StringType(), False),
        StructField('username', StringType(), False)
    ])

    tweetsDf = spark.createDataFrame(sc.emptyRDD(), schema)

    for tweet in tweepy.Cursor(api.search, q='barajas', rpp=100, lang='en').items(MAX_TWEETS):
        json_tweet = {k: tweet._json[k] for k in important_fields}
        json_tweet['text'] = json_tweet['text'].replace("'", "").replace("\"", "").replace("\n", "")
        tweetDf = spark.createDataFrame([
            (json_tweet['id'], json_tweet['text'], json_tweet['user']['name'])
        ], schema)
        tweetsDf = tweetsDf.union(tweetDf)

    tweets_df_splitted = tweetsDf.randomSplit([0.75, 0.25], MAX_TWEETS)
    training_set = tweets_df_splitted[0]
    test_set = tweets_df_splitted[1]

    username_indexed = StringIndexer(inputCol="username", outputCol="username_indexed")
    tokenizer = Tokenizer(inputCol="text", outputCol="token_raw")
    ngram = NGram(inputCol="token_raw", outputCol="ngram", n=2)
    hashing_tf = HashingTF(inputCol="ngram", outputCol="tf", numFeatures=20)
    idf = IDF(inputCol="tf", outputCol="idf", minDocFreq=2)
    lr = LogisticRegression(featuresCol="idf", labelCol="username_indexed")
    pipeline = Pipeline(stages=[username_indexed, tokenizer, ngram, hashing_tf, idf, lr])

    pipeline_model = pipeline.fit(training_set)
    pipeline_model.write().overwrite().save("tweet_traveling_partners_model")

    tweet_traveling_partners_prediction = pipeline_model.transform(test_set)

    selected = tweet_traveling_partners_prediction.select("username", "text", "probability", "prediction")
    for row in selected.collect():
        print(row)

    spark.stop()

if __name__ == "__main__":
    initialize()
