(defproject bench "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.7.0-alpha4"]
                 [co.paralleluniverse/pulsar "0.6.2"]]
  :java-agents [[co.paralleluniverse/quasar-core "0.6.2"]]
  :aot :all
  :jvm-opts ["-Dco.paralleluniverse.fibers.detectRunawayFibers=false"
;              "-Dco.paralleluniverse.fibers.DefaultFiberPool.parallelism=2"
              "-server" "-XX:+TieredCompilation"]
  )
