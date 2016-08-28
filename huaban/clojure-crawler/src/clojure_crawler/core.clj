(ns clojure-crawler.core
  (:require [org.httpkit.client :as http]
            [clojure.java.io :as io]
            [clojure.data.json :as json])
  (:gen-class))

(def image-save-path "./image/")

(defn str2json
  "将json字符串转换为字典"
  [body]
  (json/read-str body :key-fn keyword))

(defn get-home-page
  "请求首页内容"
  []
  (let [options {:timeout 1000
                 :as :text}
        url "http://huaban.com/favorite/beauty/"
        {:keys [body]} @(http/get url)]
    body))

(defn get-image-info
  "从首页内容得到图片信息"
  [page]
  (subs (re-find (re-matcher #"page\[\"pins\"\].*;" page)) 15))

(defn download-image
  "将图片保存在本地"
  [url file-name]
  (println (str "download " url))
  (with-open [in (io/input-stream url)
              out (io/output-stream file-name)]
    (io/copy in out)))

(defn make-image-url
  "根据key值得到图片真实地址"
  [k]
  (str "http://hbimg.b0.upaiyun.com/" k "_fw658"))

(defn make-file-name
  [file-name]
  (str image-save-path file-name ".jpg"))

(defn download-images
  "根据json字符串下载图片"
  [jsons]
  (dorun (map (fn [a]
                (let [k (:key (:file a)) file-name (:pin_id a)]
                  (download-image (make-image-url k) (make-file-name file-name))))
              jsons))
  (:pin_id (last jsons)))

(defn download-home-page
  "下载首页图片"
  []
  (-> (get-home-page)
      get-image-info
      str2json
      download-images))

(defn make-json-request-url
  "根据pin值创建Ajax请求url"
  [pin]
  (str "http://huaban.com/favorite/beauty/?is07k6hx&max=" pin "&limit=20&wfl=1"))

(defn get-more-page
  "加载Ajax请求"
  [last-pin]
  (let [options {:timeout 1000
                 :as :text
                 :headers {"Accept" "application/json"
                           "X-Requested-With" "XMLHttpRequest"
                           "X-Request" "JSON"}}
        {:keys [body]} @(http/get (make-json-request-url last-pin) options)]
    body))

(defn download-more
  "下载通过Ajax异步加载的图片"
  [last-pin]
  (-> last-pin
      get-more-page
      str2json
      second
      second
      download-images))

(defn main
  "下载指定页数的图片，如果不指定页数，下载首页图片"
  ([]
   (download-home-page)
   (println "Finished"))
  ([page-num]
   (if (> 1 page-num)
     (download-home-page)
     (letfn [(down-more
               [pin n]
               (if (zero? n)
                 (println "Finished!")
                 (recur (download-more pin) (dec n))))]
       (down-more (download-home-page) (- page-num 1))))))

(defn -main
  "下载花瓣网美女标签下的图片"
  [& args]
  (if (nil? args)
    (main)
    (main (read-string (first args)))))
