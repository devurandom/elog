; SPDX-License-Identifier: GPL-3.0-or-later
; Copyright (C) 2017-2018  Dennis Schridde

(ns elog.utils
  (:require [clojure.string :as string]))

(defn multiline-str [& strings]
  (string/join "\n" strings))

(defn strip-extension [filename extension]
  (string/replace filename (re-pattern (str extension "$")) ""))

(defn vector-compare [v1 v2]
  "Unlike clojure.core/compare, this one compares length only after comparing the common indices"
  (let [first-try (->> (map compare v1 v2)
                       (reduce (fn [previous comparison]
                                 (cond
                                   (not= previous 0) previous ; keep value, if we already found a difference before
                                   (> comparison 0) 1
                                   (< comparison 0) -1
                                   :else 0))
                               0))]
    (if (not= first-try 0)
      first-try
      (- (count v1) (count v2)))))

(defn vector-compare [v1 v2]
  "Unlike clojure.core/compare, this one compares length only after comparing the common indices"
  (let [first-try (->> (map compare v1 v2)
                       (remove #{0})
                       first)]
    (cond
      (> first-try 0) 1
      (< first-try 0) -1
      :else (- (count v1) (count v2)))))

(defn println-by [ms k]
  (doseq [m ms]
    (-> m k println)))
