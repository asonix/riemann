(ns riemann.prometheus-test
  (:require [clj-http.client :as client]
            [clojure.test :refer :all]
            [riemann.prometheus :as prometheus]
            [riemann.logging :as logging]
            [riemann.test-utils :refer [with-mock]]))

(logging/init)

(deftest ^:prometheus prometheus-test
  (with-mock [calls client/post]
    (let [d (prometheus/prometheus {:host "localhost"})]

      (testing "an event without tag")
      (d {:host    "testhost"
          :service "testservice"
          :metric  42
          :time    123456789
          :state   "ok"})
      (is (= 1 (count @calls)))
      (is (= (vec (last @calls))
             ["http://localhost:9091/metrics/job/riemann/host/testhost"
              {:body "testservice 42\n"
               :socket-timeout 5000
               :conn-timeout 5000
               :content-type :json
               :throw-entire-message? true}])))))
