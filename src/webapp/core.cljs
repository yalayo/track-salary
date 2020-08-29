(ns ^:figwheel-hooks webapp.core
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [cljs.core.async :refer [put! chan <! >!]]
   [tubax.core :refer [xml->clj]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Let's do this!"}))

;; temp file uploading code
;; will be moved later to other files

;; atom to store file contents
;;(def file-data (atom ""))

;; transducer to stick on a core-async channel to manipulate all the weird javascript
;; event objects --- basically just takes the array of file objects or something
;; that the incomprehensible browser API creates and grabs the first one, then resets things.
(def first-file
  (map (fn [e]
         (let [target (.-currentTarget e)
               file (-> target .-files (aget 0))]
           (set! (.-value target) "")
           file))))

;; transducer to get text out of file object.
(def extract-result
  (map #(-> % .-target .-result js->clj)))

;; two core.async channels to take file array and then file and apply above transducers to them.
(def upload-reqs (chan 1 first-file))
(def file-reads (chan 1 extract-result))

;; function to call when a file event appears: stick it on the upload-reqs channel (which will use the transducer to grab the first file)
(defn put-upload [e]
  (put! upload-reqs e))

(defn process-file [e]
  (put-upload e)
  (re-frame/dispatch [:process-file])) ;; domino 1, dispatching the event

;; sit around in a loop waiting for a file to appear in the upload-reqs channel, read any such file, and when the read is successful, stick the file on the file-reads channel.
(go-loop []
  (let [reader (js/FileReader.)
        file (<! upload-reqs)]
    (set! (.-onload reader) #(put! file-reads %))
    (.readAsText reader file)
    (recur)))

;; sit around in a loop waiting for a string to appear in the file-reads channel and put it in the state atom to be read by reagent and rendered on the page.
(go-loop []
  (reset! app-state (<! file-reads))
  (recur))

(defn show-data [data]
  (if (clojure.string/blank? data) "" (xml->clj data)))

;; reframe
;; domino 2, registering event handling
(re-frame/reg-event-fx
 :process-file
 (fn [{:keys [db]} _]
   {:db (assoc db :payment (show-data @app-state))}))

;; domino 4, registering subscription
(re-frame/reg-sub
 :payment
 (fn [db _]
   (db :payment)))


;; input component to allow users to upload file.
(defn input-component []
  [:input {:type "file" :id "file" :accept ".xml" :name "file" :on-change process-file}])

;; ------------------------- 
;; Views

(defn home-page []
  (let [data (re-frame/subscribe [:payment])] ;; domino 5, subscribe
  [:div
   [:h2 "Welcome to Reagent"]
   [input-component]
   [:p @data]
   ]))

;; end 


(defn get-app-element []
  (gdom/getElement "app"))

(defn main-component []
  [:div
   [:h1 (:text @app-state)]
   [home-page]])

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

