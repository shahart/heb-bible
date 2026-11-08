require 'json'
require 'sinatra'
require_relative 'bible_loader'
require_relative 'pasuk_service'
require_relative 'dilugim_service'

verses = BibleLoader.load
service = PasukService.new(verses)
dilugim_service = DilugimService.new(verses)
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

post '/dilugim' do
  skip_min = Integer(params.fetch('skip_min', '1'), 10)
  skip_max = Integer(params.fetch('skip_max', '100'), 10)
  if skip_min < 1 || skip_max < skip_min
    status 400
    return({ error: 'Invalid skip range' }.to_json)
  end

  dilugim_service.count(request.body.read.force_encoding('UTF-8'), skip_min, skip_max).to_json
rescue ArgumentError
  status 400
  { error: 'Skip range must contain integers' }.to_json
end

not_found do
  { error: 'Not Found' }.to_json
end
