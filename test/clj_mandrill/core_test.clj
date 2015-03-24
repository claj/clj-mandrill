(ns clj-mandrill.core-test
  (:require [clojure.test :refer :all]
            [clj-mandrill.core :refer [mandrill-url
                                       method-url
                                       call-mandrill
                                       send-message
                                       user-info
                                       ping
                                       senders
                                       send-template
                                       *mandrill-api-key*]]
            [clj-http.fake :refer [with-fake-routes]] 
            [cheshire.core :refer []]))


(deftest url-api-keys
  (is (= "https://mandrillapp.com/api/1.0/" mandrill-url))

  (binding [ *mandrill-api-key* "abcdefg" ]
    (is (= "https://mandrillapp.com/api/1.0/" mandrill-url))
    (is (= "https://mandrillapp.com/api/1.0/users/ping.json" (method-url "users/ping")))
    (with-fake-routes
      { "https://mandrillapp.com/api/1.0/messages/send.json"
        (fn [req] (let [ body (slurp (:body req)) ]
                    {:status 200 :headers {"Content-type" "application/json"} :body body }))}

      (let [ resp (call-mandrill "messages/send" {:email_address "bob@test.com"})] ; Note this is not a real api
            (is (= "abcdefg" (:key resp)))
            (is (= "bob@test.com" (:email_address resp)))))))


(deftest should-send-message
  (binding [ *mandrill-api-key* "abcdefg" ]
    (with-fake-routes
      { "https://mandrillapp.com/api/1.0/messages/send.json"
        (fn [req] (let [ body (slurp (:body req)) ]
                    {:status 200 :headers {"Content-type" "application/json"} :body body }))}

      (let [ resp (send-message {:text "Hi" :subject "Just a note" :from_email "alice@test.com" :from_name "Alice"
                                  :to [{:email "bob@test.com" :name "Bob"}]})]
            (is (= "abcdefg" (:key resp)))
            (is (= {:text "Hi" :subject "Just a note" :from_email "alice@test.com" :from_name "Alice"
                                  :to [{:email "bob@test.com" :name "Bob"}]}
                   (:message resp)))))))


(deftest should-send-template
  (binding [ *mandrill-api-key* "abcdefg" ]
    (with-fake-routes
      { "https://mandrillapp.com/api/1.0/messages/send-template.json"
        (fn [req] (let [ body (slurp (:body req)) ]
                    {:status 200 :headers {"Content-type" "application/json"} :body body }))}

      (let [ resp (send-template "verify_email" {:subject "Just a note" :from_email "alice@test.com" :from_name "Alice"
                                  :to [{:email "bob@test.com" :name "Bob"}]})]
            (is (= "abcdefg" (:key resp)))
            (is (= "verify_email" (:template_name resp)))
            (is (= [] (:template_content resp)))

            (is (= {:subject "Just a note" :from_email "alice@test.com" :from_name "Alice"
                                  :to [{:email "bob@test.com" :name "Bob"}]}
                   (:message resp))))


      (let [ resp (send-template "verify_email" {:subject "Just a note" :from_email "alice@test.com" :from_name "Alice"
                                  :to [{:email "bob@test.com" :name "Bob"}]}
                                  [{:name "HEADER" :content "My Header"}])]
            (is (= "abcdefg" (:key resp)))
            (is (= "verify_email" (:template_name resp)))
            (is (= [{:name "HEADER" :content "My Header"}] (:template_content resp)))
            (is (= {:subject "Just a note" :from_email "alice@test.com" :from_name "Alice"
                                  :to [{:email "bob@test.com" :name "Bob"}]}
                   (:message resp)))))))


(deftest should-get-user-info
  (binding [ *mandrill-api-key* "abcdefg" ]
    (with-fake-routes
      { "https://mandrillapp.com/api/1.0/users/info.json"
        (fn [req] (let [ body (slurp (:body req)) ]
                    {:status 200 :headers {"Content-type" "application/json"} :body body }))}

      (let [ resp (user-info)]
            (is (= "abcdefg" (:key resp)))))))

(deftest should-ping
  (binding [ *mandrill-api-key* "abcdefg" ]
    (with-fake-routes
      { "https://mandrillapp.com/api/1.0/users/ping.json"
        (fn [req] (let [ body (slurp (:body req)) ]
                    {:status 200 :headers {"Content-type" "application/json"} :body body }))}

      (let [ resp (ping)]
            (is (= "abcdefg" (:key resp)))))))

(deftest should-fetch-senders
  (binding [ *mandrill-api-key* "abcdefg" ]
    (with-fake-routes
      { "https://mandrillapp.com/api/1.0/users/senders.json"
        (fn [req] (let [ body (slurp (:body req)) ]
                    {:status 200 :headers {"Content-type" "application/json"} :body body }))}

      (let [ resp (senders)]
            (is (= "abcdefg" (:key resp)))))))



