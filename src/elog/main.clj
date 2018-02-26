; SPDX-License-Identifier: GPL-3.0-or-later
; Copyright (C) 2017-2018  Dennis Schridde

(ns elog.main
  (require [clojure.string :as string]
           [environ.core :refer [env]]
           [cljstache.core :as stache]
           [tentacles.repos :as gh-repos]
           [clj-time.format :as tf]
           [elog.gentoo :as gentoo]
           [elog.utils :as utils]
           [elog.version-utils :as vutils])
  (:gen-class))

(def log-stache
      (utils/multiline-str
        "{{pkg-spec}} ({{version}}) {{repository}}; urgency={{urgency}}"
        ""
        "{{#message-lines}}"
        "  {{{.}}}"
        "{{/message-lines}}"
        ""
        "  -- {{author.name}} <{{author.email}}>  {{date}}"
        ""))

(defn render-log [commit message date-rfc2822]
  (stache/render
    log-stache
    (assoc commit
      :urgency "medium"
      :date date-rfc2822
      :message-lines (string/split-lines message))))

; https://www.debian.org/doc/debian-policy/ch-source.html#s-dpkgchangelog
(defn format-changelog [{:keys [version message author] :as commit}]
  (let [date-rfc2822 (->> (:date author)
                          (tf/parse)
                          (tf/unparse (tf/formatters :rfc822)))]
    (-> commit
        (dissoc :message)
        (assoc
          :version (vutils/->version version)
          :log (render-log commit message date-rfc2822)))))

(defn condense-commit [commit]
  (let [{:keys [sha] {:keys [author message]} :commit} commit]
    {:sha sha
     :author author
     :message message}))

(defn assoc-versions [commit auth]
  (let [{:keys [pkg-spec]} commit]
    (assoc commit
      :versions (->> (gh-repos/specific-commit gentoo/organisation gentoo/repository (:sha commit) {:auth auth}) ; https://developer.github.com/v3/repos/commits/
                     :files
                     (filter (comp #{"added" "renamed"} :status))
                     (map :filename)
                     (filter #(string/ends-with? % ".ebuild"))
                     (filter #(string/starts-with? % pkg-spec))
                     (mapv vutils/ebuild-filename->pkg-version)))))

(defn expand-key [m k-from k-to]
  (let [stripped-m (dissoc m k-from)]
    (map #(assoc stripped-m k-to %) (k-from m))))

(defn changelog [pkg-spec auth]
  (->> (gh-repos/commits gentoo/organisation gentoo/repository {:path pkg-spec :auth auth}) ; https://developer.github.com/v3/repos/commits/
       (mapcat #(-> %
                    condense-commit
                    (assoc
                      :repository gentoo/repository
                      :pkg-spec pkg-spec)
                    (assoc-versions auth)
                    (expand-key :versions :version)))
       (map format-changelog)))

(defn -main
  [& args]
  (let [auth [(env :elog-username) (env :elog-password)]
        pkg-specs args]
    (dorun
      (map #(-> %
                (changelog auth)
                (utils/println-by :log))
           pkg-specs))))
