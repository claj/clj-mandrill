(defproject clj-mandrill "0.1.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                  [clj-http "1.1.0"]
                  [cheshire "5.4.0"]]
  :profiles {:dev {:dependencies [[clj-http-fake "1.0.1"]]}})
