(ns moyure.core
  (:use 
        [compojure.core]
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
            [sandbar.forms :as forms]
            [moyure.db :as db]))
 
(defn layout [con]

    (html [:html 
             [:head 
                [:title "Organize your meet ups with Compojure - Moyure"]]
             
             [:body 
                 [:h2 "MOYURE"] 
                 (if-let [m (flash-get :user-message)] 
                      [:div m])
                   con]]))



(defn show-all []
   (let [all (db/find)] 
      mapcat #([:tr [:td (:id %)]]) all))

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
    :on-success #(do (db/insert-meet  % )
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

