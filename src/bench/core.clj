(ns bench.core
  (:gen-class)
  (:require [co.paralleluniverse.pulsar.core :as p])
  (:import (co.paralleluniverse.fibers Fiber)
           (java.util HashMap)))

(set! *warn-on-reflection* true)

(def key-range 100)
(def thread-count 300)
(def repeat-num 100000)

(def r (ref {}))

(p/defsfn ref-calc []
          (dotimes [_ repeat-num]
            (let [k (keyword (str "key" (rand-int key-range)))]
              (dosync
                (alter r update-in [k] (fn [v] (if v (inc v) 1)))))))

(defn ref-bench []
  (time (let [ts (doall (repeatedly thread-count
                                    #(p/fiber ref-calc)))]
          (doseq [^Fiber t ts]
            (.start t))
          (doseq [^Fiber t ts]
            (.join t))))
  )

(def a (atom {}))

(p/defsfn atom-calc []
          (dotimes [_ repeat-num]
            (let [k (keyword (str "key" (rand-int key-range)))]
              (swap! a update-in [k] (fn [v] (if v (inc v) 1))))))

(defn atom-bench []
  (time (let [ts (doall (repeatedly thread-count
                                    #(p/fiber atom-calc)))]
          (doseq [^Fiber t ts]
            (.start t))
          (doseq [^Fiber t ts]
            (.join t))))
  )

(def ^HashMap h (HashMap.))

(p/defsfn hash-calc []
          (dotimes [_ repeat-num]
            (let [k (keyword (str "key" (rand-int key-range)))]
              (locking h
                (let [v (.get h k)]
                  (.put h k (if v (inc v) 1)))))))

(defn hash-bench []
  (time (let [ts (doall (repeatedly thread-count
                                    #(p/fiber hash-calc)))]
          (doseq [^Fiber t ts]
            (.start t))
          (doseq [^Fiber t ts]
            (.join t))))
  )
