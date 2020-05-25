(ns aws-localstack.cloud_formation
  (:require [camel-snake-kebab.core   :refer [->PascalCaseString]]
            [clojure.data.json        :as json]
            [cognitect.aws.client.api :as aws]))

(def cloud-formation (aws/client {:api               :cloudformation
                                  :region            :sa-east-1
                                  :endpoint-override {:protocol :http
                                                      :hostname "localhost"
                                                      :port     4566}}))

(def s3-stack
  (-> {:resources {:some-bucket {:type "AWS::S3::Bucket"
                                 :name :some-bucket
                                 :tags [{:key "environment" :value "dev"}
                                        {:key "local-stack" :value "true"}]}}}
      (assoc :aws-template-format-version "2010-09-09")
      (assoc :description "I'm a CloudStack template...")))

(defn ->json [template]
  (json/write-str template :key-fn #(->PascalCaseString %)))

(defn -main []
  ; create stack template
  (-> cloud-formation
      (aws/invoke {:op :CreateStack
                   :request {:StackName    "localstack-dev-some-bucket"
                             :TemplateBody (->json s3-stack)}})))