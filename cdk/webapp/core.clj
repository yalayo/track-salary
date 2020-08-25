(ns webapp.core
  (:require [stedi.cdk.alpha :as cdk]))

(cdk/import [[App Stack] :from "@aws-cdk/core"]
            [[Bucket] :from "@aws-cdk/aws-s3"]
            [[BucketDeployment Source] :from "@aws-cdk/aws-s3-deployment"])

(def app (App))

(def stack (Stack app "x-web-client"))

(def bucket
  (Bucket stack
          "x-web-bucket"
          {:publicReadAccess true
           :websiteIndexDocument "index.html"
           }))

(def deployment
  (BucketDeployment stack
                    "x-deployment"
                    {:sources [(Source/asset "dist/")]
                     :destinationBucket bucket
                     }))

