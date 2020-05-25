(ns aws-localstack.s3
  (:require [clojure.java.io          :as io]
            [cognitect.aws.client.api :as aws]))


(def s3 (aws/client {:api               :s3
                     :region            :sa-east-1
                     :endpoint-override {:protocol :http
                                         :hostname "localhost"
                                         :port     4566}}))

(def some-bucket "some-bucket")

(defn not-found? [response]
  (= :cognitect.anomalies/not-found (:cognitect.anomalies/category response)))

(defn -main []
  ; list buckets
  (-> s3
      (aws/invoke {:op :ListBuckets})
      println)

  ; create new bucket, if doesn't exists
  (if (not-found? (aws/invoke s3 {:op :HeadBucket
                                  :request {:Bucket some-bucket}}))
      (-> s3
          (aws/invoke {:op :CreateBucket
                       :request {:Bucket some-bucket}})
          println))

  ; list objects on "some-bucket"
  (-> s3
      (aws/invoke {:op :ListObjects
                   :request {:Bucket some-bucket}})
      :Contents)

  ; upload new file
  (-> s3
      (aws/invoke {:op :PutObject
                   :request {:Bucket some-bucket
                             :Key    "a-cat.jpg"
                             :Body   (io/input-stream (io/resource "a-cat.jpg"))}})
      println)

  ; list objects on "some-bucket" again
  (-> s3
      (aws/invoke {:op :ListObjects
                   :request {:Bucket some-bucket}})
      :Contents
      println)

  ; delete the file
  (-> s3
      (aws/invoke {:op      :DeleteObject
                   :request {:Bucket some-bucket
                             :Key    "a-cat.jpg"}})
      println)

  ; delete the bucket
  (-> s3
      (aws/invoke {:op :DeleteBucket
                   :request {:Bucket some-bucket}})
      println))