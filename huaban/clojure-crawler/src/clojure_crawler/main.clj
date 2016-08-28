(ns clojure-crawler.main
  (:require [org.httpkit.client :as http]
            [clojure.java.io :as io]
            [clojure.data.json :as json])
  (:gen-class))

(def options {:timeout 1000
              :as :text
              :headers {"Accept" "application/json"
                        "X-Requested-With" "XMLHttpRequest"
                        "X-Request" "JSON"}})

(defn make-url
  [pin]
  (str "http://huaban.com/favorite/beauty/?is07k6hx&max=" pin "&limit=20&wfl=1"))

(defn get-page
  [pin]
  (let [options {:timeout 1000
                 :as :text
                 :headers {"Accept" "application/json"
                           "X-Requested-With" "XMLHttpRequest"
                           "X-Request" "JSON"}}
        body @(http/get (make-url pin))]
    ))


(defn str2json
  [body]
  (json/read-str body :key-fn keyword))

(defn download-image
  [url file-name]
  (println (str "download " url))
  (with-open [in (io/input-stream url)
              out (io/output-stream file-name)]
    (io/copy in out)))

(defn make-real-imageurl
  [pin]
  (str "http://hbimg.b0.upaiyun.com/" pin "_fw658"))

(defn make-file-name
  [file-name]
  (str "/tmp/img/" file-name ".jpg"))

(defn download-images
  [content]
  (let [c (second(second content))]
    (dorun
    (map (fn [a]
         (let [k (:key (:file a)) file-name (:pin_id a)]
                 (download-image (make-url k) (make-file-name file-name))))
                    c))))

(defn process
  [body]
  (-> body
      str2json
      download-images))

(defn main
  []
  (http/get "http://huaban.com/favorite/beauty/?is07k6hx&max=824270409&limit=20&wfl=1" options
    (fn [{:keys [status body error]}]
      (if error
        (println "Failed")
        (process body)))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
