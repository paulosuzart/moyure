(ns moyure.db)

(def id (ref 0))

(def db
    (ref {}))

(defn nextval []
    (dosync (alter id inc)))

(defn insert-meet 
    "Insert a new meet."
    [d]
    (dosync 
        (let [nid (nextval)]
            (alter db assoc nid (assoc d :id nid)) 
            nid)))
(defn find 
    "If id present, returns the given entry. 
     Otherwise, returnts all entries (the actual map of db)"
    ([] @db)
    ([id] (get @db id))) 
