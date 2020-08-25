(ns views.select-file-test
  (:require [views.select-file :refer [select-file-component]]
            [devcards.core :refer-macros [defcard defcard-rg]]))

(defn- show-component []
  [select-file-component])

(defcard-rg show-component
  [:div
   [:h1 [:i "Component"]]
   [show-component]])
