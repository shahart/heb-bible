require 'json'
require 'sinatra'
require_relative 'bible_loader'
require_relative 'pasuk_service'

verses = BibleLoader.load
service = PasukService.new(verses)
verse_count = verses.length

configure do
  set :bind, '0.0.0.0'
  set :port, ENV.fetch('PORT', '9010').to_i
end

before do
  content_type :json, charset: 'utf-8'
end

get '/psukim' do
  name = params['name']
  if name.nil? || name.empty?
    { count: verse_count }.to_json
  else
    { count: service.count(name, params['containsName'] == 'true') }.to_json
  end

end

get '/psukim/:name' do
  { count: service.count(params['name'], params['containsName'] == 'true') }.to_json
end

not_found do
  { error: 'Not Found' }.to_json
end
