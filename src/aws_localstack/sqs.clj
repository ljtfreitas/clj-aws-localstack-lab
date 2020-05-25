(ns aws-localstack.sqs
  (:require [cognitect.aws.client.api :as aws]))

(def sqs (aws/client {:api               :sqs
                      :region            :sa-east-1
                      :endpoint-override {:protocol :http
                                          :hostname "localhost"
                                          :port     4566}}))

(def some-queue "some-queue")

(defn queue-url [sqs queue-name]

  (-> sqs
      (aws/invoke {:op      :GetQueueUrl
                   :request {:QueueName queue-name}})
      :QueueUrl))

(defn -main []
  ; list queues
  (-> sqs
      (aws/invoke {:op :ListQueues})
      println)

  ; create new queue
  (-> sqs
      (aws/invoke {:op      :CreateQueue
                   :request {:QueueName some-queue}})
      println)

  ; send a message
  (-> sqs
      (queue-url some-queue)
      (#(aws/invoke sqs {:op      :SendMessage
                         :request {:QueueUrl    %
                                   :MessageBody "hello, i'm a message..."}}))
      println)

  ; receive message
  (-> sqs
      (queue-url some-queue)
      (#(aws/invoke sqs {:op      :ReceiveMessage
                         :request {:QueueUrl    %}}))
      :Messages
      (#(map :Body %))
      println))