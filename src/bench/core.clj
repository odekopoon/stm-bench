(ns bench.core
  (:gen-class)
  (:import [java.util HashMap]))

(set! *warn-on-reflection* true)

(def key-range 100)
(def thread-count 300)
(def repeat-num 100000)

(def r (ref {}))

(defn ref-calc []
  (dotimes [_ repeat-num]
    (let [k (keyword (str "key" (rand-int key-range))) ]
      (dosync
        (alter r update-in [k] (fn [v] (if v (inc v) 1)))))))

(defn ref-bench []
  (time (let [ts (doall (repeatedly thread-count
                                    #(Thread. ref-calc)))]
          (doseq [^Thread t ts]
            (.start t))
          (doseq [^Thread t ts]
            (.join t)))))

(def a (atom {}))

(defn atom-calc []
  (dotimes [_ repeat-num]
    (let [k (keyword (str "key" (rand-int key-range))) ]
      (swap! a update-in [k] (fn [v] (if v (inc v) 1))))))

(defn atom-bench []
  (time (let [ts (doall (repeatedly thread-count
                                    #(Thread. atom-calc)))]
          (doseq [^Thread t ts]
            (.start t))
          (doseq [^Thread t ts]
            (.join t)))))

(def ^HashMap h (HashMap.))

(defn hash-calc []
  (dotimes [_ repeat-num]
    (let [k (keyword (str "key" (rand-int key-range))) ]
      (locking h
        (let [v (.get h k)]
          (.put h k (if v (inc v) 1)))))))

(defn hash-bench []
  (time (let [ts (doall (repeatedly thread-count
                                    #(Thread. hash-calc)))]
          (doseq [^Thread t ts]
            (.start t))
          (doseq [^Thread t ts]
            (.join t)))))
