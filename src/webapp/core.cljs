(ns ^:figwheel-hooks webapp.core
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [district0x.re-frame.google-analytics-fx]))

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Let's do this!"}))

(defn get-app-element []
  (gdom/getElement "app"))

(defn main-component []
  [:div
   [:h1 (:text @app-state)]])

(defn mount [el]
  (rdom/render [main-component] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

;; To Disable GA while developing:
(district0x.re-frame.google-analytics-fx/set-enabled! (not goog.DEBUG))
