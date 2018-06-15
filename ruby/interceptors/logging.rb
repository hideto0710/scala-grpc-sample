require 'grpc'

class Logging < GRPC::ClientInterceptor
  def request_response(request: nil, call: nil, method: nil, metadata: nil)
    puts "Intercepting request response method #{method}" \
        " for request #{request} with call #{call} and metadata: #{metadata}"
    yield
  end

  def client_streamer(requests: nil, call: nil, method: nil, metadata: nil)
    puts "Intercepting client streamer method #{method}" \
       " for requests #{requests} with call #{call} and metadata: #{metadata}"
    yield
  end

  def server_streamer(request: nil, call: nil, method: nil, metadata: nil)
    puts "Intercepting server streamer method #{method}" \
        " for request #{request} with call #{call} and metadata: #{metadata}"
    yield
  end

  def bidi_streamer(requests: nil, call: nil, method: nil, metadata: nil)
    puts "Intercepting bidi streamer method #{method}" \
        " for requests #{requests} with call #{call} and metadata: #{metadata}"
    yield
  end
end
