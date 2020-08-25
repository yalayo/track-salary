(ns webapp.cards
  (:require [devcards.core]
            [clojure.spec.test.alpha :as st]
            [webapp.all-tests]))

(st/instrument)

(devcards.core/start-devcard-ui!)
