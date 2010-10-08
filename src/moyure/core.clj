(ns moyure.core
  (:use 
        [compojure.core]
        [ring.adapter.jetty :only [run-jetty]]
        [ring.middleware.file :only [wrap-file]]
        [sandbar.stateful-session :only [flash-put! 
                                         flash-get
                                         wrap-stateful-session]]
        [hiccup.core :only [html]]
        [hiccup.page-helpers :only [link-to]]
        [sandbar.core :only [stylesheet]])
  (:require [compojure.route :as route]
            [sandbar.forms :as forms]
            [moyure.db :as db]))


(defn layout
    "Acts as a template wrapping all the content (cont) in the
    base structure"
    [con]
    (html [:html 
             [:head 
                [:title "Organize your meet ups with Moyure"]
                (stylesheet "sandbar-forms.css")
                (stylesheet "sandbar.css")]
             [:body 
                 [:h2 "MOYURE"] 
                 (if-let [m (flash-get :user-message)] 
                      [:div m])
                   con]]))

(defn show-all
    "Generates a html snipet for entries"
    [a]
    (for [[k v] a]
        (let [id (:id v)
             title (:title v)] 
             [:tr 
                 [:td id] [:td title] [:td (link-to (str "/meetup/" id) "Edit")]])))

(defn home
    "The welcome screen"
    []
    (layout [:div 
                [:b "Hello Visitor"]
                [:p (link-to "/meetup" "New MeetUp")]
                (if-let [a (db/find)]
                    [:table (show-all a)])]))

(def m-label {:title "Title"
              :when "When"
              :subject "Subject"})

(forms/defform meetup-form "/meetup"
    :fields [(forms/hidden :id)
             (forms/textfield :title)
             (forms/textfield :when {:size 10})
             (forms/textarea :subject)]
    :load #(db/find %)
    :on-cancel "/"
    :on-success #(do (db/insert-meet  % )
                     (flash-put! :user-message [:p "Meet up saved, go tell your friends!"])
                     "/")
    :properties m-label)


(defroutes app-routes 
    (GET "/" [] (home))
    (meetup-form (fn [request form] (layout form)))
    (route/not-found (layout [:h2 "Page not found dude!"])))

(def s-routes (-> app-routes 
                  wrap-stateful-session
                  (wrap-file "public")))

(defn run[]
    (future (run-jetty (var s-routes) {:port 8080})))

