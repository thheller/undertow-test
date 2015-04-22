(ns undertow-test.core
  (:require [clojure.java.io :as io])
  (:import [io.undertow Undertow UndertowOptions]
           [org.xnio Options]
           [io.undertow.util Headers]
           [io.undertow.server HttpHandler]
           (java.nio ByteBuffer)))

(def test-body
  (slurp (io/resource "data.txt")))

(def test-body-len
  (count test-body))

(def test-body-buffer
  (doto (ByteBuffer/allocate test-body-len)
    (.put (.getBytes test-body))
    (.flip)))

(defn make-handler []
  (reify
    HttpHandler
    (handleRequest [this exchange]
      (doto (.getResponseHeaders exchange)
        (.put Headers/CONTENT_TYPE "text/plain; charset=utf-8")
        (.put Headers/CONTENT_LENGTH ^long test-body-len))
      (-> exchange
          (.getResponseSender)
          (.send (.duplicate test-body-buffer))))))

(defn start-server [host port]
  (let [server (-> (Undertow/builder)
                   (.addHttpListener port host)
                   (.setBufferSize (* 1024 16))
                   (.setIoThreads (* 2 (.. Runtime getRuntime availableProcessors)))
                   (.setSocketOption Options/BACKLOG (int 10000))
                   (.setServerOption UndertowOptions/ALWAYS_SET_KEEP_ALIVE false)
                   (.setServerOption UndertowOptions/ALWAYS_SET_DATE true)
                   (.setWorkerThreads 200)
                   (.setHandler (make-handler))
                   (.build))]
    (.start server)
    server))

(defn -main [host port]
  (start-server host (Integer/parseInt port)))
