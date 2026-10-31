require 'zlib'

class BibleLoader
  def self.load(path = ENV.fetch('BIBLE_GZIP_PATH', File.expand_path('../bible.txt.gz', __dir__)))
    verses = []
    Zlib::GzipReader.open(path) do |file|
      file.each_line do |line|
        line = line.strip
        next if line.empty?

        comma = line.index(',')
        verses << line[(comma + 1)..] if comma
      end
    end
    verses
  end
end
