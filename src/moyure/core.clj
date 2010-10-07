(ns moyure.core
  (:use compojure.core hiccup.core ring.adapter.jetty)
  (:require [compojure.route :as route]))

(defn page [con]

    (html [:html 
             [:head 
                [:title "Organize your meet ups with Compojure - Moyure"]]
             
             [:body con]]))
(def hel 
    (page [:span [:b "Hello"]]))

(defroutes hello
    (GET "/" [] hel)
    (route/not-found "Page not found dude!"))

(defn run[]
    (run-jetty hello {:port 8080}))
