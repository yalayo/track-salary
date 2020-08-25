(ns views.select-file
  (:require [re-frame.core :as re-frame]))

(defn select-file-component []
  [:span.upload-label
   [:label
    [:input.hidden-xs-up
     {:type "file" :accept ".xml" :on-change put-upload}]
    [:i.fa.fa-upload.fa-lg]
    (or file-name "Seleccione el/los CFDIs")]
   (when file-name
     [:i.fa.fa-times {:on-click #(reset! app-state {})}])])
