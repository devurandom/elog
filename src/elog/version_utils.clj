; SPDX-License-Identifier: GPL-3.0-or-later
; Copyright (C) 2017-2018  Dennis Schridde

(ns elog.version-utils
  (:require [clojure.string :as string]
            [elog.utils :as utils]
            [elog.gentoo :as gentoo]))

(def safe-bigdec (fnil bigdec 0))

(defn update-all [m & {:as kfs}]
  (reduce (fn [acc [k f]]
            (update acc k f))
          m
          kfs))

(defn ->version [version-string]
  (-> (zipmap [:_ :base :_ :subversion :_ :qualifier :qualifier-version :_ :revision]
              (re-find gentoo/pkg-version-regex version-string))
      (dissoc :_)
      (update-all
        :base #(->> (string/split % #"\.")
                    (mapv safe-bigdec))
        :qualifier-version safe-bigdec
        :revision safe-bigdec)))

(defn qualifier->number [q]
  (case q
    "alpha" -4
    "beta" -3
    "pre" -2
    "rc" -1
    nil 0
    "p" 1))

(defn compare-by
  ([key-fn] (compare-by key-fn compare))
  ([key-fn base-comparator]
   (fn [a b]
     (base-comparator (key-fn a) (key-fn b)))))

(def qualifier-compare (compare-by qualifier->number))

(defn compare-in-order [& comparators]
  (fn [a b]
    (or
      (->> comparators
           (map #(% a b))
           (remove #{0})
           first)
      0)))

(def version-compare
  (compare-in-order
    (compare-by :base utils/vector-compare)
    (compare-by :subversion)
    (compare-by :qualifier qualifier-compare)
    (compare-by :qualifier-version)
    (compare-by :revision)))

(defn ebuild-filename->pkg-version [filename]
  (-> (re-find gentoo/ebuild-filename-version-regex filename)
      first
      (utils/strip-extension ".ebuild")))
