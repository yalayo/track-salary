(ns webapp.test-runner
  (:require  [webapp.all-tests]
             [clojure.spec.test.alpha :as st]
             [figwheel.main.testing :refer [run-tests-async]]))

(defn -main [& args]
  (st/instrument)
  (run-tests-async 5000))
