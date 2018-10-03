(defproject elog "0.1.0-SNAPSHOT"
  :description " A simple program to generate a Debian-style changelog for Gentoo packages from the Gentoo GitHub repository"
  :url "https://github.com/devurandom/elog"
  :license {:name "GNU General Public License v3.0 or later"
            :url "https://www.gnu.org/licenses/gpl-3.0-standalone.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/data.json "0.2.6"]
                 [environ "1.1.0"]
                 [irresponsible/tentacles "0.6.2"]
                 [clj-time "0.14.4"]
                 [cljstache "2.0.1"]]
  :main ^:skip-aot elog.main
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
