(ns moyure.core
  (:use [compojure.core]
        [hiccup.core :only [html]]
        [hiccup.page-helpers :only [doctype link-to]]
        [ring.adapter.jetty :only [run-jetty]]
        [sandbar.core]
        [sandbar.stateful-session :only [flash-put! flash-get
                                         wrap-stateful-session]]
        [sandbar.validation :only [add-validation-error
                                   build-validator
                                   non-empty-string]])
  (:require [compojure.route :as route]
            [sandbar.forms :as forms]))
 
(defn layout [con]

    (html [:html 
             [:head 
                [:title "Organize your meet ups with Compojure - Moyure"]]
             
             [:body 
                 [:h2 "MOYURE"] 
                 (if-let [m (flash-get :user-message)] 
                      [:div m])
                   con]]))

(def db (ref {}))
(defn insert [d] 
    (dosync (alter db assoc (:id d) d)))

(def id (ref 0))
(def nextval (dosync (alter id inc)))
(defn findAll [] @db)


(defn show-all []
   (mapcat #([:tr [:td (:id %)]]) @db))

(defn home [] 
    (layout [:div 
                 [:b "Hello Visitor"]
                 [:p (link-to "/meetup" "New MeetUp")]
                 [:table (show-all)]]))


(def m-label {:title "Title"
              :when "When"
              :subject "Subject"})

(forms/defform meetup-form "/meetup"
    :fields [(forms/hidden :id)
             (forms/textfield :title)
             (forms/textfield :when {:size 10})
             (forms/textarea :subject)]
    :on-cancel "/"
    :on-success #(do (insert (assoc % :id nextval)) (println @db)
                     (flash-put! :user-message "Meet up saved, go tell your friends!")
                     "/")
   ;; :validator #(non-empty-string % :title :when :subject m-label)
    :properties m-label)
 

(defroutes app-routes 
    (GET "/" [] (home))
    (meetup-form (fn [request form] (layout form)))
    (route/not-found (layout [:h2 "Page not found dude!"])))

(def s-routes (-> app-routes wrap-stateful-session))

(defn run[]
    (future (run-jetty (var s-routes) {:port 8080})))

