require 'event_store_client'
require "event_store_client/adapters/grpc"

RSpec.describe RubyEvents do
  it "connects to the message store" do

    EventStoreClient.configure do |config|
      config.eventstore_url = "esdb://localhost:2113"
      config.adapter = :grpc
      config.eventstore_user = "admin"
      config.eventstore_password = "changeit"
      config.verify_ssl = false
    end

    event_store = EventStoreClient::Client.new

    events = event_store.read("pharmacy-0987654321")

    events.each { |event| puts event }
  end
end
